package uk.gov.justice.services.components.query.api.interceptors;

import static uk.gov.justice.services.core.annotation.Component.EVENT_API;

import uk.gov.justice.services.components.common.BaseInterceptorChainProvider;
import uk.gov.justice.services.core.accesscontrol.LocalAccessControlInterceptor;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;

public class EventApiInterceptorChainProvider extends BaseInterceptorChainProvider {

    public EventApiInterceptorChainProvider() {
        interceptorChainTypes().add(new PriorityInterceptorType(3000, LocalAuditInterceptor.class));
        interceptorChainTypes().add(new PriorityInterceptorType(4000, LocalAccessControlInterceptor.class));
    }

    @Override
    public String component() {
        return EVENT_API;
    }
}
