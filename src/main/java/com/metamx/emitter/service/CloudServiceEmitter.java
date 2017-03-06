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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.metamx.emitter.core.Emitter;

public class CloudServiceEmitter extends ServiceEmitter
{
  private static final String ENV_DIMENSION = "env";
  private static final String LOCATION_DIMENSION = "location";
  private static final String ZONE_DIMENSION = "zone";

  public CloudServiceEmitter(
      String service,
      String host,
      String env,
      String location,
      String zone,
      Emitter emitter
  )
  {
    this(service, host, env, location, zone, emitter, ImmutableMap.<String, String>of());
  }

  public CloudServiceEmitter(
      String service,
      String host,
      String env,
      String location,
      String zone,
      Emitter emitter,
      ImmutableMap<String, String> otherServiceDimensions
  )
  {
    super(service, host, emitter, new ImmutableMap.Builder<String, String>()
        .put(ENV_DIMENSION, Preconditions.checkNotNull(env))
        .put(LOCATION_DIMENSION, Preconditions.checkNotNull(location))
        .put(ZONE_DIMENSION, Preconditions.checkNotNull(zone))
        .putAll(otherServiceDimensions)
        .build());
  }

  public String getEnv() {
    return serviceDimensions.get(ENV_DIMENSION);
  }

  public String getLocation() {
    return serviceDimensions.get(LOCATION_DIMENSION);
  }

  public String getZone() {
    return serviceDimensions.get(ZONE_DIMENSION);
  }
}

