package com.metamx.emitter.service;

import com.metamx.emitter.core.Event;

public interface ServiceEvent extends Event
{
  public String getService();
  public String getHost();
}
