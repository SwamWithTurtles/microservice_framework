package uk.gov.justice.services.core.it;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;
import static uk.gov.justice.services.core.interceptor.DefaultInterceptorContext.interceptorContextWithInput;
import static uk.gov.justice.services.messaging.DefaultJsonEnvelope.envelope;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.metadataOf;

import uk.gov.justice.services.common.configuration.GlobalValueProducer;
import uk.gov.justice.services.common.converter.ObjectToJsonValueConverter;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.core.accesscontrol.AccessControlFailureMessageGenerator;
import uk.gov.justice.services.core.accesscontrol.AllowAllPolicyEvaluator;
import uk.gov.justice.services.core.accesscontrol.DefaultAccessControlService;
import uk.gov.justice.services.core.accesscontrol.PolicyEvaluator;
import uk.gov.justice.services.core.annotation.Adapter;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.cdi.LoggerProducer;
import uk.gov.justice.services.core.dispatcher.DispatcherCache;
import uk.gov.justice.services.core.dispatcher.DispatcherFactory;
import uk.gov.justice.services.core.dispatcher.EmptySystemUserProvider;
import uk.gov.justice.services.core.dispatcher.RequesterProducer;
import uk.gov.justice.services.core.dispatcher.ServiceComponentObserver;
import uk.gov.justice.services.core.dispatcher.SystemUserUtil;
import uk.gov.justice.services.core.envelope.EnvelopeValidationExceptionHandlerProducer;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.eventfilter.AllowAllEventFilter;
import uk.gov.justice.services.core.extension.AnnotationScanner;
import uk.gov.justice.services.core.extension.BeanInstantiater;
import uk.gov.justice.services.core.interceptor.Interceptor;
import uk.gov.justice.services.core.interceptor.InterceptorCache;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessorProducer;
import uk.gov.justice.services.core.interceptor.InterceptorChainProvider;
import uk.gov.justice.services.core.jms.DefaultJmsDestinations;
import uk.gov.justice.services.core.jms.JmsSenderFactory;
import uk.gov.justice.services.core.json.DefaultJsonSchemaValidator;
import uk.gov.justice.services.core.json.JsonSchemaLoader;
import uk.gov.justice.services.core.sender.ComponentDestination;
import uk.gov.justice.services.core.sender.SenderProducer;
import uk.gov.justice.services.messaging.DefaultJsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.jms.DefaultJmsEnvelopeSender;
import uk.gov.justice.services.messaging.jms.EnvelopeConverter;
import uk.gov.justice.services.test.utils.common.envelope.TestEnvelopeRecorder;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Module;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ApplicationComposer.class)
@Adapter(EVENT_LISTENER)
public class AllEventsHandlerIT {

    private static final String EVENT_ABC = "test.event-abc";

    @Inject
    private InterceptorChainProcessor interceptorChainProcessor;

    @Inject
    private AbcEventHandler abcEventHandler;

    @Inject
    private AllEventsHandler allEventsHandler;

    @Module
    @Classes(cdi = true, value = {
            AbcEventHandler.class,
            AllEventsHandler.class,
            AnnotationScanner.class,
            RequesterProducer.class,
            ServiceComponentObserver.class,
            AllowAllEventFilter.class,

            InterceptorChainProcessorProducer.class,
            InterceptorChainProcessor.class,
            InterceptorCache.class,
            EventListenerInterceptorChainProvider.class,

            SenderProducer.class,
            JmsSenderFactory.class,
            ComponentDestination.class,
            DefaultJmsEnvelopeSender.class,
            DefaultJmsDestinations.class,
            EnvelopeConverter.class,

            StringToJsonObjectConverter.class,
            DefaultJsonObjectEnvelopeConverter.class,
            ObjectToJsonValueConverter.class,
            ObjectMapper.class,
            Enveloper.class,

            AccessControlFailureMessageGenerator.class,
            AllowAllPolicyEvaluator.class,
            DefaultAccessControlService.class,
            DispatcherCache.class,
            DispatcherFactory.class,
            PolicyEvaluator.class,

            EnvelopeValidationExceptionHandlerProducer.class,
            GlobalValueProducer.class,
            DefaultJsonSchemaValidator.class,
            JsonSchemaLoader.class,

            LoggerProducer.class,
            EmptySystemUserProvider.class,
            SystemUserUtil.class,
            BeanInstantiater.class,
            UtcClock.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("core-test")
                .addServlet("TestApp", Application.class.getName());
    }

    @Test
    public void shouldHandleEventByName() {

        final UUID metadataId = randomUUID();
        final JsonEnvelope jsonEnvelope = envelope()
                .with(metadataOf(metadataId, EVENT_ABC)
                        .withStreamId(randomUUID())
                        .withVersion(1L))
                .build();

        interceptorChainProcessor.process(interceptorContextWithInput(jsonEnvelope));

        assertThat(abcEventHandler.firstRecordedEnvelope(), not(nullValue()));
        assertThat(abcEventHandler.firstRecordedEnvelope().metadata().id(), equalTo(metadataId));
    }

    @Test
    public void shouldHandleEventByTheAllEventsHandlerIfNamedHandlerNotFound() {

        final UUID metadataId = randomUUID();
        final JsonEnvelope jsonEnvelope = envelope()
                .with(metadataOf(metadataId, "some.unregistered.event")
                        .withStreamId(randomUUID())
                        .withVersion(1L))
                .build();

        interceptorChainProcessor.process(interceptorContextWithInput(jsonEnvelope));

        assertThat(allEventsHandler.firstRecordedEnvelope(), not(nullValue()));
        assertThat(allEventsHandler.firstRecordedEnvelope().metadata().id(), equalTo(metadataId));
    }

    @ServiceComponent(EVENT_LISTENER)
    @ApplicationScoped
    public static class AbcEventHandler extends TestEnvelopeRecorder {

        @Handles(EVENT_ABC)
        public void handle(JsonEnvelope envelope) {
            record(envelope);
        }

    }

    @ServiceComponent(EVENT_LISTENER)
    @ApplicationScoped
    public static class AllEventsHandler extends TestEnvelopeRecorder {

        @Handles("*")
        public void handle(JsonEnvelope envelope) {
            record(envelope);
        }

    }

    public static class EventListenerInterceptorChainProvider implements InterceptorChainProvider {

        @Override
        public String component() {
            return EVENT_LISTENER;
        }

        @Override
        public List<Pair<Integer, Class<? extends Interceptor>>> interceptorChainTypes() {
            return emptyList();
        }
    }
}
