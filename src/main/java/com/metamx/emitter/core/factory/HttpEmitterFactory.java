package com.metamx.emitter.core.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.HttpEmitterConfig;
import com.metamx.emitter.core.HttpPostEmitter;
import com.metamx.http.client.HttpClient;

public class HttpEmitterFactory implements EmitterFactory
{
  @JsonProperty("http")
  private HttpEmitterConfig httpEmitterConfig = null;

  public HttpEmitterFactory() {}

  public HttpEmitterFactory(HttpEmitterConfig httpEmitterConfig)
  {
    this.httpEmitterConfig = httpEmitterConfig;
  }

  @Override
  public Emitter build(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    Emitter retVal = new HttpPostEmitter(httpEmitterConfig, httpClient, objectMapper);
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
