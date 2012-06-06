package com.metamx.emitter.service;

import com.google.common.collect.ImmutableMap;
import com.metamx.emitter.service.ServiceMetricEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class ServiceMetricEventTest
{
  @Test
  public void testStupidTest() throws Exception
  {
    ServiceMetricEvent builderEvent = new ServiceMetricEvent.Builder()
        .setUser1("a")
        .setUser2("b")
        .setUser3("c")
        .setUser4("d")
        .setUser5("e")
        .setUser6("f")
        .setUser7("g")
        .setUser8("h")
        .setUser9("i")
        .setUser10("j")
        .build("test-metric", 1234)
        .build("test", "localhost");

    ServiceMetricEvent constructorEvent = new ServiceMetricEvent(
        "test",
        "localhost",
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "h",
        "i",
        "j",
        "test-metric",
        1234
    );

    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "metrics")
                    .put("timestamp", builderEvent.getCreatedTime().toString())
                    .put("service", "test")
                    .put("host", "localhost")
                    .put("metric", "test-metric")
                    .put("user1", "a")
                    .put("user2", "b")
                    .put("user3", "c")
                    .put("user4", "d")
                    .put("user5", "e")
                    .put("user6", "f")
                    .put("user7", "g")
                    .put("user8", "h")
                    .put("user9", "i")
                    .put("user10", "j")
                    .put("value", 1234)
                    .build(),
        builderEvent.toMap()
    );
    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "metrics")
                    .put("timestamp", constructorEvent.getCreatedTime().toString())
                    .put("service", "test")
                    .put("host", "localhost")
                    .put("metric", "test-metric")
                    .put("user1", "a")
                    .put("user2", "b")
                    .put("user3", "c")
                    .put("user4", "d")
                    .put("user5", "e")
                    .put("user6", "f")
                    .put("user7", "g")
                    .put("user8", "h")
                    .put("user9", "i")
                    .put("user10", "j")
                    .put("value", 1234)
                    .build(), constructorEvent.toMap()
    );
  }
}
