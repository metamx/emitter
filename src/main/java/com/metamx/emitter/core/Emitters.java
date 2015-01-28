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

package com.metamx.emitter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.metamx.common.IAE;
import com.metamx.common.ISE;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.logger.Logger;
import com.metamx.http.client.HttpClient;

import java.util.Map;
import java.util.Properties;

public class Emitters
{
  private static final Logger log = new Logger(Emitters.class);

  private static final String LOG_EMITTER_PROP = "com.metamx.emitter.logging";
  private static final String HTTP_EMITTER_PROP = "com.metamx.emitter.http";

  public static Emitter create(Properties props, HttpClient httpClient, Lifecycle lifecycle)
  {
    return create(props, httpClient, new ObjectMapper(), lifecycle);
  }

  public static Emitter create(Properties props, HttpClient httpClient, ObjectMapper jsonMapper, Lifecycle lifecycle)
  {
    Map<String, Object> jsonified = Maps.newHashMap();

    if (props.getProperty(LOG_EMITTER_PROP) != null) {
      jsonified.put("logging", makeLoggingMap(props));
    }
    else if (props.getProperty(HTTP_EMITTER_PROP) != null) {
      jsonified.put("http", makeHttpMap(props));
    }
    else {
      throw new ISE("Unknown type of emitter. Please set [%s] or [%s]", LOG_EMITTER_PROP, HTTP_EMITTER_PROP);
    }

    return jsonMapper.convertValue(jsonified, EmitterBuilder.class).build(jsonMapper, httpClient, lifecycle);
  }

  // Package-visible for unit tests

  static Map<String, Object> makeHttpMap(Properties props)
  {
    Map<String, Object> httpMap = Maps.newHashMap();

    final String urlProperty = "com.metamx.emitter.http.url";

    final String baseUrl = props.getProperty(urlProperty);
    if (baseUrl == null) {
      throw new IAE("Property[%s] must be set", urlProperty);
    }

    httpMap.put("recipientBaseUrl", baseUrl);
    httpMap.put("flushMillis", Long.parseLong(props.getProperty("com.metamx.emitter.flushMillis", "60000")));
    httpMap.put("flushCount", Integer.parseInt(props.getProperty("com.metamx.emitter.flushCount", "300")));
    if (props.containsKey("com.metamx.emitter.http.basicAuthentication")) {
      httpMap.put("basicAuthentication", props.getProperty("com.metamx.emitter.http.basicAuthentication"));
    }
    if (props.containsKey("com.metamx.emitter.http.batchingStrategy")) {
      httpMap.put("batchingStrategy", props.getProperty("com.metamx.emitter.http.batchingStrategy").toUpperCase());
    }
    if (props.containsKey("com.metamx.emitter.http.maxBatchSize")) {
      httpMap.put("maxBatchSize", Integer.parseInt(props.getProperty("com.metamx.emitter.http.maxBatchSize")));
    }
    if (props.containsKey("com.metamx.emitter.http.maxBufferSize")) {
      httpMap.put("maxBufferSize", Long.parseLong(props.getProperty("com.metamx.emitter.http.maxBufferSize")));
    }
    return httpMap;
  }

  // Package-visible for unit tests
  static Map<String, Object> makeLoggingMap(Properties props)
  {
    Map<String, Object> loggingMap = Maps.newHashMap();

    loggingMap.put(
        "loggerClass", props.getProperty("com.metamx.emitter.logging.class", LoggingEmitter.class.getName())
    );
    loggingMap.put(
        "logLevel", props.getProperty("com.metamx.emitter.logging.level", "debug")
    );
    return loggingMap;
  }
}
