package uk.gov.justice.services.components.command.handler.interceptors;

import static java.lang.Integer.MAX_VALUE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_HANDLER;

import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;

import java.util.List;

import org.junit.Test;

public class CommandHandlerInterceptorChainProviderTest {

    @Test
    public void shouldReturnComponent() throws Exception {
        assertThat(new CommandHandlerInterceptorChainProvider().component(), is(COMMAND_HANDLER));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProvideDefaultInterceptorChainTypes() throws Exception {
        final List<PriorityInterceptorType> interceptorChainTypes = new CommandHandlerInterceptorChainProvider().interceptorChainTypes();

        assertThat(interceptorChainTypes, containsInAnyOrder(
                new PriorityInterceptorType(1, TotalActionMetricsInterceptor.class),
                new PriorityInterceptorType(2, IndividualActionMetricsInterceptor.class),
                new PriorityInterceptorType(3000, LocalAuditInterceptor.class),
                new PriorityInterceptorType(MAX_VALUE, RetryInterceptor.class)));
    }
}