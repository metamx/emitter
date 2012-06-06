package com.metamx.emitter.service;

import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.Event;
import com.metamx.emitter.service.ServiceEventBuilder;

import java.io.IOException;

public class ServiceEmitter implements Emitter
{
  private final String service;
  private final String host;
  private final Emitter emitter;

  public ServiceEmitter(String service, String host, Emitter emitter)
  {
    this.service = service;
    this.host = host;
    this.emitter = emitter;
  }

  public String getService()
  {
    return service;
  }

  public String getHost()
  {
    return host;
  }

  public void start()
  {
    emitter.start();
  }

  public void emit(Event event)
  {
    emitter.emit(event);
  }

  public void emit(ServiceEventBuilder builder)
  {
    emit(builder.build(service, host));
  }

  public void flush() throws IOException
  {
    emitter.flush();
  }

  public void close() throws IOException
  {
    emitter.close();
  }
}
