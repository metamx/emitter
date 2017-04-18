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
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.metamx.common.ISE;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Map;

/**
 */
public class ServiceMetricEvent implements ServiceEvent
{
  public static Builder builder()
  {
    return new Builder();
  }

  private final DateTime createdTime;
  private final ImmutableMap<String, String> serviceDims;
  private final Map<String, Object> userDims;
  private final String feed;
  private final String metric;
  private final Number value;

  private ServiceMetricEvent(
      DateTime createdTime,
      ImmutableMap<String, String> serviceDims,
      Map<String, Object> userDims,
      String feed,
      String metric,
      Number value
  )
  {
    this.createdTime = createdTime != null ? createdTime : new DateTime();
    this.serviceDims = serviceDims;
    this.userDims = userDims;
    this.feed = feed;
    this.metric = metric;
    this.value = value;
  }

  public DateTime getCreatedTime()
  {
    return createdTime;
  }

  public String getFeed()
  {
    return feed;
  }

  public String getService()
  {
    return serviceDims.get("service");
  }

  public String getHost()
  {
    return serviceDims.get("host");
  }

  public Map<String, Object> getUserDims()
  {
    return ImmutableMap.copyOf(userDims);
  }

  public String getMetric()
  {
    return metric;
  }

  public Number getValue()
  {
    return value;
  }

  public boolean isSafeToBuffer()
  {
    return true;
  }

  @Override
  @JsonValue
  public Map<String, Object> toMap()
  {
    return ImmutableMap.<String, Object>builder()
                       .put("feed", getFeed())
                       .put("timestamp", createdTime.toString())
                       .putAll(serviceDims)
                       .put("metric", metric)
                       .put("value", value)
                       .putAll(
                           Maps.filterEntries(
                               userDims,
                               new Predicate<Map.Entry<String, Object>>()
                               {
                                 @Override
                                 public boolean apply(Map.Entry<String, Object> input)
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
    private String feed = "metrics";

    public Builder setFeed(String feed)
    {
      this.feed = feed;
      return this;
    }

    public Builder setUser1(String[] x)
    {
      userDims.put("user1", Arrays.asList(x));
      return this;
    }

    public Builder setUser2(String[] x)
    {
      userDims.put("user2", Arrays.asList(x));
      return this;
    }

    public Builder setUser3(String[] x)
    {
      userDims.put("user3", Arrays.asList(x));
      return this;
    }

    public Builder setUser4(String[] x)
    {
      userDims.put("user4", Arrays.asList(x));
      return this;
    }

    public Builder setUser5(String[] x)
    {
      userDims.put("user5", Arrays.asList(x));
      return this;
    }

    public Builder setUser6(String[] x)
    {
      userDims.put("user6", Arrays.asList(x));
      return this;
    }

    public Builder setUser7(String[] x)
    {
      userDims.put("user7", Arrays.asList(x));
      return this;
    }

    public Builder setUser8(String[] x)
    {
      userDims.put("user8", Arrays.asList(x));
      return this;
    }

    public Builder setUser9(String[] x)
    {
      userDims.put("user9", Arrays.asList(x));
      return this;
    }

    public Builder setUser10(String[] x)
    {
      userDims.put("user10", Arrays.asList(x));
      return this;
    }

    public Builder setDimension(String dim, String[] values)
    {
      userDims.put(dim, Arrays.asList(values));
      return this;
    }

    public Builder setUser1(String x)
    {
      userDims.put("user1", x);
      return this;
    }

    public Builder setUser2(String x)
    {
      userDims.put("user2", x);
      return this;
    }

    public Builder setUser3(String x)
    {
      userDims.put("user3", x);
      return this;
    }

    public Builder setUser4(String x)
    {
      userDims.put("user4", x);
      return this;
    }

    public Builder setUser5(String x)
    {
      userDims.put("user5", x);
      return this;
    }

    public Builder setUser6(String x)
    {
      userDims.put("user6", x);
      return this;
    }

    public Builder setUser7(String x)
    {
      userDims.put("user7", x);
      return this;
    }

    public Builder setUser8(String x)
    {
      userDims.put("user8", x);
      return this;
    }

    public Builder setUser9(String x)
    {
      userDims.put("user9", x);
      return this;
    }

    public Builder setUser10(String x)
    {
      userDims.put("user10", x);
      return this;
    }

    public Builder setDimension(String dim, String value)
    {
      userDims.put(dim, value);
      return this;
    }

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

    public Object getDimension(String dim)
    {
      return userDims.get(dim);
    }

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
      if (Double.isNaN(value.doubleValue())) {
        throw new ISE("Value of NaN is not allowed!");
      }
      if (Double.isInfinite(value.doubleValue())) {
        throw new ISE("Value of Infinite is not allowed!");
      }

      return new ServiceEventBuilder<ServiceMetricEvent>()
      {
        @Override
        public ServiceMetricEvent build(ImmutableMap<String, String> serviceDimensions)
        {
          return new ServiceMetricEvent(
              createdTime,
              serviceDimensions,
              userDims,
              feed,
              metric,
              value
          );
        }
      };
    }
  }
}
