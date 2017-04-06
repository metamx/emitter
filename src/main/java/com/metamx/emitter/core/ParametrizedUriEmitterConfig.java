package com.metamx.emitter.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParametrizedUriEmitterConfig
{
  @JsonProperty("http")
  private HttpEmitterConfig.Builder basicHttpConficBuilder = new HttpEmitterConfig.Builder(null);

  public HttpEmitterConfig.Builder getBasicHttpConficBuilder()
  {
    return basicHttpConficBuilder;
  }

  public void setBasicHttpConficBuilder(HttpEmitterConfig.Builder basicHttpConficBuilder)
  {
    this.basicHttpConficBuilder = basicHttpConficBuilder;
  }

  public HttpEmitterConfig buildHttpEmitterConfig(String baseUri)
  {
    return basicHttpConficBuilder.copyWithRecipientBaseUrl(baseUri).build();
  }
}
