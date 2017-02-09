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
  private static final long DEFAULT_FLUSH_MILLIS = 60 * 1000;
  private static final int DEFAULT_FLUSH_COUNTS = 500;
  private static final String DEFAULT_RECIPIENT_BASE_URL = null;
  private static final int DEFAULT_MAX_BATCH_SIZE = 5 * 1024 * 1024;
  private static final long DEFAULT_MAX_BUFFER_SIZE = 250 * 1024 * 1024;
  private static final long DEFAULT_FLUSH_TIME_OUT = Long.MAX_VALUE; // do not time out in case flushTimeOut is not set
  private static final String DEFAULT_BASIC_AUTHENTICATION = null;
  private static final BatchingStrategy DEFAULT_BATCHING_STRATEGY = BatchingStrategy.ARRAY;
  private static final ContentEncoding DEFAULT_CONTENT_ENCODING = null;

  @Min(1)
  @JsonProperty
  private long flushMillis = DEFAULT_FLUSH_MILLIS;

  @Min(0)
  @JsonProperty
  private int flushCount = DEFAULT_FLUSH_COUNTS;

  @Min(0)
  @JsonProperty
  private long flushTimeOut = DEFAULT_FLUSH_TIME_OUT;

  @NotNull
  @JsonProperty
  private String recipientBaseUrl = DEFAULT_RECIPIENT_BASE_URL;

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

  @JsonProperty
  private ContentEncoding contentEncoding = DEFAULT_CONTENT_ENCODING;

  /**
   * For JSON deserialization only. In other cases use {@link Builder}
   */
  public HttpEmitterConfig() {}

  /**
   * @deprecated use {@link Builder}
   */
  @Deprecated
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

  /**
   * @deprecated use {@link Builder}
   */
  @Deprecated
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

  /**
   * @deprecated use {@link Builder}
   */
  @Deprecated
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

  /**
   * @deprecated use {@link Builder}
   */
  @Deprecated
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
    this(
        flushMillis,
        flushCount,
        flushTimeOut,
        recipientBaseUrl,
        basicAuthentication,
        batchingStrategy,
        maxBatchSize,
        maxBufferSize,
        DEFAULT_CONTENT_ENCODING
    );
  }

  private HttpEmitterConfig(
      long flushMillis,
      int flushCount,
      long flushTimeOut,
      String recipientBaseUrl,
      String basicAuthentication,
      BatchingStrategy batchingStrategy,
      int maxBatchSize,
      long maxBufferSize,
      ContentEncoding contentEncoding
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
    this.contentEncoding = contentEncoding;
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

  public ContentEncoding getContentEncoding() {
    return contentEncoding;
  }

  public static class Builder
  {
    private long flushMillis = DEFAULT_FLUSH_MILLIS;
    private int flushCount = DEFAULT_FLUSH_COUNTS;
    private String recipientBaseUrl = DEFAULT_RECIPIENT_BASE_URL;
    private long flushTimeOut = DEFAULT_FLUSH_TIME_OUT;
    private String basicAuthentication = DEFAULT_BASIC_AUTHENTICATION;
    private BatchingStrategy batchingStrategy = DEFAULT_BATCHING_STRATEGY;
    private int maxBatchSize = DEFAULT_MAX_BATCH_SIZE;
    private long maxBufferSize = DEFAULT_MAX_BUFFER_SIZE;
    private ContentEncoding contentEncoding = DEFAULT_CONTENT_ENCODING;

    public Builder(String recipientBaseUrl) {
      this.recipientBaseUrl = recipientBaseUrl;
    }

    public Builder setFlushMillis(long flushMillis)
    {
      this.flushMillis = flushMillis;
      return this;
    }

    public Builder setFlushCount(int flushCount)
    {
      this.flushCount = flushCount;
      return this;
    }

    public Builder setFlushTimeOut(long flushTimeOut)
    {
      this.flushTimeOut = flushTimeOut;
      return this;
    }

    public Builder setBasicAuthentication(String basicAuthentication)
    {
      this.basicAuthentication = basicAuthentication;
      return this;
    }

    public Builder setBatchingStrategy(BatchingStrategy batchingStrategy)
    {
      this.batchingStrategy = batchingStrategy;
      return this;
    }

    public Builder setMaxBatchSize(int maxBatchSize)
    {
      this.maxBatchSize = maxBatchSize;
      return this;
    }

    public Builder setMaxBufferSize(long maxBufferSize)
    {
      this.maxBufferSize = maxBufferSize;
      return this;
    }

    public Builder setContentEncoding(ContentEncoding contentEncoding)
    {
      this.contentEncoding = contentEncoding;
      return this;
    }

    public HttpEmitterConfig build()
    {
      return new HttpEmitterConfig(
          flushMillis,
          flushCount,
          flushTimeOut,
          recipientBaseUrl,
          basicAuthentication,
          batchingStrategy,
          maxBatchSize,
          maxBufferSize,
          contentEncoding
      );
    }
  }
}
