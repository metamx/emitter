package com.metamx.emitter.core.factory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.ParametrizedUriEmitter;
import com.metamx.emitter.core.ParametrizedUriEmitterConfig;
import com.metamx.http.client.HttpClient;

import javax.validation.constraints.NotNull;

public class ParametrizedUriEmitterFactory extends ParametrizedUriEmitterConfig implements EmitterFactory
{
  @JsonCreator
  public ParametrizedUriEmitterFactory(@NotNull @JsonProperty("recipientBaseUrlPattern") String recipientBaseUrlPattern)
  {
    super(recipientBaseUrlPattern);
  }

  @Override
  public Emitter makeEmitter(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    final Emitter retVal = new ParametrizedUriEmitter(this, httpClient, objectMapper);
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
