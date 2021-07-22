## Exception Metrics are causing IllegalArgumentExceptions

Seems like the `MeteredInvocationHandleFactory` in `feign-micrometer` 11.5 tries to create both a timer and counter with the same name when using a custom error
decoder that does not return a subclass of `FeignException`. This results in the following exception:

```
Caused by: java.lang.IllegalArgumentException: There is already a registered meter of a different type with the same name
	at io.micrometer.core.instrument.MeterRegistry.registerMeterIfNecessary(MeterRegistry.java:571)
	at io.micrometer.core.instrument.MeterRegistry.registerMeterIfNecessary(MeterRegistry.java:561)
	at io.micrometer.core.instrument.MeterRegistry.counter(MeterRegistry.java:284)
	at io.micrometer.core.instrument.Counter$Builder.register(Counter.java:128)
	at io.micrometer.core.instrument.MeterRegistry.counter(MeterRegistry.java:391)
	at feign.micrometer.MeteredInvocationHandleFactory.createExceptionCounter(MeteredInvocationHandleFactory.java:108)
	at feign.micrometer.MeteredInvocationHandleFactory.lambda$create$0(MeteredInvocationHandleFactory.java:85)
	at com.sun.proxy.$Proxy128.getById(Unknown Source)
```
