package com.metamx.emitter.service;

import com.google.common.collect.ImmutableMap;
import com.metamx.emitter.core.Event;
import org.joda.time.DateTime;

import java.util.Map;

/**
 */
public class UnitEvent implements Event
{
  private final String feed;
  private final Number value;
  private final String targetURLKey;
  private final DateTime createdTime;

  public UnitEvent(String feed, Number value)
  {
    this(feed, value, TARGET_URL_KEY);
  }

  public UnitEvent(String feed, Number value, String targetURLKey)
  {
    this.feed = feed;
    this.value = value;
    this.targetURLKey = targetURLKey;

    createdTime = new DateTime();
  }

  @Override
  public Map<String, Object> toMap()
  {
    return ImmutableMap.<String, Object>of(
        "feed", feed,
        "metrics", ImmutableMap.of("value", value)
    );
  }

  public DateTime getCreatedTime()
  {
    return createdTime;
  }

  public String getFeed()
  {
    return targetURLKey;
  }

  public boolean isSafeToBuffer()
  {
    return true;
  }

  public static final String TARGET_URL_KEY = "test";
}
