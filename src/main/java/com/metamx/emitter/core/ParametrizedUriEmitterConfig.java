package com.metamx.emitter.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

public class ParametrizedUriEmitterConfig extends HttpEmitterConfig.Builder
{
  private String recipientBaseUrlPattern;

  @JsonCreator
  public ParametrizedUriEmitterConfig(@NotNull @JsonProperty("recipientBaseUrlPattern") String recipientBaseUrlPattern)
  {
    super(null);
    this.recipientBaseUrlPattern = recipientBaseUrlPattern;
  }

  public String getRecipientBaseUrlPattern()
  {
    return recipientBaseUrlPattern;
  }

  public HttpEmitterConfig buildHttpEmitterConfig(String baseUri)
  {
    return copyWithRecipientBaseUrl(baseUri).build();
  }
}
