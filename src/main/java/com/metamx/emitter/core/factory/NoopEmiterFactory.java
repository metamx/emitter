package com.metamx.emitter.core.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.NoopEmitter;
import com.metamx.http.client.HttpClient;

public class NoopEmiterFactory implements EmitterFactory
{
  @Override
  public Emitter build(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    return build(lifecycle);
  }

  public Emitter build(Lifecycle lifecycle)
  {
    Emitter retVal = new NoopEmitter();
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
