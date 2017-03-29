package com.metamx.emitter.core.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.Emitter;
import com.metamx.http.client.HttpClient;

public interface EmitterFactory
{
  Emitter build(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle);
}
