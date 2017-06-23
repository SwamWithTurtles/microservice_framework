package uk.gov.justice.services.components.command.handler.interceptors;

import static java.lang.Integer.MAX_VALUE;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_HANDLER;

import uk.gov.justice.services.components.common.BaseInterceptorChainProvider;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;

public class CommandHandlerInterceptorChainProvider extends BaseInterceptorChainProvider {

    public CommandHandlerInterceptorChainProvider() {
        interceptorChainTypes().add(new PriorityInterceptorType(MAX_VALUE, RetryInterceptor.class));
        interceptorChainTypes().add(new PriorityInterceptorType(3000, LocalAuditInterceptor.class));
    }

    @Override
    public String component() {
        return COMMAND_HANDLER;
    }
}
