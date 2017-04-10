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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.factory.EmitterFactory;
import com.metamx.emitter.core.factory.HttpEmitterFactory;
import com.metamx.emitter.core.factory.LoggingEmitterFactory;
import com.metamx.emitter.core.factory.NoopEmiterFactory;
import com.metamx.http.client.HttpClient;

/**
 * @deprecated use implementation of {@link EmitterFactory}
 */
@Deprecated
public class EmitterBuilder
{
  @JsonProperty("http")
  private HttpEmitterConfig httpEmitterConfig = null;

  @JsonProperty("logging")
  private LoggingEmitterConfig loggingEmitterConfig = null;

  public HttpEmitterConfig getHttpEmitterConfig()
  {
    return httpEmitterConfig;
  }

  public void setHttpEmitterConfig(HttpEmitterConfig httpEmitterConfig)
  {
    this.httpEmitterConfig = httpEmitterConfig;
  }

  public LoggingEmitterConfig getLoggingEmitterConfig()
  {
    return loggingEmitterConfig;
  }

  public void setLoggingEmitterConfig(LoggingEmitterConfig loggingEmitterConfig)
  {
    this.loggingEmitterConfig = loggingEmitterConfig;
  }

  public Emitter build(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    if (loggingEmitterConfig != null) {
      return buildLogging(objectMapper, lifecycle);
    } else if (httpEmitterConfig != null) {
      return buildHttp(httpClient, objectMapper, lifecycle);
    } else {
      return buildNoop(lifecycle);
    }
  }

  /**
   * @deprecated use {@link NoopEmiterFactory#makeEmitter}
   */
  public Emitter buildNoop(Lifecycle lifecycle)
  {
    return new NoopEmiterFactory().makeEmitter(lifecycle);
  }

  /**
   * @deprecated use {@link LoggingEmitterFactory#makeEmitter}
   */
  @Deprecated
  public Emitter buildLogging(ObjectMapper objectMapper, Lifecycle lifecycle)
  {
    Emitter retVal = new LoggingEmitter(loggingEmitterConfig, objectMapper);
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }

  /**
   * @deprecated use {@link HttpEmitterFactory#makeEmitter}
   */
  @Deprecated
  public Emitter buildHttp(HttpClient httpClient, ObjectMapper objectMapper, Lifecycle lifecycle)
  {
    Emitter retVal = new HttpPostEmitter(httpEmitterConfig, httpClient, objectMapper);
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
