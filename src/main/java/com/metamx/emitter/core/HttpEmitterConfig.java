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

import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 */
public class HttpEmitterConfig
{
  @Min(1)
  @JsonProperty
  private long flushMillis = 60 * 1000;

  @Min(0)
  @JsonProperty
  private int flushCount = 500;

  @NotNull
  @JsonProperty
  private String recipientBaseUrl = null;

  public HttpEmitterConfig(){}

  public HttpEmitterConfig(
      long flushMillis,
      int flushCount,
      String recipientBaseUrl
  )
  {
    this.flushMillis = flushMillis;
    this.flushCount = flushCount;
    this.recipientBaseUrl = recipientBaseUrl;
  }

  public long getFlushMillis()
  {
    return flushMillis;
  }

  public int getFlushCount()
  {
    return flushCount;
  }

  public String getRecipientBaseUrl()
  {
    return recipientBaseUrl;
  }
}
