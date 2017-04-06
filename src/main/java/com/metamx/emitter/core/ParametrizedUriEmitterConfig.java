package com.metamx.emitter.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParametrizedUriEmitterConfig
{
  @JsonProperty("http")
  private HttpEmitterConfig.Builder basicHttpConfigBuilder = new HttpEmitterConfig.Builder(null);

  public HttpEmitterConfig.Builder getBasicHttpConfigBuilder()
  {
    return basicHttpConfigBuilder;
  }

  public void setBasicHttpConfigBuilder(HttpEmitterConfig.Builder basicHttpConfigBuilder)
  {
    this.basicHttpConfigBuilder = basicHttpConfigBuilder;
  }

  public HttpEmitterConfig buildHttpEmitterConfig(String baseUri)
  {
    return basicHttpConfigBuilder.copyWithRecipientBaseUrl(baseUri).build();
  }
}
