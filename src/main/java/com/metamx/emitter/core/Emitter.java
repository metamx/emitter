package com.metamx.emitter.core;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 */
public interface Emitter extends Closeable, Flushable
{
  void start();
  void emit(Event event);
  void flush() throws IOException;
  void close() throws IOException;
}
