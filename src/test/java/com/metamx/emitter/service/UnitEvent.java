/*
 * Copyright 2012 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metamx.emitter.service;

import com.fasterxml.jackson.annotation.JsonValue;
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
  @JsonValue
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
