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
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.metamx.emitter.service.UnitEvent;
import com.metamx.http.client.GoHandler;
import com.metamx.http.client.GoHandlers;
import com.metamx.http.client.MockHttpClient;
import com.metamx.http.client.Request;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 */
public class EmitterTest
{
  private static final ObjectMapper jsonMapper = new ObjectMapper();
  public static String TARGET_URL = "http://metrics.foo.bar/";

  MockHttpClient httpClient;
  HttpPostEmitter emitter;

  @Before
  public void setUp() throws Exception
  {
    httpClient = new MockHttpClient();
  }

  @After
  public void tearDown() throws Exception
  {
    if (emitter != null) {
      emitter.close();
    }
  }

  private HttpPostEmitter timeBasedEmitter(long timeInMillis)
  {
    HttpPostEmitter emitter = new HttpPostEmitter(
        new HttpEmitterConfig(timeInMillis, Integer.MAX_VALUE, TARGET_URL),
        httpClient,
        true,
        jsonMapper
    );
    emitter.start();
    return emitter;
  }

  private HttpPostEmitter sizeBasedEmitter(int size)
  {
    HttpPostEmitter emitter = new HttpPostEmitter(
        new HttpEmitterConfig(Long.MAX_VALUE, size, TARGET_URL),
        httpClient,
        true,
        jsonMapper
    );
    emitter.start();
    return emitter;
  }

  @Test
  public void testSanity() throws Exception
  {
    final List<UnitEvent> events = Arrays.asList(
        new UnitEvent("test", 1),
        new UnitEvent("test", 2)
    );
    emitter = sizeBasedEmitter(2);

    httpClient.setGoHandler(
        new GoHandler()
        {
          @Override
          public <Intermediate, Final> ListenableFuture<Final> go(Request<Intermediate, Final> request) throws Exception
          {
            Assert.assertEquals(new URL(TARGET_URL), request.getUrl());
            Assert.assertEquals(
                ImmutableList.of("application/json"),
                request.getHeaders().get(HttpHeaders.Names.CONTENT_TYPE)
            );
            Assert.assertEquals(
                jsonMapper.convertValue(events.iterator(), List.class),
                jsonMapper.readValue(request.getContent().toString(Charsets.UTF_8), List.class)
            );

            return Futures.immediateFuture(null);
          }
        }.times(1)
    );

    for (UnitEvent event : events) {
      emitter.emit(event);
    }
    waitForEmission(emitter);
    Assert.assertTrue(httpClient.succeeded());

    closeNoFlush(emitter);
  }

  @Test
  public void testSizeBasedEmission() throws Exception
  {
    emitter = sizeBasedEmitter(3);

    httpClient.setGoHandler(GoHandlers.failingHandler());
    emitter.emit(new UnitEvent("test", 1));
    emitter.emit(new UnitEvent("test", 2));

    httpClient.setGoHandler(GoHandlers.passingHandler(null).times(1));
    emitter.emit(new UnitEvent("test", 3));
    waitForEmission(emitter);

    httpClient.setGoHandler(GoHandlers.failingHandler());
    emitter.emit(new UnitEvent("test", 4));
    emitter.emit(new UnitEvent("test", 5));

    closeAndExpectFlush(emitter);
  }

  @Test
  public void testTimeBasedEmission() throws Exception
  {
    final int timeBetweenEmissions = 100;
    emitter = timeBasedEmitter(timeBetweenEmissions);

    final CountDownLatch latch = new CountDownLatch(1);

    httpClient.setGoHandler(
        new GoHandler()
        {
          @Override
          public <Intermediate, Final> ListenableFuture<Final> go(Request<Intermediate, Final> intermediateFinalRequest)
              throws Exception
          {
            latch.countDown();
            return Futures.immediateFuture(null);
          }
        }.times(1)
    );

    long emitTime = System.currentTimeMillis();
    emitter.emit(new UnitEvent("test", 1));

    latch.await();
    long timeWaited = System.currentTimeMillis() - emitTime;
    Assert.assertTrue(
        String.format("timeWaited[%s] !< %s", timeWaited, timeBetweenEmissions * 2),
        timeWaited < timeBetweenEmissions * 2
    );

    waitForEmission(emitter);

    final CountDownLatch thisLatch = new CountDownLatch(1);
    httpClient.setGoHandler(
        new GoHandler()
        {
          @Override
          public <Intermediate, Final> ListenableFuture<Final> go(Request<Intermediate, Final> intermediateFinalRequest)
              throws Exception
          {
            thisLatch.countDown();
            return Futures.immediateFuture(null);
          }
        }.times(1)
    );

    emitTime = System.currentTimeMillis();
    emitter.emit(new UnitEvent("test", 2));

    thisLatch.await();
    timeWaited = System.currentTimeMillis() - emitTime;
    Assert.assertTrue(
        String.format("timeWaited[%s] !< %s", timeWaited, timeBetweenEmissions * 2),
        timeWaited < timeBetweenEmissions * 2
    );

    waitForEmission(emitter);
    closeNoFlush(emitter);
  }

  @Test
  public void testFailedEmission() throws Exception
  {
    emitter = sizeBasedEmitter(1);

    httpClient.setGoHandler(
        new GoHandler()
        {
          @Override
          public <Intermediate, Final> ListenableFuture<Final> go(Request<Intermediate, Final> request)
              throws Exception
          {
            Assert.assertNull(
                request.getHandler()
                       .handleResponse(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST))
                       .getObj()
            );
            return Futures.immediateFuture(null);
          }
        }.times(1)
    );
    emitter.emit(new UnitEvent("test", 1));
    waitForEmission(emitter);
    Assert.assertTrue(httpClient.succeeded());

    httpClient.setGoHandler(
        new GoHandler()
        {
          @Override
          public <Intermediate, Final> ListenableFuture<Final> go(Request<Intermediate, Final> request)
              throws Exception
          {
            Assert.assertEquals(
                jsonMapper.convertValue(
                    ImmutableList.of(new UnitEvent("test", 1).toMap(), new UnitEvent("test", 2).toMap()), List.class
                ),
                jsonMapper.readValue(request.getContent().toString(Charsets.UTF_8), List.class)
            );

            return Futures.immediateFuture(null);
          }
        }.times(1)
    );

    emitter.emit(new UnitEvent("test", 2));
    waitForEmission(emitter);
    Assert.assertTrue(httpClient.succeeded());
    closeNoFlush(emitter);
  }

  private void closeAndExpectFlush(Emitter emitter) throws IOException
  {
    httpClient.setGoHandler(GoHandlers.passingHandler(null).times(1));
    emitter.close();
  }


  private void closeNoFlush(Emitter emitter) throws IOException
  {
    emitter.close();
  }

  private void waitForEmission(HttpPostEmitter emitter) throws InterruptedException
  {
    final CountDownLatch latch = new CountDownLatch(1);
    emitter.getExec().execute(
        new Runnable()
        {
          @Override
          public void run()
          {
            latch.countDown();
          }
        }
    );

    if(! latch.await(10, TimeUnit.SECONDS)) {
      Assert.fail("latch await() did not complete in 10 seconds");
    }
  }
}
