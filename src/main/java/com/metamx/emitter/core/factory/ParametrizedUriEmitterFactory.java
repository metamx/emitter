package com.metamx.emitter.core.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.FeedUriExtractor;
import com.metamx.emitter.core.ParametrizedUriEmitter;
import com.metamx.emitter.core.ParametrizedUriEmitterConfig;
import com.metamx.emitter.core.ParametrizedUriExtractor;
import com.metamx.emitter.core.UriExtractor;
import com.metamx.http.client.HttpClient;
import java.util.HashSet;
import java.util.Set;

public class ParametrizedUriEmitterFactory extends ParametrizedUriEmitterConfig implements EmitterFactory
{
  public ParametrizedUriEmitterFactory() {}

  @Override
  public Emitter makeEmitter(ObjectMapper objectMapper, HttpClient httpClient, Lifecycle lifecycle)
  {
    String baseUri = this.getBasicHttpConficBuilder().build().getRecipientBaseUrl();
    ParametrizedUriExtractor parametrizedUriExtractor = new ParametrizedUriExtractor(baseUri);
    UriExtractor uriExtractor = parametrizedUriExtractor;
    Set<String> onlyFeedParam = new HashSet<>();
    onlyFeedParam.add("feed");
    if (parametrizedUriExtractor.getParams().equals(onlyFeedParam)) {
      uriExtractor = new FeedUriExtractor(baseUri.replace("{feed}", "%s"));
    }
    Emitter retVal = new ParametrizedUriEmitter(
        this,
        httpClient,
        objectMapper,
        uriExtractor
    );
    lifecycle.addManagedInstance(retVal);
    return retVal;
  }
}
