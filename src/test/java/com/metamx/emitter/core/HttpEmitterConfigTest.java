package com.metamx.emitter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Properties;

public class HttpEmitterConfigTest
{
  @Test
  public void testDefaults()
  {
    final Properties props = new Properties();
    props.put("com.metamx.emitter.http.url", "http://example.com/");

    final ObjectMapper objectMapper = new ObjectMapper();
    final HttpEmitterConfig config = objectMapper.convertValue(Emitters.makeHttpMap(props), HttpEmitterConfig.class);

    Assert.assertEquals(60000, config.getFlushMillis());
    Assert.assertEquals(300, config.getFlushCount());
    Assert.assertEquals("http://example.com/", config.getRecipientBaseUrl());
    Assert.assertEquals(null, config.getBasicAuthentication());
    Assert.assertEquals(BatchingStrategy.ARRAY, config.getBatchingStrategy());
    Assert.assertEquals(5 * 1024 * 1024, config.getMaxBatchSize());
    Assert.assertEquals(250 * 1024 * 1024, config.getMaxBufferSize());
  }

  @Test
  public void testSettingEverything()
  {
    final Properties props = new Properties();
    props.setProperty("com.metamx.emitter.flushMillis", "1");
    props.setProperty("com.metamx.emitter.flushCount", "2");
    props.setProperty("com.metamx.emitter.http.url", "http://example.com/");
    props.setProperty("com.metamx.emitter.http.basicAuthentication", "a:b");
    props.setProperty("com.metamx.emitter.http.batchingStrategy", "newlines");
    props.setProperty("com.metamx.emitter.http.maxBatchSize", "4");
    props.setProperty("com.metamx.emitter.http.maxBufferSize", "8");

    final ObjectMapper objectMapper = new ObjectMapper();
    final HttpEmitterConfig config = objectMapper.convertValue(Emitters.makeHttpMap(props), HttpEmitterConfig.class);

    Assert.assertEquals(1, config.getFlushMillis());
    Assert.assertEquals(2, config.getFlushCount());
    Assert.assertEquals("http://example.com/", config.getRecipientBaseUrl());
    Assert.assertEquals("a:b", config.getBasicAuthentication());
    Assert.assertEquals(BatchingStrategy.NEWLINES, config.getBatchingStrategy());
    Assert.assertEquals(4, config.getMaxBatchSize());
    Assert.assertEquals(8, config.getMaxBufferSize());
  }
}
