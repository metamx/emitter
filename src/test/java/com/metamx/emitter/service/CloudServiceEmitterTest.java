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
import org.junit.Assert;
import org.junit.Test;

public class CloudServiceEmitterTest
{
  @Test
  public void testBaseConstructorParams() throws Exception
  {
    CloudServiceEmitter cse = new CloudServiceEmitter("service", "host", "env", "location", "zone", null);
    Assert.assertEquals("Services do not match", "service", cse.getService());
    Assert.assertEquals("Hosts do not match", "host", cse.getHost());
    Assert.assertEquals("Envs do not match", "env", cse.getEnv());
    Assert.assertEquals("Locations do not match", "location", cse.getLocation());
    Assert.assertEquals("Zones do not match", "zone", cse.getZone());
  }

  @Test
  public void testOtherDimensions() throws Exception
  {
    CloudServiceEmitter cse = new CloudServiceEmitter(
        "service",
        "host",
        "env",
        "location",
        "zone",
        null,
        ImmutableMap.<String, String>of("test", "value")
    );
    Assert.assertEquals("Other dimensions do not match", "value", cse.serviceDimensions.get("test"));
  }

}
