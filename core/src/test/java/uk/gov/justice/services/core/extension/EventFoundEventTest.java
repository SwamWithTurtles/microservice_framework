package uk.gov.justice.services.core.extension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CORBA.Object;

/**
 * Unit tests for the {@link EventFoundEvent} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventFoundEventTest {

    private final static String EVENT_NAME = "test";

    private final static Class<?> CLASS = Object.class;

    private EventFoundEvent event;

    @Before
    public void setup() {
        event = new EventFoundEvent(CLASS, EVENT_NAME);
    }

    @Test
    public void shouldReturnEventName() {
        assertThat(event.getEventName(), equalTo(EVENT_NAME));
    }

    @Test
    public void shouldReturnClass() {
        assertThat(event.getClazz(), equalTo(CLASS));
    }
}
