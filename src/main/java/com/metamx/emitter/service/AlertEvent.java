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

import com.google.common.collect.ImmutableMap;
import com.metamx.emitter.core.Event;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class AlertEvent implements Event
{
  private final String service;
  private final String host;
  private final Severity severity;
  private final String description;
  private final DateTime createdTime;

  private final Map<String, Object> dataMap;

  public AlertEvent(
      String service,
      String host,
      Severity severity,
      String description,
      Map<String, Object> dataMap
  )
  {
    this.service = service;
    this.host = host;
    this.severity = severity;
    this.description = description;
    this.dataMap = dataMap;

    createdTime = new DateTime();
  }

  public AlertEvent(
      String service,
      String host,
      String description,
      Map<String, Object> dataMap
  )
  {
    this(service, host, Severity.DEFAULT, description, dataMap);
  }

  public AlertEvent(
      String service,
      String host,
      String description
  )
  {
    this(service, host, description, new HashMap<String, Object>());
  }

  public DateTime getCreatedTime()
  {
    return createdTime;
  }

  public String getFeed()
  {
    return "alerts";
  }

  public boolean isSafeToBuffer()
  {
    return false;
  }

  @Override
  public Map<String, Object> toMap()
  {
    return ImmutableMap.<String, Object>builder()
        .put("feed", getFeed())
        .put("timestamp", createdTime.toString())
        .put("service", service)
        .put("host", host)
        .put("severity", severity.toString())
        .put("description", description)
        .put("data", dataMap)
        .build();
  }

  public static enum Severity
  {
    ANOMALY
    {
      @Override
      public String toString()
      {
        return "anomaly";
      }
    },

    COMPONENT_FAILURE
    {
      @Override
      public String toString()
      {
        return "component-failure";
      }
    },

    SERVICE_FAILURE
    {
      @Override
      public String toString()
      {
        return "service-failure";
      }
    };

    public static final Severity DEFAULT = COMPONENT_FAILURE;
  }

  @Deprecated // Use AlertBuilder
  public static class Builder
  {
    private final Map<String, Object> dataMap = new HashMap<String, Object>();

    public Builder addData(String identifier, String value)
    {
      dataMap.put(identifier, value);
      return this;
    }

    @Deprecated
    public ServiceEventBuilder<AlertEvent> build(
        final String description
    )
    {
      return build(Severity.DEFAULT, description, dataMap);
    }

    @Deprecated
    public ServiceEventBuilder<AlertEvent> build(
        final String description,
        final Map<String, Object> dataMap
    )
    {
      return build(Severity.DEFAULT, description, dataMap);
    }

    @Deprecated
    public ServiceEventBuilder<AlertEvent> build(
        final Severity severity,
        final String description
    )
    {
      return build(severity, description, dataMap);
    }

    @Deprecated
    public ServiceEventBuilder<AlertEvent> build(
        final Severity severity,
        final String description,
        final Map<String, Object> dataMap
    )
    {
      return new ServiceEventBuilder<AlertEvent>()
      {
        @Override
        public AlertEvent build(String service, String host)
        {
          return new AlertEvent(service, host, severity, description, dataMap);
        }
      };
    }
  }
}
