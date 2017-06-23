package uk.gov.justice.services.core.it;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.interceptor.DefaultInterceptorContext.interceptorContextWithInput;
import static uk.gov.justice.services.messaging.DefaultJsonEnvelope.envelope;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.metadataOf;

import uk.gov.justice.services.common.configuration.GlobalValueProducer;
import uk.gov.justice.services.common.converter.ObjectToJsonValueConverter;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.core.accesscontrol.AccessControlFailureMessageGenerator;
import uk.gov.justice.services.core.accesscontrol.AccessControlService;
import uk.gov.justice.services.core.accesscontrol.AllowAllPolicyEvaluator;
import uk.gov.justice.services.core.accesscontrol.PolicyEvaluator;
import uk.gov.justice.services.core.annotation.CustomAdapter;
import uk.gov.justice.services.core.annotation.CustomServiceComponent;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.cdi.LoggerProducer;
import uk.gov.justice.services.core.dispatcher.DispatcherCache;
import uk.gov.justice.services.core.dispatcher.EmptySystemUserProvider;
import uk.gov.justice.services.core.dispatcher.ServiceComponentObserver;
import uk.gov.justice.services.core.dispatcher.SystemUserUtil;
import uk.gov.justice.services.core.envelope.EnvelopeValidationExceptionHandlerProducer;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.extension.BeanInstantiater;
import uk.gov.justice.services.core.extension.ServiceComponentScanner;
import uk.gov.justice.services.core.interceptor.Interceptor;
import uk.gov.justice.services.core.interceptor.InterceptorCache;
import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.InterceptorChainObserver;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessorProducer;
import uk.gov.justice.services.core.interceptor.InterceptorChainProvider;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.core.interceptor.PriorityInterceptorType;
import uk.gov.justice.services.core.json.DefaultJsonSchemaValidator;
import uk.gov.justice.services.core.json.JsonSchemaLoader;
import uk.gov.justice.services.core.requester.RequesterProducer;
import uk.gov.justice.services.core.sender.SenderProducer;
import uk.gov.justice.services.messaging.DefaultJsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.jms.DefaultEnvelopeConverter;
import uk.gov.justice.services.messaging.jms.DefaultJmsEnvelopeSender;
import uk.gov.justice.services.messaging.logging.DefaultTraceLogger;
import uk.gov.justice.services.test.utils.common.envelope.TestEnvelopeRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Module;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ApplicationComposer.class)
@CustomAdapter("CUSTOM_COMPONENT_TEST")
public class CustomServiceComponentHandlerIT {

    private static final String CUSTOM_XYZ = "test.event-xyz";

    @Inject
    private InterceptorChainProcessor interceptorChainProcessor;

    @Inject
    private CustomComponentHandler customComponentHandler;

    @Module
    @Classes(cdi = true, value = {
            CustomComponentHandler.class,
            ServiceComponentScanner.class,
            RequesterProducer.class,
            ServiceComponentObserver.class,

            InterceptorChainProcessorProducer.class,
            InterceptorChainProcessor.class,
            InterceptorCache.class,
            InterceptorChainObserver.class,
            TestInterceptor.class,
            CustomInterceptorChainProvider.class,

            SenderProducer.class,
            DefaultJmsEnvelopeSender.class,
            DefaultEnvelopeConverter.class,

            StringToJsonObjectConverter.class,
            DefaultJsonObjectEnvelopeConverter.class,
            ObjectToJsonValueConverter.class,
            ObjectMapper.class,
            Enveloper.class,

            EnvelopeValidationExceptionHandlerProducer.class,
            GlobalValueProducer.class,
            DefaultJsonSchemaValidator.class,
            JsonSchemaLoader.class,

            AccessControlFailureMessageGenerator.class,
            AllowAllPolicyEvaluator.class,
            AccessControlService.class,
            DispatcherCache.class,
            PolicyEvaluator.class,
            LoggerProducer.class,
            EmptySystemUserProvider.class,
            SystemUserUtil.class,
            BeanInstantiater.class,
            UtcClock.class,
            DefaultTraceLogger.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("custom-component-test")
                .addServlet("TestApp", Application.class.getName());
    }

    @Test
    public void shouldHandleFrameWorkComponentByName() {
        final UUID metadataId = randomUUID();
        final JsonEnvelope jsonEnvelope = envelope()
                .with(metadataOf(metadataId, CUSTOM_XYZ)
                        .withStreamId(randomUUID())
                        .withVersion(1L))
                .build();

        interceptorChainProcessor.process(interceptorContextWithInput(jsonEnvelope));

        assertThat(customComponentHandler.firstRecordedEnvelope(), not(nullValue()));
        assertThat(customComponentHandler.firstRecordedEnvelope().metadata().id(), equalTo(metadataId));
    }

    @CustomServiceComponent("CUSTOM_COMPONENT_TEST")
    @ApplicationScoped
    public static class CustomComponentHandler extends TestEnvelopeRecorder {

        @Handles(CUSTOM_XYZ)
        public void handle(JsonEnvelope envelope) {
            record(envelope);
        }

    }

    @ApplicationScoped
    public static class TestInterceptor implements Interceptor {

        //State is set only for testing purposes.  Interceptor should not hold state in normal operation.
        private static UUID id;

        @Override
        public InterceptorContext process(final InterceptorContext interceptorContext, final InterceptorChain interceptorChain) {
            id = interceptorContext.inputEnvelope().metadata().id();
            return interceptorChain.processNext(interceptorContext);
        }
    }

    public static class CustomInterceptorChainProvider implements InterceptorChainProvider {

        @Override
        public String component() {
            return "CUSTOM_COMPONENT_TEST";
        }

        @Override
        public List<PriorityInterceptorType> interceptorChainTypes() {
            final List<PriorityInterceptorType> interceptorTypes = new ArrayList<>();
            interceptorTypes.add(new PriorityInterceptorType(1, TestInterceptor.class));
            return interceptorTypes;
        }
    }
}
