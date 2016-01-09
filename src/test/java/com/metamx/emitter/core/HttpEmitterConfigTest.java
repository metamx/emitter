/*
 * Copyright 2012 - 2015 Metamarkets Group Inc.
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
import org.junit.Assert;
import org.junit.Test;

public class HttpEmitterConfigTest
{
  @Test
  public void testDefaults() throws Exception
  {
    final ObjectMapper objectMapper = new ObjectMapper();
    final HttpEmitterConfig config = objectMapper.readValue("{}", HttpEmitterConfig.class);

    Assert.assertEquals(60000, config.getFlushMillis());
    Assert.assertEquals(500, config.getFlushCount());
    Assert.assertNull(config.getRecipientBaseUrl());
    Assert.assertEquals(null, config.getBasicAuthentication());
    Assert.assertEquals(BatchingStrategy.ARRAY, config.getBatchingStrategy());
    Assert.assertEquals(5 * 1024 * 1024, config.getMaxBatchSize());
    Assert.assertEquals(250 * 1024 * 1024, config.getMaxBufferSize());
    Assert.assertEquals(Long.MAX_VALUE, config.getFlushTimeOut());
  }

  @Test
  public void testSettingEverything() throws Exception
  {
    final ObjectMapper objectMapper = new ObjectMapper();
    final HttpEmitterConfig config =
        objectMapper.readValue(
            objectMapper.writeValueAsString(
                objectMapper.readValue(
                    "{\n"
                    + "  \"flushMillis\": 1,\n"
                    + "  \"flushCount\": 2,\n"
                    + "  \"flushTimeOut\": 1000,\n"
                    + "  \"recipientBaseUrl\": \"http://example.com/\",\n"
                    + "  \"basicAuthentication\": \"a:b\",\n"
                    + "  \"batchingStrategy\": \"NEWLINES\",\n"
                    + "  \"maxBatchSize\": 4,\n"
                    + "  \"maxBufferSize\": 8\n"
                    + "}",
                    HttpEmitterConfig.class
                )
            ),
            HttpEmitterConfig.class
        );

    Assert.assertEquals(1, config.getFlushMillis());
    Assert.assertEquals(2, config.getFlushCount());
    Assert.assertEquals("http://example.com/", config.getRecipientBaseUrl());
    Assert.assertEquals("a:b", config.getBasicAuthentication());
    Assert.assertEquals(BatchingStrategy.NEWLINES, config.getBatchingStrategy());
    Assert.assertEquals(4, config.getMaxBatchSize());
    Assert.assertEquals(8, config.getMaxBufferSize());
    Assert.assertEquals(1000, config.getFlushTimeOut());
  }
}
