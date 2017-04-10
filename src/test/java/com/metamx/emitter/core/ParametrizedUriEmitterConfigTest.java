package com.metamx.emitter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import junit.framework.Assert;
import org.junit.Test;

public class ParametrizedUriEmitterConfigTest
{
  @Test
  public void testDefaults()
  {
    final Properties props = new Properties();

    final ObjectMapper objectMapper = new ObjectMapper();
    final ParametrizedUriEmitterConfig paramConfig = objectMapper.convertValue(Emitters.makeCustomFactoryMap(props), ParametrizedUriEmitterConfig.class);
    final HttpEmitterConfig config = paramConfig.buildHttpEmitterConfig("http://example.com/topic");

    Assert.assertEquals(60000, config.getFlushMillis());
    Assert.assertEquals(500, config.getFlushCount());
    Assert.assertEquals("http://example.com/topic", config.getRecipientBaseUrl());
    Assert.assertEquals(null, config.getBasicAuthentication());
    Assert.assertEquals(BatchingStrategy.ARRAY, config.getBatchingStrategy());
    Assert.assertEquals(5 * 1024 * 1024, config.getMaxBatchSize());
    Assert.assertEquals(250 * 1024 * 1024, config.getMaxBufferSize());
    Assert.assertEquals(Long.MAX_VALUE, config.getFlushTimeOut());
  }

  @Test
  public void testSettingEverything()
  {
    final Properties props = new Properties();
    props.setProperty("com.metamx.emitter.flushMillis", "1");
    props.setProperty("com.metamx.emitter.flushCount", "2");
    props.setProperty("com.metamx.emitter.basicAuthentication", "a:b");
    props.setProperty("com.metamx.emitter.batchingStrategy", "NEWLINES");
    props.setProperty("com.metamx.emitter.maxBatchSize", "4");
    props.setProperty("com.metamx.emitter.maxBufferSize", "8");
    props.setProperty("com.metamx.emitter.flushTimeOut", "1000");

    final ObjectMapper objectMapper = new ObjectMapper();
    final ParametrizedUriEmitterConfig paramConfig = objectMapper.convertValue(Emitters.makeCustomFactoryMap(props), ParametrizedUriEmitterConfig.class);
    final HttpEmitterConfig config = paramConfig.buildHttpEmitterConfig("http://example.com/topic");

    Assert.assertEquals(1, config.getFlushMillis());
    Assert.assertEquals(2, config.getFlushCount());
    Assert.assertEquals("http://example.com/topic", config.getRecipientBaseUrl());
    Assert.assertEquals("a:b", config.getBasicAuthentication());
    Assert.assertEquals(BatchingStrategy.NEWLINES, config.getBatchingStrategy());
    Assert.assertEquals(4, config.getMaxBatchSize());
    Assert.assertEquals(8, config.getMaxBufferSize());
    Assert.assertEquals(1000, config.getFlushTimeOut());
  }
}
