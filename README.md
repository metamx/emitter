event-emitter
=============

## Emitter creation
The easiest way to create an emmiter instance is via `com.metamx.emitter.core.Emitters.create` method.
This method will create one of the predefined emitters or your own implementation based on the properties provided.

## Emitter types

### Logging emitter
When properties contains `com.metamx.emitter.logging` property set then an instance of `LoggingEmitter` will be created.
For more details on `LoggingEmitter` configuration please refer `com.metamx.emitter.core.Emitters.makeLoggingMap()` 

### Http emitter
When properties contains `com.metamx.emitter.http` property set then an instance of `HttpPostEmitter` will be created.
The only required parameter is `com.metamx.emitter.http.url` that is a url where all the events will be sent too.
For more details on `HttpPostEmitter` configuration please refer `com.metamx.emitter.core.Emitters.makeHttpMap()`
 
### Parametrized URI http emitter
`ParametrizedUriEmitter` is a predefined custom emitter that can be used when events should be posted to different url based on event data.
You should set `com.metamx.emitter.type = parametrized` in properties to create one. 
The URI patter is defined via `com.metamx.emitter.http.recipientBaseUrl` property. 
For instance: `com.metamx.emitter.http.recipientBaseUrl=http://example.com/{feed}` will make it send events to different endpoints according to `event.getFeed` value.
`com.metamx.emitter.http.recipientBaseUrl=http://example.com/{key1}/{key2}` requires that `key1` and `key2` are defined in event map.

### Custom emitter
To create your own emitter you need to implement EmitterFactory and add it as registered type to `ObjectMapper` that is used to call `Emitters.create`
You can refer to `com.metamx.emitter.core.CustomEmitterFactoryTest` for an example of custom emitter creation.
All properties with `com.metamx.emitter.*` prefix will be translated into configuration json used to create `Emitter`.
Consider the following example:
```properties
com.metamx.emitter.key1 = val1
com.metamx.emitter.outer.inner1 = inner_val1
com.metamx.emitter.outer.inner2 = inner_val2
```
will be translated into:
```json
{
  "key1":"val1", 
  "outer":
    {
      "inner1":"inner_val1",
      "inner2":"inner_val2"
    }
}
```