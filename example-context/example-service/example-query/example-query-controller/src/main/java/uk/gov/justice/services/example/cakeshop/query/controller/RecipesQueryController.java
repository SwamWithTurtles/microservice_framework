package uk.gov.justice.services.example.cakeshop.query.controller;

import static uk.gov.justice.services.core.annotation.Component.QUERY_CONTROLLER;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.dispatcher.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.inject.Inject;

@ServiceComponent(QUERY_CONTROLLER)
public class RecipesQueryController {

    @Inject
    Requester requester;

    @Handles("cakeshop.search-recipes")
    public JsonEnvelope listRecipes(final JsonEnvelope query) {
        return requester.request(query);
    }

    @Handles("cakeshop.get-recipe")
    public JsonEnvelope recipe(final JsonEnvelope query) {
        return requester.request(query);
    }
}