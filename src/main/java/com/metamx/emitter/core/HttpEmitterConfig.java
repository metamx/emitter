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
  private static final int DEFAULT_MAX_BATCH_SIZE = 5 * 1024 * 1024;
  private static final long DEFAULT_MAX_BUFFER_SIZE = 250 * 1024 * 1024;
  private static final long DEFAULT_FLUSH_TIME_OUT = Long.MAX_VALUE; // do not time out in case flushTimeOut is not set
  private static final String DEFAULT_BASIC_AUTHENTICATION = null;
  private static final BatchingStrategy DEFAULT_BATCHING_STRATEGY = BatchingStrategy.ARRAY;

  @Min(1)
  @JsonProperty
  private long flushMillis = 60 * 1000;

  @Min(0)
  @JsonProperty
  private int flushCount = 500;

  @Min(0)
  @JsonProperty
  private long flushTimeOut = DEFAULT_FLUSH_TIME_OUT;

  @NotNull
  @JsonProperty
  private String recipientBaseUrl = null;

  @JsonProperty
  private String basicAuthentication = DEFAULT_BASIC_AUTHENTICATION;

  @JsonProperty
  private BatchingStrategy batchingStrategy = DEFAULT_BATCHING_STRATEGY;

  @Min(0)
  @JsonProperty
  private int maxBatchSize = DEFAULT_MAX_BATCH_SIZE;

  @Min(0)
  @JsonProperty
  private long maxBufferSize = DEFAULT_MAX_BUFFER_SIZE;

  public HttpEmitterConfig() {}

  public HttpEmitterConfig(
      long flushMillis,
      int flushCount,
      String recipientBaseUrl
  )
  {
    this(
        flushMillis,
        flushCount,
        DEFAULT_FLUSH_TIME_OUT,
        recipientBaseUrl,
        DEFAULT_BASIC_AUTHENTICATION,
        DEFAULT_BATCHING_STRATEGY,
        DEFAULT_MAX_BATCH_SIZE,
        DEFAULT_MAX_BUFFER_SIZE
    );
  }

  public HttpEmitterConfig(
      long flushMillis,
      int flushCount,
      String recipientBaseUrl,
      int maxBatchSize,
      long maxBufferSize
  )
  {
    this(
        flushMillis,
        flushCount,
        DEFAULT_FLUSH_TIME_OUT,
        recipientBaseUrl,
        DEFAULT_BASIC_AUTHENTICATION,
        DEFAULT_BATCHING_STRATEGY,
        maxBatchSize,
        maxBufferSize
    );
  }

  public HttpEmitterConfig(
      long flushMillis,
      int flushCount,
      String recipientBaseUrl,
      String basicAuthentication,
      BatchingStrategy batchingStrategy,
      int maxBatchSize,
      long maxBufferSize
  )
  {
    this(
        flushMillis,
        flushCount,
        DEFAULT_FLUSH_TIME_OUT,
        recipientBaseUrl,
        basicAuthentication,
        batchingStrategy,
        maxBatchSize,
        maxBufferSize
    );
  }

  public HttpEmitterConfig(
      long flushMillis,
      int flushCount,
      long flushTimeOut,
      String recipientBaseUrl,
      String basicAuthentication,
      BatchingStrategy batchingStrategy,
      int maxBatchSize,
      long maxBufferSize
  )
  {
    this.flushMillis = flushMillis;
    this.flushCount = flushCount;
    this.flushTimeOut = flushTimeOut;
    this.recipientBaseUrl = recipientBaseUrl;
    this.basicAuthentication = basicAuthentication;
    this.batchingStrategy = batchingStrategy;
    this.maxBatchSize = maxBatchSize;
    this.maxBufferSize = maxBufferSize;
  }

  public long getFlushMillis()
  {
    return flushMillis;
  }

  public int getFlushCount()
  {
    return flushCount;
  }

  public long getFlushTimeOut() {
    return flushTimeOut;
  }

  public String getRecipientBaseUrl()
  {
    return recipientBaseUrl;
  }

  public String getBasicAuthentication()
  {
    return basicAuthentication;
  }

  public BatchingStrategy getBatchingStrategy()
  {
    return batchingStrategy;
  }

  public int getMaxBatchSize()
  {
    return maxBatchSize;
  }

  public long getMaxBufferSize()
  {
    return maxBufferSize;
  }
}
