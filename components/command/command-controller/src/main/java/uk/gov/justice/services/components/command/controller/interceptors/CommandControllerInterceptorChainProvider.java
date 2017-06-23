package uk.gov.justice.services.components.command.controller.interceptors;

import static uk.gov.justice.services.core.annotation.Component.COMMAND_CONTROLLER;

import uk.gov.justice.services.components.common.BaseInterceptorChainProvider;
import uk.gov.justice.services.core.accesscontrol.LocalAccessControlInterceptor;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;

public class CommandControllerInterceptorChainProvider extends BaseInterceptorChainProvider {

    public CommandControllerInterceptorChainProvider() {
        interceptorChainTypes().add(new PriorityInterceptorType(3000, LocalAuditInterceptor.class));
        interceptorChainTypes().add(new PriorityInterceptorType(4000, LocalAccessControlInterceptor.class));
    }

    @Override
    public String component() {
        return COMMAND_CONTROLLER;
    }
}
