package com.metamx.emitter.core;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 */
public class HttpEmitterConfig
{
  @Min(1)
  @JsonProperty
  private long flushMillis = 60 * 1000;

  @Min(0)
  @JsonProperty
  private int flushCount = 500;

  @NotNull
  @JsonProperty
  private String recipientBaseUrl = null;

  public HttpEmitterConfig(){}

  public HttpEmitterConfig(
      long flushMillis,
      int flushCount,
      String recipientBaseUrl
  )
  {
    this.flushMillis = flushMillis;
    this.flushCount = flushCount;
    this.recipientBaseUrl = recipientBaseUrl;
  }

  public long getFlushMillis()
  {
    return flushMillis;
  }

  public int getFlushCount()
  {
    return flushCount;
  }

  public String getRecipientBaseUrl()
  {
    return recipientBaseUrl;
  }
}
