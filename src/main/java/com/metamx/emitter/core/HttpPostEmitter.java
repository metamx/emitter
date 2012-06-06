package com.metamx.emitter.core;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import com.metamx.http.client.HttpClient;
import com.metamx.http.client.response.ClientResponse;
import com.metamx.http.client.response.HttpResponseHandler;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 */
public class HttpPostEmitter implements Flushable, Closeable, Emitter
{
  private static final Logger log = new Logger(HttpPostEmitter.class);
  private static final AtomicInteger instanceCounter = new AtomicInteger();

  private final HttpEmitterConfig config;
  private final HttpClient client;
  private final ObjectMapper jsonMapper;

  private final AtomicReference<List<Event>> eventsList =
      new AtomicReference<List<Event>>(Lists.<Event>newLinkedList());
  private final AtomicInteger count = new AtomicInteger(0);
  private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat(String.format("HttpPostEmitter-%s-%%s", instanceCounter.incrementAndGet()))
          .build()
  );
  private final AtomicLong version = new AtomicLong(0);
  private final AtomicBoolean started = new AtomicBoolean(false);

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
    this.config = config;
    this.client = client;
    this.jsonMapper = jsonMapper;
  }

  @Override
  @LifecycleStart
  public void start()
  {
    if (!started.get()) {
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

    synchronized (eventsList) {
      eventsList.get().add(event);
    }

    if (!event.isSafeToBuffer() || count.incrementAndGet() >= config.getFlushCount()) {
      exec.execute(new EmittingRunnable(version.get()));
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

        final List<Event> events;
        synchronized (eventsList) {
          events = eventsList.getAndSet(Lists.<Event>newLinkedList());
        }
        log.debug("Running export with version[%s] and eventsList size[%s]", instantiatedVersion, events.size());
        if (!events.isEmpty()) {
          try {
            URL url = new URL(config.getRecipientBaseUrl());

            log.debug("url[%s] events.size[%s]", url, events.size());
            client.post(url)
                .setContent("application/json", jsonMapper.writeValueAsBytes(events))
                .go(new HttpResponseHandler<Object, Object>()
                {
                  @Override
                  public ClientResponse<Object> handleResponse(HttpResponse httpResponse)
                  {
                    if ((httpResponse.getStatus().getCode() / 100) != 2) {
                      log.warn(
                          "Emissions of events not successful[%s], with message[%s].",
                          httpResponse.getStatus(),
                          httpResponse.getContent().toString(Charsets.UTF_8)
                      );
                      synchronized (eventsList) {
                        eventsList.get().addAll(events);
                      }
                    }
                    return ClientResponse.finished(null);
                  }

                  @Override
                  public ClientResponse<Object> handleChunk(
                      ClientResponse<Object> response, HttpChunk httpChunk
                  )
                  {
                    return response;
                  }

                  @Override
                  public ClientResponse<Object> done(ClientResponse<Object> response)
                  {
                    return response;
                  }
                })
                .get();
          }
          catch (MalformedURLException e) {
            log.error(e, "Cannot post events!!!  Misconfigured or something? Bad urlString[%s]",
                      config.getRecipientBaseUrl()
            );
          }
          catch (JsonProcessingException e) {
            log.error(e, "Couldn't generate JSON objects? urlString[%s]", config.getRecipientBaseUrl());
          }
          catch (Exception e) {
            log.warn(e, "Got exception when posting events to urlString[%s].  Resubmitting.",
                     config.getRecipientBaseUrl()
            );
            // Re-queue and don't force a re-run immediately. Whatever happened might be transient, best to wait.
            synchronized (eventsList) {
              eventsList.get().addAll(events);
            }
          }
        }
      }
      catch (Throwable e) {
        log.error(e, "Uncaught exception in EmittingRunnable.run()");
        e.printStackTrace();
      }

      // Always reschedule, otherwise we all of a sudden don't emit anything.
      exec.schedule(new EmittingRunnable(currVersion), config.getFlushMillis(), TimeUnit.MILLISECONDS);
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
