package com.metamx.emitter.service;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.metamx.emitter.service.AlertEvent;
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
        .build("test", "localhost");

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
        .build("test", "localhost");

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
        .build("test", "localhost");

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
        .build("test", "localhost");

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
      Assert.assertEquals(
        contents(new AlertEvent(service, host,                             desc, data)),
        contents(new AlertEvent(service, host, Severity.COMPONENT_FAILURE, desc, data))
      );

      Assert.assertEquals(
        contents(new AlertEvent(service, host,                             desc                   )),
        contents(new AlertEvent(service, host, Severity.COMPONENT_FAILURE, desc, ImmutableMap.<String,Object>of()))
      );

      Assert.assertEquals(
        contents(new AlertEvent.Builder().addData("a","1").addData("b","2").build(desc).build(service, host)),
        contents(new AlertEvent(service, host, Severity.COMPONENT_FAILURE, desc, data))
      );

      Assert.assertEquals(
        contents(new AlertEvent.Builder().build(desc, data).build(service, host)),
        contents(new AlertEvent(service, host, Severity.COMPONENT_FAILURE, desc, data))
      );

      Assert.assertEquals(
        contents(new AlertEvent.Builder().addData("a","1").addData("b","2").build(severity, desc).build(service, host)),
        contents(new AlertEvent(service, host, severity, desc, data))
      );

      Assert.assertEquals(
        contents(new AlertEvent.Builder().build(severity, desc, data).build(service, host)),
        contents(new AlertEvent(service, host, severity, desc, data))
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
