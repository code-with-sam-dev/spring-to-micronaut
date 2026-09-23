package dev.codewithsam.payments.probe;

import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import jakarta.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
@InterceptorBean(Counted.class)
public class CountedInterceptor implements MethodInterceptor<Object, Object> {
    public final AtomicInteger calls = new AtomicInteger();
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        calls.incrementAndGet();
        return context.proceed();
    }
}
