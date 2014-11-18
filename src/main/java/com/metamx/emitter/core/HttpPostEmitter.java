/*
 * Copyright 2012 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metamx.emitter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.metamx.common.ISE;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import com.metamx.http.client.HttpClient;
import com.metamx.http.client.response.StatusResponseHandler;
import com.metamx.http.client.response.StatusResponseHolder;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class HttpPostEmitter implements Flushable, Closeable, Emitter
{
  private static final int MAX_EVENT_SIZE = 1023 * 1024; // Set max size slightly less than 1M to allow for metadata
  private static final long BUFFER_FULL_WARNING_THROTTLE = 30000;
  private static final byte[] BATCH_START = new byte[]{'['};
  private static final byte[] BATCH_END = new byte[]{']'};
  private static final byte[] MESSAGE_SEPARATOR = new byte[]{','};

  private static final Logger log = new Logger(HttpPostEmitter.class);
  private static final AtomicInteger instanceCounter = new AtomicInteger();

  private final HttpEmitterConfig config;
  private final HttpClient client;
  private final ObjectMapper jsonMapper;
  private final URL url;

  private final AtomicReference<List<byte[]>> eventsList =
      new AtomicReference<List<byte[]>>(Lists.<byte[]>newLinkedList());
  private final AtomicInteger count = new AtomicInteger(0);
  private final AtomicLong bufferedSize = new AtomicLong(0);
  private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat(String.format("HttpPostEmitter-%s-%%s", instanceCounter.incrementAndGet()))
          .build()
  );
  private final AtomicLong version = new AtomicLong(0);
  private final AtomicBoolean started = new AtomicBoolean(false);

  // Trackers for buffer-full warnings. Only use under synchronized(eventsList).
  private long lastBufferFullWarning = 0;
  private long messagesDroppedSinceLastBufferFullWarning = 0;

  public HttpPostEmitter(
      HttpEmitterConfig config,
      HttpClient client
  )
  {
    this(config, client, new ObjectMapper());
  }

  public HttpPostEmitter(
      HttpEmitterConfig config,
      HttpClient client,
      ObjectMapper jsonMapper
  )
  {
    Preconditions.checkArgument(
        config.getMaxBatchSize() >= MAX_EVENT_SIZE + BATCH_START.length + BATCH_END.length,
        "maxBatchSize must be greater than MAX_EVENT_SIZE[%,d] + overhead[%,d].",
        MAX_EVENT_SIZE,
        BATCH_START.length + BATCH_END.length
    );
    Preconditions.checkArgument(
        config.getMaxBufferSize() >= MAX_EVENT_SIZE,
        "maxBufferSize must be greater than MAX_EVENT_SIZE[%,d].",
        MAX_EVENT_SIZE,
        BATCH_START.length + BATCH_END.length
    );
    this.config = config;
    this.client = client;
    this.jsonMapper = jsonMapper;
    try {
      this.url = new URL(config.getRecipientBaseUrl());
    }
    catch (MalformedURLException e) {
      throw new ISE(e, "Bad URL: %s", config.getRecipientBaseUrl());
    }
  }

  @Override
  @LifecycleStart
  public void start()
  {
    synchronized (started) {
      if (!started.getAndSet(true)) {
        exec.schedule(
            new EmittingRunnable(version.get()),
            config.getFlushMillis(),
            TimeUnit.MILLISECONDS
        );
      }
    }
  }

  @Override
  public void emit(Event event)
  {
    synchronized (started) {
      if (!started.get()) {
        throw new RejectedExecutionException("Service is closed.");
      }
    }

    final byte[] eventBytes;
    try {
      eventBytes = jsonMapper.writeValueAsBytes(event);
    }
    catch (IOException e) {
      throw Throwables.propagate(e);
    }

    if (eventBytes.length > MAX_EVENT_SIZE) {
      log.error(
          "Event too large to emit (%,d > %,d): %s ...",
          eventBytes.length,
          MAX_EVENT_SIZE,
          new String(eventBytes, 0, MAX_EVENT_SIZE)
      );
      return;
    }

    synchronized (eventsList) {
      if (bufferedSize.get() + eventBytes.length <= config.getMaxBufferSize()) {
        eventsList.get().add(eventBytes);
        bufferedSize.addAndGet(eventBytes.length);
        if (!event.isSafeToBuffer() || count.incrementAndGet() >= config.getFlushCount()) {
          exec.execute(new EmittingRunnable(version.get()));
        }
      } else {
        messagesDroppedSinceLastBufferFullWarning++;
      }

      final long now = System.currentTimeMillis();
      if (messagesDroppedSinceLastBufferFullWarning > 0 && lastBufferFullWarning + BUFFER_FULL_WARNING_THROTTLE < now) {
        log.error("Buffer full: dropped %,d events!", messagesDroppedSinceLastBufferFullWarning);
        lastBufferFullWarning = now;
        messagesDroppedSinceLastBufferFullWarning = 0;
      }
    }
  }

  @Override
  public void flush() throws IOException
  {
    final CountDownLatch latch = new CountDownLatch(1);

    if (started.get()) {

      final EmittingRunnable emittingRunnable = new EmittingRunnable(version.get());
      exec.execute(
          new Runnable()
          {
            @Override
            public void run()
            {
              try {
                emittingRunnable.run();
              }
              finally {
                log.debug("Counting down");
                latch.countDown();
              }
            }
          }
      );

      try {
        latch.await();
        log.debug("Awaited Latch");
      }
      catch (InterruptedException e) {
        log.debug("Thread Interrupted");
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  @LifecycleStop
  public void close() throws IOException
  {
    synchronized (started) {
      // flush() doesn't do things if it is not started, so flush must happen before we mark it as not started.
      flush();
      started.set(false);
      exec.shutdown();
    }
  }

  private class EmittingRunnable implements Runnable
  {
    private final long instantiatedVersion;

    public EmittingRunnable(
        long instantiatedVersion
    )
    {
      this.instantiatedVersion = instantiatedVersion;
    }

    @Override
    public void run()
    {
      long currVersion = version.get();

      try {
        if (!started.get()) {
          log.info("Not started, skipping...");
          return;
        }

        if (instantiatedVersion != currVersion) {
          log.debug("Skipping because instantiatedVersion[%s] != currVersion[%s]", instantiatedVersion, currVersion);
          return;
        } else {
          count.set(0);
          currVersion = version.incrementAndGet();
        }

        final List<byte[]> events;
        synchronized (eventsList) {
          events = eventsList.getAndSet(Lists.<byte[]>newLinkedList());
        }

        long eventsBytesCount = 0;
        for (final byte[] message : events) {
          eventsBytesCount += message.length;
        }

        // At this point we have taken charge of "events" but have not yet decremented bufferedSize.
        // We must eventually either decrement bufferedSize or re-add the events to "eventsList".

        boolean requeue = false;

        try {
          final List<List<byte[]>> batches = splitIntoBatches(events);
          log.debug(
              "Running export with version[%s], eventsList count[%s], bytes[%s], batches[%s]",
              instantiatedVersion,
              events.size(),
              eventsBytesCount,
              batches.size()
          );

          for (final List<byte[]> batch : batches) {
            log.debug("Sending batch to url[%s], batch.size[%,d]", url, batch.size());

            final StatusResponseHolder response = client.post(url)
                                                        .setContent("application/json", serializeBatch(batch))
                                                        .go(new StatusResponseHandler(Charsets.UTF_8))
                                                        .get();

            if (response.getStatus().getCode() / 100 != 2) {
              throw new ISE(
                  "Emissions of events not successful[%s], with message[%s].",
                  response.getStatus(),
                  response.getContent().trim()
              );
            }
          }
        }
        catch (Exception e) {
          log.warn(
              e, "Got exception when posting events to urlString[%s]. Resubmitting.",
              config.getRecipientBaseUrl()
          );
          // Re-queue and don't force a re-run immediately. Whatever happened might be transient, best to wait.
          requeue = true;
        }
        catch (Throwable e) {
          // Non-Exception Throwable. Don't retry, just throw away the messages and then re-throw.
          log.warn(
              e, "Got unrecoverable error when posting events to urlString[%s]. Dropping.",
              config.getRecipientBaseUrl()
          );
          throw e;
        } finally {
          if (requeue) {
            synchronized (eventsList) {
              eventsList.get().addAll(events);
            }
          } else {
            bufferedSize.addAndGet(-eventsBytesCount);
          }
        }
      }
      catch (Throwable e) {
        log.error(e, "Uncaught exception in EmittingRunnable.run()");
      }

      // Always reschedule, otherwise we all of a sudden don't emit anything.
      exec.schedule(new EmittingRunnable(currVersion), config.getFlushMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Serializes messages into a batch. Does not validate against maxBatchSize.
     *
     * @param messages list of JSON objects, one per message
     *
     * @return serialized JSON array
     */
    private byte[] serializeBatch(List<byte[]> messages)
    {
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        boolean first = true;
        baos.write(BATCH_START);
        for (final byte[] message : messages) {
          if (first) {
            first = false;
          } else {
            baos.write(MESSAGE_SEPARATOR);
          }
          baos.write(message);
        }
        baos.write(BATCH_END);
        return baos.toByteArray();
      }
      catch (IOException e) {
        // There's no reason to have IOException in the signature of this method, since BAOS won't throw them.
        throw Throwables.propagate(e);
      }
    }

    /**
     * Splits up messages into batches based on the configured maxBatchSize.
     *
     * @param messages list of JSON objects, one per message
     *
     * @return sub-lists of "messages"
     */
    private List<List<byte[]>> splitIntoBatches(List<byte[]> messages)
    {
      final List<List<byte[]>> batches = Lists.newLinkedList();
      List<byte[]> currentBatch = Lists.newArrayList();
      long currentBatchBytes = 0;

      for (final byte[] message : messages) {
        if (!currentBatch.isEmpty() && currentBatchBytes + MESSAGE_SEPARATOR.length + message.length + BATCH_END.length
                                       > config.getMaxBatchSize()) {
          // Existing batch is full; close it and start a new one.
          batches.add(currentBatch);
          currentBatch = Lists.newArrayList();
          currentBatchBytes = 0;
        }

        currentBatch.add(message);
        currentBatchBytes += message.length;
      }

      if (!currentBatch.isEmpty()) {
        batches.add(currentBatch);
      }

      return batches;
    }
  }

  /**
   * Used for tests, should not be used elsewhere.
   *
   * @return the executor used for emission of events
   */
  ScheduledExecutorService getExec()
  {
    return exec;
  }
}
