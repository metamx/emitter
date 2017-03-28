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
import com.metamx.http.client.HttpClient;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class EmitterBuilder
{
  @JsonProperty("http")
  private HttpEmitterConfig httpEmitterConfig = null;

  @JsonProperty("logging")
  private LoggingEmitterConfig loggingEmitterConfig = null;

  @JsonProperty("parametrized")
  private ParametrizedUriHttpEmitterConfig parametrizedUriHttpEmitterConfig = null;

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

  public ParametrizedUriHttpEmitterConfig getParametrizedUriHttpEmitterConfig()
  {
    return parametrizedUriHttpEmitterConfig;
  }

  public void setParametrizedUriHttpEmitterConfig(ParametrizedUriHttpEmitterConfig parametrizedUriHttpEmitterConfig)
  {
    this.parametrizedUriHttpEmitterConfig = parametrizedUriHttpEmitterConfig;
  }

  public Emitter build(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    if (loggingEmitterConfig != null) {
      return buildLogging(objectMapper, lifecycle);
    } else if (httpEmitterConfig != null) {
      return buildHttp(httpClient, objectMapper, lifecycle);
    } else if (parametrizedUriHttpEmitterConfig != null) {
      return buildParametrized(httpClient, objectMapper, lifecycle);
    } else {
      return buildNoop(lifecycle);
    }
  }

  public Emitter buildNoop(Lifecycle lifecycle)
  {
    Emitter retVal = new NoopEmitter();
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }

  public Emitter buildLogging(ObjectMapper objectMapper, Lifecycle lifecycle)
  {
    Emitter retVal = new LoggingEmitter(loggingEmitterConfig, objectMapper);
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }

  public Emitter buildHttp(HttpClient httpClient, ObjectMapper objectMapper, Lifecycle lifecycle)
  {
    Emitter retVal = new HttpPostEmitter(httpEmitterConfig, httpClient, objectMapper);
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }

  public Emitter buildParametrized(HttpClient httpClient, ObjectMapper objectMapper, Lifecycle lifecycle)
  {
    String baseUri = parametrizedUriHttpEmitterConfig.getHttpEmitterProperties().get("recipientBaseUrl").toString();
    ParametrizedUriExtractor parametrizedUriExtractor = new ParametrizedUriExtractor(baseUri);
    URIExtractor uriExtractor = parametrizedUriExtractor;
    Set<String> onlyFeedParam = new HashSet<String>();
    onlyFeedParam.add("feed");
    if (parametrizedUriExtractor.params.equals(onlyFeedParam)) {
      uriExtractor = new FeedUriExtractor(baseUri.replace("{feed}", "%s"));
    }
    Emitter retVal = new ParametrizedUriHttpPostEmitter(
        parametrizedUriHttpEmitterConfig,
        httpClient,
        objectMapper,
        uriExtractor
    );
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
