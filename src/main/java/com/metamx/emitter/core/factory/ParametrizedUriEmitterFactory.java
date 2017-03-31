package com.metamx.emitter.core.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.FeedUriExtractor;
import com.metamx.emitter.core.ParametrizedUriEmitter;
import com.metamx.emitter.core.ParametrizedUriExtractor;
import com.metamx.emitter.core.UriExtractor;
import com.metamx.http.client.HttpClient;
import java.util.HashSet;
import java.util.Set;

public class ParametrizedUriEmitterFactory extends HttpEmitterFactory
{
  @JsonProperty
  private String recipientBaseUrl = null;

  public ParametrizedUriEmitterFactory() {}


  @Override
  public Emitter build(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    final String baseUri = recipientBaseUrl;
    final ParametrizedUriExtractor parametrizedUriExtractor = new ParametrizedUriExtractor(baseUri);
    final Set<String> onlyFeedParam = new HashSet<>();
    UriExtractor uriExtractor = parametrizedUriExtractor;
    onlyFeedParam.add("feed");
    if (parametrizedUriExtractor.getParams().equals(onlyFeedParam)) {
      uriExtractor = new FeedUriExtractor(baseUri.replace("{feed}", "%s"));
    }
    final Emitter retVal = new ParametrizedUriEmitter(
        this,
        httpClient,
        objectMapper,
        uriExtractor
    );
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
