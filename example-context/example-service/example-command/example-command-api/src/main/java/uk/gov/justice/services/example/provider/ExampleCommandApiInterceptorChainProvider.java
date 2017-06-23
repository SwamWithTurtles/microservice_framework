package uk.gov.justice.services.example.provider;

import uk.gov.justice.services.adapter.rest.interceptor.InputStreamFileInterceptor;
import uk.gov.justice.services.components.command.api.interceptors.CommandApiInterceptorChainProvider;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;

import java.util.List;

public class ExampleCommandApiInterceptorChainProvider extends CommandApiInterceptorChainProvider {

    @Override
    public List<PriorityInterceptorType> interceptorChainTypes() {
        final List<PriorityInterceptorType> interceptorChainTypes = super.interceptorChainTypes();
        interceptorChainTypes.add(new PriorityInterceptorType(7000, InputStreamFileInterceptor.class));
        return interceptorChainTypes;
    }
}