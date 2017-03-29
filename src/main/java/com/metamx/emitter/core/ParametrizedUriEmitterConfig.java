package com.metamx.emitter.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class ParametrizedUriEmitterConfig
{
  @JsonProperty
  private Map<String, Object> httpEmitterProperties;

  public Map<String, Object> getHttpEmitterProperties()
  {
    return httpEmitterProperties;
  }

  public void setHttpEmitterProperties(Map<String, Object> httpEmitterProperties)
  {
    this.httpEmitterProperties = httpEmitterProperties;
  }


  public HttpEmitterConfig buildHttpEmitterConfig(String baseUri, ObjectMapper jsonMapper)
  {
    Map<String, Object> jsonified = new HashMap<>(httpEmitterProperties);
    jsonified.put("recipientBaseUrl", baseUri);
    return jsonMapper.convertValue(jsonified, HttpEmitterConfig.class);
  }
}
