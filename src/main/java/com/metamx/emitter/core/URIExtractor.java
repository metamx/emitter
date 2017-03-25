package com.metamx.emitter.core;

import java.net.URI;
import java.net.URISyntaxException;

public interface URIExtractor
{
  URI apply(Event event) throws URISyntaxException;
}
