package com.ecommerce.site.shop.tracing;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleLoggingHandler implements ObservationHandler<Observation.Context> {
    @Override
    public void onStart(Observation.Context context) {
        log.info("Starting context " + toString(context));
    }

    @Override
    public void onError(Observation.Context context) {
        log.info("Error for context " + toString(context));
    }

    @Override
    public void onEvent(Observation.Event event, Observation.Context context) {
        log.info("Event for context " + toString(context) + " [" + toString(event) + "]");
    }

    @Override
    public void onScopeOpened(Observation.Context context) {
        log.info("Scope opened for context " + toString(context));
    }

    @Override
    public void onScopeClosed(Observation.Context context) {
        log.info("Scope closed for context " + toString(context));
    }

    @Override
    public void onStop(Observation.Context context) {
        log.info("Stopping context " + toString(context));
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    private static String toString(Observation.Context context) {
        return null == context ? "(no context)" : context.getName()
                + " (" + context.getClass().getName() + "@" + System.identityHashCode(context) + ")";
    }

    private static String toString(Observation.Event event) {
        return null == event ? "(no event)" : event.getName();
    }
}
