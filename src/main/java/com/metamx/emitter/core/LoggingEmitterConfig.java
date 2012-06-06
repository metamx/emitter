package com.metamx.emitter.core;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 */
public class LoggingEmitterConfig
{
  @NotNull
  @JsonProperty
  private String loggerClass = LoggingEmitterConfig.class.toString();

  @NotNull
  @JsonProperty
  private String logLevel = "info";

  public String getLoggerClass()
  {
    return loggerClass;
  }

  public String getLogLevel()
  {
    return logLevel;
  }
}
