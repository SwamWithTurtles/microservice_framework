package uk.gov.justice.services.components.command.api.interceptors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;

import uk.gov.justice.services.core.accesscontrol.LocalAccessControlInterceptor;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;

import java.util.List;

import org.junit.Test;

public class CommandApiInterceptorChainProviderTest {

    @Test
    public void shouldReturnComponent() throws Exception {
        assertThat(new CommandApiInterceptorChainProvider().component(), is(COMMAND_API));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProvideDefaultInterceptorChainTypes() throws Exception {
        final List<PriorityInterceptorType> interceptorChainTypes = new CommandApiInterceptorChainProvider().interceptorChainTypes();

        assertThat(interceptorChainTypes, containsInAnyOrder(
                new PriorityInterceptorType(1, TotalActionMetricsInterceptor.class),
                new PriorityInterceptorType(2, IndividualActionMetricsInterceptor.class),
                new PriorityInterceptorType(3000, LocalAuditInterceptor.class),
                new PriorityInterceptorType(4000, LocalAccessControlInterceptor.class)));
    }
}