package com.metamx.emitter.core;

/**
 */

import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
public class LoggingEmitter implements Emitter
{
  private final Logger log;
  private final Level level;
  private static final ObjectMapper jsonMapper = new ObjectMapper();

  private final AtomicBoolean started = new AtomicBoolean(false);

  public LoggingEmitter(LoggingEmitterConfig config)
  {
    this(new Logger(config.getLoggerClass()), Level.toLevel(config.getLogLevel()));
  }

  public LoggingEmitter(Logger log, Level level)
  {
    this.log = log;
    this.level = level;
  }

  @Override
  @LifecycleStart
  public void start()
  {
    started.set(true);
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

  @Override
  public void emit(Event event)
  {
    synchronized (started) {
      if (!started.get()) {
        throw new RejectedExecutionException("Service not started.");
      }
    }
    try {
      final String message = "Event [%s]";
      switch (level) {
        case TRACE:
          if (log.isTraceEnabled()) {
            log.trace(message, jsonMapper.writeValueAsString(event.toMap()));
          }
          break;
        case DEBUG:
          if (log.isDebugEnabled()) {
            log.debug(message, jsonMapper.writeValueAsString(event.toMap()));
          }
          break;
        case INFO:
          if (log.isInfoEnabled()) {
            log.info(message, jsonMapper.writeValueAsString(event.toMap()));
          }
          break;
        case WARN:
          log.warn(message, jsonMapper.writeValueAsString(event.toMap()));
          break;
        case ERROR:
          log.error(message, jsonMapper.writeValueAsString(event.toMap()));
          break;
      }
    } catch (Exception e) {
      log.warn("Failed to generate json", e);
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
    synchronized (started) {
      started.set(false);
    }
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

  private enum Level {
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
}
