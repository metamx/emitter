package com.metamx.emitter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.http.client.HttpClient;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ParametrizedUriEmitter implements Flushable, Closeable, Emitter
{
  private final Map<URI, HttpPostEmitter> emitters = new HashMap<URI, HttpPostEmitter>();
  private UriExtractor uriExtractor;
  private Lifecycle innerLifecycle = new Lifecycle();
  private HttpClient client;
  private ObjectMapper jsonMapper;
  private ParametrizedUriEmitterConfig config;

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
        synchronized (emitters) {
          emitter = emitters.get(uri); // Double check that nobody else created emitter while we were waiting on lock
          if (emitter == null) {
            emitter = new HttpPostEmitter(
                config.buildHttpEmitterConfig(uri.toString(), jsonMapper),
                client,
                jsonMapper
            );

            innerLifecycle.addMaybeStartManagedInstance(emitter);

            emitters.put(uri, emitter);
          }
        }
      }
      emitter.emit(event);
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(String.format("Failed to extract URI for event: %s", event.toMap().toString()));
    }
    catch (Exception e) {
      throw new RuntimeException(e);
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
    synchronized (emitters) {
      for (Emitter emitter : emitters.values()) {
        emitter.flush();
      }
    }
  }
}
