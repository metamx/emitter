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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.metamx.emitter.core.Event;
import org.joda.time.DateTime;

import java.util.Map;

/**
 */
public class ServiceMetricEvent implements Event
{
  private final DateTime createdTime;
  private final String service;
  private final String host;
  private final String[] user1;
  private final String[] user2;
  private final String[] user3;
  private final String[] user4;
  private final String[] user5;
  private final String[] user6;
  private final String[] user7;
  private final String[] user8;
  private final String[] user9;
  private final String[] user10;
  private final String metric;
  private final Number value;

  public ServiceMetricEvent(
      DateTime createdTime,
      String service,
      String host,
      String[] user1,
      String[] user2,
      String[] user3,
      String[] user4,
      String[] user5,
      String[] user6,
      String[] user7,
      String[] user8,
      String[] user9,
      String[] user10,
      String metric,
      Number value
  )
  {
    this.createdTime = createdTime;
    this.service = service;
    this.host = host;
    this.user1 = user1;
    this.user2 = user2;
    this.user3 = user3;
    this.user4 = user4;
    this.user5 = user5;
    this.user6 = user6;
    this.user7 = user7;
    this.user8 = user8;
    this.user9 = user9;
    this.user10 = user10;
    this.metric = metric;
    this.value = value;
  }

  public ServiceMetricEvent(
      String service,
      String host,
      String[] user1,
      String[] user2,
      String[] user3,
      String[] user4,
      String[] user5,
      String[] user6,
      String[] user7,
      String[] user8,
      String[] user9,
      String[] user10,
      String metric,
      Number value
  )
  {
    this(
        new DateTime(),
        service,
        host,
        user1,
        user2,
        user3,
        user4,
        user5,
        user6,
        user7,
        user8,
        user9,
        user10,
        metric,
        value
    );
  }

  public ServiceMetricEvent(
      String service,
      String host,
      String user1,
      String user2,
      String user3,
      String user4,
      String user5,
      String user6,
      String user7,
      String user8,
      String user9,
      String user10,
      String metric,
      Number value
  )
  {
    this(
        service,
        host,
        user1  == null ? null : new String[] { user1 },
        user2  == null ? null : new String[] { user2 },
        user3  == null ? null : new String[] { user3 },
        user4  == null ? null : new String[] { user4 },
        user5  == null ? null : new String[] { user5 },
        user6  == null ? null : new String[] { user6 },
        user7  == null ? null : new String[] { user7 },
        user8  == null ? null : new String[] { user8 },
        user9  == null ? null : new String[] { user9 },
        user10 == null ? null : new String[] { user10 },
        metric,
        value
    );
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
    Joiner joiner = Joiner.on("\u0001");
    ImmutableMap.Builder<String, String> userDimsBuilder = ImmutableMap.builder();
    if (user1  != null && user1.length  > 0) userDimsBuilder.put("user1",  joiner.join(user1));
    if (user2  != null && user2.length  > 0) userDimsBuilder.put("user2",  joiner.join(user2));
    if (user3  != null && user3.length  > 0) userDimsBuilder.put("user3",  joiner.join(user3));
    if (user4  != null && user4.length  > 0) userDimsBuilder.put("user4",  joiner.join(user4));
    if (user5  != null && user5.length  > 0) userDimsBuilder.put("user5",  joiner.join(user5));
    if (user6  != null && user6.length  > 0) userDimsBuilder.put("user6",  joiner.join(user6));
    if (user7  != null && user7.length  > 0) userDimsBuilder.put("user7",  joiner.join(user7));
    if (user8  != null && user8.length  > 0) userDimsBuilder.put("user8",  joiner.join(user8));
    if (user9  != null && user9.length  > 0) userDimsBuilder.put("user9",  joiner.join(user9));
    if (user10 != null && user10.length > 0) userDimsBuilder.put("user10", joiner.join(user10));
    Map<String, String> userDims = userDimsBuilder.build();

    return ImmutableMap.<String, Object>builder()
        .put("feed", getFeed())
        .put("timestamp", createdTime.toString())
        .put("service", service)
        .put("host", host)
        .put("metric", metric)
        .put("value", value)
        .putAll(userDims)
        .build();
  }

  public static class Builder
  {
    private String[] user1  = null;
    private String[] user2  = null;
    private String[] user3  = null;
    private String[] user4  = null;
    private String[] user5  = null;
    private String[] user6  = null;
    private String[] user7  = null;
    private String[] user8  = null;
    private String[] user9  = null;
    private String[] user10 = null;

    public String[] getUser1  () { return user1  ; }
    public String[] getUser2  () { return user2  ; }
    public String[] getUser3  () { return user3  ; }
    public String[] getUser4  () { return user4  ; }
    public String[] getUser5  () { return user5  ; }
    public String[] getUser6  () { return user6  ; }
    public String[] getUser7  () { return user7  ; }
    public String[] getUser8  () { return user8  ; }
    public String[] getUser9  () { return user9  ; }
    public String[] getUser10 () { return user10 ; }

    public Builder setUser1  (String[] x) { user1  = x; return this; }
    public Builder setUser2  (String[] x) { user2  = x; return this; }
    public Builder setUser3  (String[] x) { user3  = x; return this; }
    public Builder setUser4  (String[] x) { user4  = x; return this; }
    public Builder setUser5  (String[] x) { user5  = x; return this; }
    public Builder setUser6  (String[] x) { user6  = x; return this; }
    public Builder setUser7  (String[] x) { user7  = x; return this; }
    public Builder setUser8  (String[] x) { user8  = x; return this; }
    public Builder setUser9  (String[] x) { user9  = x; return this; }
    public Builder setUser10 (String[] x) { user10 = x; return this; }

    public Builder setUser1  (String x) { return setUser1  (x == null ? null : new String[] { x }); }
    public Builder setUser2  (String x) { return setUser2  (x == null ? null : new String[] { x }); }
    public Builder setUser3  (String x) { return setUser3  (x == null ? null : new String[] { x }); }
    public Builder setUser4  (String x) { return setUser4  (x == null ? null : new String[] { x }); }
    public Builder setUser5  (String x) { return setUser5  (x == null ? null : new String[] { x }); }
    public Builder setUser6  (String x) { return setUser6  (x == null ? null : new String[] { x }); }
    public Builder setUser7  (String x) { return setUser7  (x == null ? null : new String[] { x }); }
    public Builder setUser8  (String x) { return setUser8  (x == null ? null : new String[] { x }); }
    public Builder setUser9  (String x) { return setUser9  (x == null ? null : new String[] { x }); }
    public Builder setUser10 (String x) { return setUser10 (x == null ? null : new String[] { x }); }

    public ServiceEventBuilder<ServiceMetricEvent> build(
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
              service,
              host,
              user1,
              user2,
              user3,
              user4,
              user5,
              user6,
              user7,
              user8,
              user9,
              user10,
              metric,
              value
          );
        }
      };
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
              user1,
              user2,
              user3,
              user4,
              user5,
              user6,
              user7,
              user8,
              user9,
              user10,
              metric,
              value
          );
        }
      };
    }

  }
}
