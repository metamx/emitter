package com.metamx.emitter.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ParametrizedUriEmitterConfig
{
  @NotNull
  @JsonProperty
  private String recipientBaseUrlPattern;

  @JsonProperty("httpEmitting")
  private BaseHttpEmittingConfig httpEmittingConfig;

  public String getRecipientBaseUrlPattern()
  {
    return recipientBaseUrlPattern;
  }

  public HttpEmitterConfig buildHttpEmitterConfig(String baseUri)
  {
    return new HttpEmitterConfig(httpEmittingConfig, baseUri);
  }
}
