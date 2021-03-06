package com.metamx.emitter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import com.metamx.http.client.HttpClient;

import javax.annotation.concurrent.GuardedBy;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ParametrizedUriEmitter implements Flushable, Closeable, Emitter
{
  private static final Logger log = new Logger(ParametrizedUriEmitter.class);
  private static final Set<String> ONLY_FEED_PARAM = ImmutableSet.of("feed");

  private static UriExtractor makeUriExtractor(ParametrizedUriEmitterConfig config)
  {
    final String baseUri = config.getRecipientBaseUrlPattern();
    final ParametrizedUriExtractor parametrizedUriExtractor = new ParametrizedUriExtractor(baseUri);
    UriExtractor uriExtractor = parametrizedUriExtractor;
    if (ONLY_FEED_PARAM.equals(parametrizedUriExtractor.getParams())) {
      uriExtractor = new FeedUriExtractor(baseUri.replace("{feed}", "%s"));
    }
    return uriExtractor;
  }

  /**
   * Type should be ConcurrentHashMap, not {@link java.util.concurrent.ConcurrentMap}, because the latter _doesn't_
   * guarantee that the lambda passed to {@link java.util.Map#computeIfAbsent} is executed at most once.
   */
  private final ConcurrentHashMap<URI, HttpPostEmitter> emitters = new ConcurrentHashMap<>();
  private final UriExtractor uriExtractor;
  private final Object startCloseLock = new Object();
  @GuardedBy("startCloseLock")
  private boolean started = false;
  @GuardedBy("startCloseLock")
  private boolean closed = false;
  private final Lifecycle innerLifecycle = new Lifecycle();
  private final HttpClient client;
  private final ObjectMapper jsonMapper;
  private final ParametrizedUriEmitterConfig config;

  public ParametrizedUriEmitter(
      ParametrizedUriEmitterConfig config,
      HttpClient client,
      ObjectMapper jsonMapper
  )
  {
    this(config, client, jsonMapper, makeUriExtractor(config));
  }

  public ParametrizedUriEmitter(
      ParametrizedUriEmitterConfig config,
      HttpClient client,
      ObjectMapper jsonMapper,
      UriExtractor uriExtractor
  )
  {
    this.config = config;
    this.client = client;
    this.jsonMapper = jsonMapper;
    this.uriExtractor = uriExtractor;
  }

  @Override
  @LifecycleStart
  public void start()
  {
    // Use full synchronized instead of atomic flag, because otherwise some thread may think that the emitter is already
    // started while it's in the process of starting by another thread.
    synchronized (startCloseLock) {
      if (started) {
        return;
      }
      started = true;
      try {
        innerLifecycle.start();
      }
      catch (RuntimeException e) {
        throw e;
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void emit(Event event)
  {
    try {
      URI uri = uriExtractor.apply(event);
      HttpPostEmitter emitter = emitters.get(uri);
      if (emitter == null) {
        try {
          emitter = emitters.computeIfAbsent(uri, u -> {
            try {
              return innerLifecycle.addMaybeStartManagedInstance(
                  new HttpPostEmitter(
                      config.buildHttpEmitterConfig(u.toString()),
                      client,
                      jsonMapper
                  )
              );
            }
            catch (Exception e) {
              throw Throwables.propagate(e);
            }
          });
        }
        catch (RuntimeException e) {
          log.error(e, "Error while creating or starting an HttpPostEmitter for URI[%s]", uri);
          return;
        }
      }
      emitter.emit(event);
    }
    catch (URISyntaxException e) {
      log.error(e, "Failed to extract URI for event[%s]", event.toMap());
    }
  }

  @Override
  @LifecycleStop
  public void close() throws IOException
  {
    // Use full synchronized instead of atomic flag, because otherwise some thread may think that the emitter is already
    // closed while it's in the process of closing by another thread.
    synchronized (startCloseLock) {
      if (closed) {
        return;
      }
      closed = true;
      innerLifecycle.stop();
    }
  }

  @Override
  public void flush() throws IOException
  {
    Exception thrown = null;
    for (HttpPostEmitter httpPostEmitter : emitters.values()) {
      try {
        httpPostEmitter.flush();
      }
      catch (Exception e) {
        // If flush was interrupted, exit the loop
        if (Thread.currentThread().isInterrupted()) {
          if (thrown != null) {
            e.addSuppressed(thrown);
          }
          throw Throwables.propagate(e);
        }
        if (thrown == null) {
          thrown = e;
        } else {
          if (thrown != e) {
            thrown.addSuppressed(e);
          }
        }
      }
    }
    if (thrown != null) {
      throw Throwables.propagate(thrown);
    }
  }

  @Override
  public String toString()
  {
    return "ParametrizedUriEmitter{" +
           "emitters=" + emitters.keySet() +
           ", uriExtractor=" + uriExtractor +
           ", config=" + config +
           '}';
  }
}
