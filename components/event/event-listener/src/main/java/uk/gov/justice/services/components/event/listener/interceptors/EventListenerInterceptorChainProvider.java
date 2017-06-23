package uk.gov.justice.services.components.event.listener.interceptors;

import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;

import uk.gov.justice.services.components.common.BaseInterceptorChainProvider;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;


public class EventListenerInterceptorChainProvider extends BaseInterceptorChainProvider {

    public EventListenerInterceptorChainProvider() {
        interceptorChainTypes().add(new PriorityInterceptorType(1000, EventBufferInterceptor.class));
        interceptorChainTypes().add(new PriorityInterceptorType(2000, EventFilterInterceptor.class));
    }

    @Override
    public String component() {
        return EVENT_LISTENER;
    }
}
