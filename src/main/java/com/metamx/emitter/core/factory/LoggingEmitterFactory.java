package com.metamx.emitter.core.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.LoggingEmitter;
import com.metamx.emitter.core.LoggingEmitterConfig;
import com.metamx.http.client.HttpClient;

public class LoggingEmitterFactory implements EmitterFactory
{
  @JsonProperty("logging")
  private LoggingEmitterConfig loggingEmitterConfig = null;

  public LoggingEmitterFactory() {}

  public LoggingEmitterFactory(LoggingEmitterConfig loggingEmitterConfig)
  {
    this.loggingEmitterConfig = loggingEmitterConfig;
  }

  @Override
  public Emitter build(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    return build(objectMapper, lifecycle);
  }

  public Emitter build(ObjectMapper objectMapper, Lifecycle lifecycle)
  {
    Emitter retVal = new LoggingEmitter(loggingEmitterConfig, objectMapper);
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
