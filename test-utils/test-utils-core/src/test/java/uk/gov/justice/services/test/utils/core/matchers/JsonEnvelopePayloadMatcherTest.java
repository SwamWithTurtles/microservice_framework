package uk.gov.justice.services.test.utils.core.matchers;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.messaging.DefaultJsonEnvelope.envelope;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.metadataWithRandomUUID;
import static uk.gov.justice.services.test.utils.core.matchers.JsonValueNullMatcher.isJsonValueNull;

import uk.gov.justice.services.messaging.DefaultJsonEnvelope;

import java.util.UUID;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.Test;

public class JsonEnvelopePayloadMatcherTest {

    private static final UUID ID = randomUUID();
    private static final String NAME = "someName";

    @Test
    public void shouldMatchJsonEnvelopePayload() throws Exception {
        assertThat(payload(), JsonEnvelopePayloadMatcher.payloadIsJson(allOf(
                withJsonPath("$.someId", equalTo(ID.toString())),
                withJsonPath("$.name", equalTo(NAME))))
        );
    }

    @Test
    public void shouldMatchJsonEnvelopePayloadWithTwoPartMethodCall() throws Exception {
        assertThat(payload(), JsonEnvelopePayloadMatcher.payload().isJson(allOf(
                withJsonPath("$.someId", equalTo(ID.toString())),
                withJsonPath("$.name", equalTo(NAME))))
        );
    }

    @Test
    public void shouldMatchJsonEnvelopePayloadWithJsonValueNull() throws Exception {
        assertThat(jsonEnvelopeWithJsonValueNullPayload(), JsonEnvelopePayloadMatcher.payload(isJsonValueNull()));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchAJsonEnvelopePayload() throws Exception {
        assertThat(payload(), JsonEnvelopePayloadMatcher.payloadIsJson(allOf(
                withJsonPath("$.someId", equalTo(ID.toString())),
                withJsonPath("$.name", equalTo("will not match"))))
        );
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchJsonEnvelopePayloadWithJsonObject() throws Exception {
        assertThat(payload(), JsonEnvelopePayloadMatcher.payload(isJsonValueNull()));
    }

    private JsonObject payload() {
        return envelope()
                .withPayloadOf(ID.toString(), "someId")
                .withPayloadOf(NAME, "name")
                .build().payloadAsJsonObject();
    }

    private JsonValue jsonEnvelopeWithJsonValueNullPayload() {
        return new DefaultJsonEnvelope(metadataWithRandomUUID("event.action").build(), JsonValue.NULL).payload();
    }
}