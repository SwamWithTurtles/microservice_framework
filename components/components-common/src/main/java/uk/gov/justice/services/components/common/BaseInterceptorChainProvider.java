package uk.gov.justice.services.components.common;

import uk.gov.justice.services.core.interceptor.InterceptorChainProvider;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseInterceptorChainProvider implements InterceptorChainProvider {

    final List<PriorityInterceptorType> interceptorChainTypes = new LinkedList<>();

    public BaseInterceptorChainProvider() {
        interceptorChainTypes.add(new PriorityInterceptorType(1, TotalActionMetricsInterceptor.class));
        interceptorChainTypes.add(new PriorityInterceptorType(2, IndividualActionMetricsInterceptor.class));
    }

    @Override
    public List<PriorityInterceptorType> interceptorChainTypes() {
        return interceptorChainTypes;
    }
}
