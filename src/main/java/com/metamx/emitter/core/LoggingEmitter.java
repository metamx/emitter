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

/**
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.ISE;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import com.metamx.emitter.service.AlertEvent;
import com.metamx.emitter.service.ServiceMetricEvent;

import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
public class LoggingEmitter implements Emitter
{
  private final Logger log;
  private final Level level;
  private final EventsToLog eventsToLog;
  private final ObjectMapper jsonMapper;

  private final AtomicBoolean started = new AtomicBoolean(false);

  public LoggingEmitter(LoggingEmitterConfig config, ObjectMapper jsonMapper)
  {
    this(
        new Logger(config.getLoggerClass()),
        Level.toLevel(config.getLogLevel()),
        EventsToLog.toEventsToLog(config.getEventsToLog()),
        jsonMapper
    );
  }

  public LoggingEmitter(Logger log, Level level, EventsToLog eventsToLog, ObjectMapper jsonMapper)
  {
    this.log = log;
    this.level = level;
    this.eventsToLog = eventsToLog;
    this.jsonMapper = jsonMapper;
  }

  @Override
  @LifecycleStart
  public void start()
  {
    final boolean alreadyStarted = started.getAndSet(true);
    if (!alreadyStarted) {
      final String message = "Start: started [%s]";
      switch (level) {
        case TRACE:
          if (log.isTraceEnabled()) {
            log.trace(message, started.get());
          }
          break;
        case DEBUG:
          if (log.isDebugEnabled()) {
            log.debug(message, started.get());
          }
          break;
        case INFO:
          if (log.isInfoEnabled()) {
            log.info(message, started.get());
          }
          break;
        case WARN:
          log.warn(message, started.get());
          break;
        case ERROR:
          log.error(message, started.get());
          break;
      }
    }
  }

  @Override
  public void emit(Event event)
  {
    if (!started.get()) {
      throw new RejectedExecutionException("Service not started.");
    }

    switch (eventsToLog) {
      case ALL:
        emitInternal(event);
        break;
      case ALERTS:
        if (event instanceof AlertEvent) {
          emitInternal(event);
        }
        break;
      case METRICS:
        if (event instanceof ServiceMetricEvent) {
          emitInternal(event);
        }
        break;
      default:
        throw new ISE("unknown eventsToLog value [%s]. Supported values are ALL, ALERTS and METRICS.", eventsToLog);
    }
  }

  private void emitInternal(Event event)
  {
    try {
      final String message = "Event [%s]";
      switch (level) {
        case TRACE:
          if (log.isTraceEnabled()) {
            log.trace(message, jsonMapper.writeValueAsString(event));
          }
          break;
        case DEBUG:
          if (log.isDebugEnabled()) {
            log.debug(message, jsonMapper.writeValueAsString(event));
          }
          break;
        case INFO:
          if (log.isInfoEnabled()) {
            log.info(message, jsonMapper.writeValueAsString(event));
          }
          break;
        case WARN:
          log.warn(message, jsonMapper.writeValueAsString(event));
          break;
        case ERROR:
          log.error(message, jsonMapper.writeValueAsString(event));
          break;
      }
    } catch (Exception e) {
      log.warn(e, "Failed to generate json");
    }
  }

  @Override
  public void flush() throws IOException
  {

  }

  @Override
  @LifecycleStop
  public void close() throws IOException
  {
    final boolean wasStarted = started.getAndSet(false);
    if (wasStarted) {
      final String message = "Close: started [%s]";
      switch (level) {
        case TRACE:
          if (log.isTraceEnabled()) {
            log.trace(message, started.get());
          }
          break;
        case DEBUG:
          if (log.isDebugEnabled()) {
            log.debug(message, started.get());
          }
          break;
        case INFO:
          if (log.isInfoEnabled()) {
            log.info(message, started.get());
          }
          break;
        case WARN:
          log.warn(message, started.get());
          break;
        case ERROR:
          log.error(message, started.get());
          break;
      }
    }
  }

  public enum Level
  {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR;

    public static Level toLevel(String name)
    {
      return Level.valueOf(name.toUpperCase());
    }
  }

  public enum EventsToLog
  {
    METRICS,
    ALERTS,
    ALL;

    public static EventsToLog toEventsToLog(String str)
    {
      return EventsToLog.valueOf(str.toUpperCase());
    }
  }
}
