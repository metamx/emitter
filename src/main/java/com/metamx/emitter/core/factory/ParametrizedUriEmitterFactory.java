package com.metamx.emitter.core.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.FeedUriExtractor;
import com.metamx.emitter.core.ParametrizedUriExtractor;
import com.metamx.emitter.core.ParametrizedUriEmitterConfig;
import com.metamx.emitter.core.ParametrizedUriEmitter;
import com.metamx.emitter.core.UriExtractor;
import com.metamx.http.client.HttpClient;
import java.util.HashSet;
import java.util.Set;

public class ParametrizedUriEmitterFactory implements EmitterFactory
{
  @JsonProperty("parametrized")
  private ParametrizedUriEmitterConfig parametrizedUriEmitterConfig = null;

  public ParametrizedUriEmitterFactory() {}

  public ParametrizedUriEmitterFactory(ParametrizedUriEmitterConfig parametrizedUriEmitterConfig)
  {
    this.parametrizedUriEmitterConfig = parametrizedUriEmitterConfig;
  }

  @Override
  public Emitter build(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    String baseUri = parametrizedUriEmitterConfig.getHttpEmitterProperties().get("recipientBaseUrl").toString();
    ParametrizedUriExtractor parametrizedUriExtractor = new ParametrizedUriExtractor(baseUri);
    UriExtractor uriExtractor = parametrizedUriExtractor;
    Set<String> onlyFeedParam = new HashSet<>();
    onlyFeedParam.add("feed");
    if (parametrizedUriExtractor.getParams().equals(onlyFeedParam)) {
      uriExtractor = new FeedUriExtractor(baseUri.replace("{feed}", "%s"));
    }
    Emitter retVal = new ParametrizedUriEmitter(
        parametrizedUriEmitterConfig,
        httpClient,
        objectMapper,
        uriExtractor
    );
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
