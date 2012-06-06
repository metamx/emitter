package com.metamx.emitter.core;

import org.codehaus.jackson.annotate.JsonValue;
import org.joda.time.DateTime;

import java.util.Map;

/**
 */
public interface Event
{
  @JsonValue
  public Map<String, Object> toMap();
  public String getFeed();
  public DateTime getCreatedTime();
  public boolean isSafeToBuffer();
}
