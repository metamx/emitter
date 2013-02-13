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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.metamx.emitter.core.Event;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 */
public class ServiceMetricEvent implements Event
{
  public static Builder builder(){
    return new Builder();
  }

  private final DateTime createdTime;
  private final String service;
  private final String host;
  private final Map<String, Object> userDims;
  private final String metric;
  private final Number value;

  private ServiceMetricEvent(
      DateTime createdTime,
      String service,
      String host,
      Map<String, Object> userDims,
      String metric,
      Number value
  )
  {
    this.createdTime = createdTime != null ? createdTime : new DateTime();
    this.service = service;
    this.host = host;
    this.userDims = userDims;
    this.metric = metric;
    this.value = value;
  }

  public DateTime getCreatedTime()
  {
    return createdTime;
  }

  public String getFeed()
  {
    return "metrics";
  }

  public boolean isSafeToBuffer()
  {
    return true;
  }

  @Override
  public Map<String, Object> toMap()
  {
    return ImmutableMap.<String, Object>builder()
        .put("feed", getFeed())
        .put("timestamp", createdTime.toString())
        .put("service", service)
        .put("host", host)
        .put("metric", metric)
        .put("value", value)
        .putAll(
            Maps.filterEntries(
                userDims,
                new Predicate<Map.Entry<String, Object>>()
                {
                  @Override
                  public boolean apply(@Nullable Map.Entry<String, Object> input)
                  {
                    return input.getKey() != null;
                  }
                }
            )
        )
        .build();
  }

  public static class Builder
  {
    private final Map<String, Object> userDims = Maps.newTreeMap();

    public Builder setUser1  (String[] x) { userDims.put("user1", Arrays.asList(x)); return this; }
    public Builder setUser2  (String[] x) { userDims.put("user2", Arrays.asList(x)); return this; }
    public Builder setUser3  (String[] x) { userDims.put("user3", Arrays.asList(x)); return this; }
    public Builder setUser4  (String[] x) { userDims.put("user4", Arrays.asList(x)); return this; }
    public Builder setUser5  (String[] x) { userDims.put("user5", Arrays.asList(x)); return this; }
    public Builder setUser6  (String[] x) { userDims.put("user6", Arrays.asList(x)); return this; }
    public Builder setUser7  (String[] x) { userDims.put("user7", Arrays.asList(x)); return this; }
    public Builder setUser8  (String[] x) { userDims.put("user8", Arrays.asList(x)); return this; }
    public Builder setUser9  (String[] x) { userDims.put("user9", Arrays.asList(x)); return this; }
    public Builder setUser10 (String[] x) { userDims.put("user10", Arrays.asList(x)); return this; }

    public Builder setUser1  (String x) { userDims.put("user1", x); return this; }
    public Builder setUser2  (String x) { userDims.put("user2", x); return this; }
    public Builder setUser3  (String x) { userDims.put("user3", x); return this; }
    public Builder setUser4  (String x) { userDims.put("user4", x); return this; }
    public Builder setUser5  (String x) { userDims.put("user5", x); return this; }
    public Builder setUser6  (String x) { userDims.put("user6", x); return this; }
    public Builder setUser7  (String x) { userDims.put("user7", x); return this; }
    public Builder setUser8  (String x) { userDims.put("user8", x); return this; }
    public Builder setUser9  (String x) { userDims.put("user9", x); return this; }
    public Builder setUser10 (String x) { userDims.put("user10", x); return this; }

    public Object getUser1() { return userDims.get("user1"); }
    public Object getUser2() { return userDims.get("user2"); }
    public Object getUser3() { return userDims.get("user3"); }
    public Object getUser4() { return userDims.get("user4"); }
    public Object getUser5() { return userDims.get("user5"); }
    public Object getUser6() { return userDims.get("user6"); }
    public Object getUser7() { return userDims.get("user7"); }
    public Object getUser8() { return userDims.get("user8"); }
    public Object getUser9() { return userDims.get("user9"); }
    public Object getUser10() { return userDims.get("user10"); }

    public ServiceEventBuilder<ServiceMetricEvent> build(
        final String metric,
        final Number value
    )
    {
      return build(null, metric, value);
    }

    public ServiceEventBuilder<ServiceMetricEvent> build(
        final DateTime createdTime,
        final String metric,
        final Number value
    )
    {
      return new ServiceEventBuilder<ServiceMetricEvent>()
      {
        @Override
        public ServiceMetricEvent build(String service, String host)
        {
          return new ServiceMetricEvent(
              createdTime,
              service,
              host,
              userDims,
              metric,
              value
          );
        }
      };
    }

  }
}
