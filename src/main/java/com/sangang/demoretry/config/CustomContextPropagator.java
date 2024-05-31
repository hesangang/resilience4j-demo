package com.sangang.demoretry.config;

import io.github.resilience4j.core.ContextPropagator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Description:
 * @author: SanGang
 * @author: 2024-05-29 18:04
 */
@Slf4j
public class CustomContextPropagator<T> implements ContextPropagator<T> {

    @Override
    public Supplier<Optional<T>> retrieve() {
        log.info("RETREIVE -> {}", RequestContextHolder.getRequestAttributes());
        return () -> (Optional<T>) Optional.ofNullable(RequestContextHolder.getRequestAttributes());
    }

    @Override
    public Consumer<Optional<T>> copy() {
        return t -> t.ifPresent(e -> {
            clear();
            if (e instanceof ServletRequestAttributes) {
                log.info("COPY -> {}", e);
                //requestAttributes.registerDestructionCallback("DESTROY", () -> log.info("DESTROY"), 0);
                RequestContextHolder.setRequestAttributes((RequestAttributes) e);
            }
        });
    }

    @Override
    public Consumer<Optional<T>> clear() {
        return t -> RequestContextHolder.resetRequestAttributes();
    }
}
