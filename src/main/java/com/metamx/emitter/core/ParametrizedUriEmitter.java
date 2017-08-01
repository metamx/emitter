package com.metamx.emitter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.http.client.HttpClient;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

public class ParametrizedUriEmitter implements Flushable, Closeable, Emitter
{
  /**
   * Type should be ConcurrentHashMap, not {@link java.util.concurrent.ConcurrentMap}, because the latter _doesn't_
   * guarantee that the lambda passed to {@link java.util.Map#computeIfAbsent} is executed at most once.
   */
  private final ConcurrentHashMap<URI, HttpPostEmitter> emitters = new ConcurrentHashMap<>();
  private final UriExtractor uriExtractor;
  private final Lifecycle innerLifecycle = new Lifecycle();
  private final HttpClient client;
  private final ObjectMapper jsonMapper;
  private final ParametrizedUriEmitterConfig config;

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

  @Override
  public void emit(Event event)
  {
    try {
      URI uri = uriExtractor.apply(event);
      HttpPostEmitter emitter = emitters.get(uri);
      if (emitter == null) {
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
      emitter.emit(event);
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(String.format("Failed to extract URI for event: %s", event.toMap().toString()));
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  @LifecycleStop
  public void close() throws IOException
  {
    innerLifecycle.stop();
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
}
