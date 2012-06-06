package com.metamx.emitter.service;

import com.metamx.emitter.core.Event;

public interface ServiceEventBuilder<X extends Event>
{
  public X build(String service, String host);
}
