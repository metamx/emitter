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

  @JsonProperty
  private int flushBytes = 1024 * 1024;

  @NotNull
  @JsonProperty
  private String recipientBaseUrl = null;

  @NotNull
  @JsonProperty
  private String username = null;

  @NotNull
  @JsonProperty
  private String password = null;

  @JsonProperty
  private String contentEncoding = null;

  @JsonProperty
  private int maxQueueBytes = 0;

  public HttpEmitterConfig(){}

  public HttpEmitterConfig(long flushMillis, int flushCount, int flushBytes, String recipientBaseUrl)
  {
    this(flushMillis, flushCount, flushBytes, recipientBaseUrl, null, null, null, 0);
  }

  public HttpEmitterConfig(
      long flushMillis,
      int flushCount,
      int flushBytes,
      String recipientBaseUrl,
      String username,
      String password,
      String contentEncoding,
      int maxQueueBytes
  )
  {
    this.flushMillis = flushMillis;
    this.flushCount = flushCount;
    this.flushBytes = flushBytes;
    this.recipientBaseUrl = recipientBaseUrl;
    this.username = username;
    this.password = password;
    this.contentEncoding = contentEncoding;
    this.maxQueueBytes = maxQueueBytes;
  }

  public long getFlushMillis()
  {
    return flushMillis;
  }

  public int getFlushCount()
  {
    return flushCount;
  }

  public int getFlushBytes()
  {
    return flushBytes;
  }

  public String getRecipientBaseUrl()
  {
    return recipientBaseUrl;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  public String getContentEncoding()
  {
    return contentEncoding;
  }

  public long getMaxQueueBytes()
  {
    return maxQueueBytes;
  }
}
