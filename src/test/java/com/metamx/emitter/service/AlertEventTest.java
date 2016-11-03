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
import com.metamx.emitter.service.AlertEvent.Severity;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 */
public class AlertEventTest
{
  @Test
  public void testStupid() throws Exception
  {
    AlertEvent event = new AlertEvent.Builder()
        .addData("something1", "a")
        .addData("something2", "b")
        .build("blargy")
        .build(ImmutableMap.of("service", "test", "host", "localhost"));

    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "alerts")
                    .put("timestamp", event.getCreatedTime().toString())
                    .put("service", "test")
                    .put("host", "localhost")
                    .put("severity", "component-failure")
                    .put("description", "blargy")
                    .put("data", ImmutableMap.<String, Object>of("something1", "a", "something2", "b"))
                    .build()
        ,
        event.toMap()
    );
  }

  @Test
  public void testAnomaly() throws Exception
  {
    AlertEvent event = new AlertEvent.Builder()
        .addData("something1", "a")
        .addData("something2", "b")
        .build(Severity.ANOMALY, "blargy")
        .build(ImmutableMap.of("service", "test", "host", "localhost"));

    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "alerts")
                    .put("timestamp", event.getCreatedTime().toString())
                    .put("service", "test")
                    .put("host", "localhost")
                    .put("severity", "anomaly")
                    .put("description", "blargy")
                    .put("data", ImmutableMap.<String, Object>of("something1", "a", "something2", "b"))
                    .build()
        ,
        event.toMap()
    );
  }

  @Test
  public void testComponentFailure() throws Exception
  {
    AlertEvent event = new AlertEvent.Builder()
        .addData("something1", "a")
        .addData("something2", "b")
        .build(Severity.COMPONENT_FAILURE, "blargy")
        .build(ImmutableMap.of("service", "test", "host", "localhost"));

    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "alerts")
                    .put("timestamp", event.getCreatedTime().toString())
                    .put("service", "test")
                    .put("host", "localhost")
                    .put("severity", "component-failure")
                    .put("description", "blargy")
                    .put("data", ImmutableMap.<String, Object>of("something1", "a", "something2", "b"))
                    .build()
        ,
        event.toMap()
    );
  }

  @Test
  public void testServiceFailure() throws Exception
  {
    AlertEvent event = new AlertEvent.Builder()
        .addData("something1", "a")
        .addData("something2", "b")
        .build(Severity.SERVICE_FAILURE, "blargy")
        .build(ImmutableMap.of("service", "test", "host", "localhost"));

    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "alerts")
                    .put("timestamp", event.getCreatedTime().toString())
                    .put("service", "test")
                    .put("host", "localhost")
                    .put("severity", "service-failure")
                    .put("description", "blargy")
                    .put("data", ImmutableMap.<String, Object>of("something1", "a", "something2", "b"))
                    .build()
        ,
        event.toMap()
    );
  }

  @Test
  public void testDefaulting() throws Exception
  {
    final String service = "some service";
    final String host = "some host";
    final String desc = "some description";
    final Map<String, Object> data = ImmutableMap.<String, Object>builder().put("a", "1").put("b", "2").build();
    for (Severity severity : new Severity[] { Severity.ANOMALY, Severity.COMPONENT_FAILURE, Severity.SERVICE_FAILURE })
    {
      ImmutableMap<String, String> serviceDimensions = ImmutableMap.of("service", service, "host", host);
      Assert.assertEquals(
        contents(new AlertEvent(serviceDimensions,                             desc, data)),
        contents(new AlertEvent(serviceDimensions, Severity.COMPONENT_FAILURE, desc, data))
      );

      Assert.assertEquals(
        contents(new AlertEvent(serviceDimensions,                             desc                   )),
        contents(new AlertEvent(serviceDimensions, Severity.COMPONENT_FAILURE, desc, ImmutableMap.<String,Object>of()))
      );

      Assert.assertEquals(
        contents(new AlertEvent.Builder().addData("a","1").addData("b","2").build(desc).build(serviceDimensions)),
        contents(new AlertEvent(serviceDimensions, Severity.COMPONENT_FAILURE, desc, data))
      );

      Assert.assertEquals(
        contents(new AlertEvent.Builder().build(desc, data).build(serviceDimensions)),
        contents(new AlertEvent(serviceDimensions, Severity.COMPONENT_FAILURE, desc, data))
      );

      Assert.assertEquals(
        contents(new AlertEvent.Builder().addData("a","1").addData("b","2")
                                         .build(severity, desc).build(serviceDimensions)),
        contents(new AlertEvent(serviceDimensions, severity, desc, data))
      );

      Assert.assertEquals(
        contents(new AlertEvent.Builder().build(severity, desc, data).build(serviceDimensions)),
        contents(new AlertEvent(serviceDimensions, severity, desc, data))
      );
    }
  }

  public Map<String, Object> contents(AlertEvent a)
  {
    return Maps.filterKeys(a.toMap(), new Predicate<String>()
    {
      @Override
      public boolean apply(String k)
      {
        return k != "timestamp";
      }
    });
  }
}
