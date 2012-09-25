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
import com.metamx.emitter.service.ServiceMetricEvent;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

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

    ServiceMetricEvent constructorEvent = ServiceMetricEvent
        .builder()
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

    ServiceMetricEvent arrayConstructorEvent = ServiceMetricEvent
        .builder()
        .setUser1(new String[] { "a" })
        .setUser2(new String[]{"b"})
        .setUser3(new String[]{"c"})
        .setUser4(new String[]{"d"})
        .setUser5(new String[]{"e"})
        .setUser6(new String[]{"f"})
        .setUser7(new String[]{"g"})
        .setUser8(new String[]{"h"})
        .setUser9(new String[]{"i"})
        .setUser10(new String[]{"j"})
        .build("test-metric", 1234)
        .build("test", "localhost");

    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "metrics")
                    .put("timestamp", arrayConstructorEvent.getCreatedTime().toString())
                    .put("service", "test")
                    .put("host", "localhost")
                    .put("metric", "test-metric")
                    .put("user1", Arrays.asList("a"))
                    .put("user2", Arrays.asList("b"))
                    .put("user3", Arrays.asList("c"))
                    .put("user4", Arrays.asList("d"))
                    .put("user5", Arrays.asList("e"))
                    .put("user6", Arrays.asList("f"))
                    .put("user7", Arrays.asList("g"))
                    .put("user8", Arrays.asList("h"))
                    .put("user9", Arrays.asList("i"))
                    .put("user10", Arrays.asList("j"))
                    .put("value", 1234)
                    .build(), arrayConstructorEvent.toMap()
    );

    Assert.assertNotNull(
        new ServiceMetricEvent.Builder()
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
            .build(null, "test-metric", 1234)
            .build("test", "localhost")
            .getCreatedTime()
    );

    Assert.assertNotNull(
        ServiceMetricEvent.builder()
                          .setUser1(new String[]{"a"})
                          .setUser2(new String[]{"b"})
                          .setUser3(new String[]{"c"})
                          .setUser4(new String[]{"d"})
                          .setUser5(new String[]{"e"})
                          .setUser6(new String[]{"f"})
                          .setUser7(new String[]{"g"})
                          .setUser8(new String[]{"h"})
                          .setUser9(new String[]{"i"})
                          .setUser10(new String[]{"j"})
                          .build("test-metric", 1234)
                          .build("test", "localhost")
                          .getCreatedTime()
    );

    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "metrics")
                    .put("timestamp", new DateTime(42).toString())
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
        new ServiceMetricEvent.Builder()
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
            .build(new DateTime(42), "test-metric", 1234)
            .build("test", "localhost")
            .toMap()
    );

    Assert.assertEquals(
        ImmutableMap.<String, Object>builder()
                    .put("feed", "metrics")
                    .put("timestamp", new DateTime(42).toString())
                    .put("service", "test")
                    .put("host", "localhost")
                    .put("metric", "test-metric")
                    .put("user1", Arrays.asList("a"))
                    .put("user2", Arrays.asList("b"))
                    .put("user3", Arrays.asList("c"))
                    .put("user4", Arrays.asList("d"))
                    .put("user5", Arrays.asList("e"))
                    .put("user6", Arrays.asList("f"))
                    .put("user7", Arrays.asList("g"))
                    .put("user8", Arrays.asList("h"))
                    .put("user9", Arrays.asList("i"))
                    .put("user10", Arrays.asList("j"))
                    .put("value", 1234)
                    .build(),
        ServiceMetricEvent.builder()
                          .setUser1(new String[] { "a" })
                          .setUser2(new String[]{"b"})
                          .setUser3(new String[]{"c"})
                          .setUser4(new String[]{"d"})
                          .setUser5(new String[]{"e"})
                          .setUser6(new String[]{"f"})
                          .setUser7(new String[]{"g"})
                          .setUser8(new String[]{"h"})
                          .setUser9(new String[]{"i"})
                          .setUser10(new String[]{"j"})
                          .build(new DateTime(42), "test-metric", 1234)
                          .build("test", "localhost")
                          .toMap()
    );
  }
}
