package uk.gov.justice.services.components.query.view.interceptors;

import static uk.gov.justice.services.core.annotation.Component.QUERY_VIEW;

import uk.gov.justice.services.components.common.BaseInterceptorChainProvider;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;

public class QueryViewInterceptorChainProvider extends BaseInterceptorChainProvider {

    public QueryViewInterceptorChainProvider() {
        interceptorChainTypes().add(new PriorityInterceptorType(3000, LocalAuditInterceptor.class));
    }

    @Override
    public String component() {
        return QUERY_VIEW;
    }
}
