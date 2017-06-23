package uk.gov.justice.services.core.dispatcher;

import static uk.gov.justice.services.core.annotation.ComponentNameUtil.componentFrom;
import static uk.gov.justice.services.core.annotation.ServiceComponentLocation.componentLocationFrom;

import uk.gov.justice.services.core.annotation.Component;
import uk.gov.justice.services.core.annotation.ServiceComponentLocation;
import uk.gov.justice.services.core.extension.ServiceComponentFoundEvent;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Creates and caches {@link Dispatcher} for {@link InjectionPoint} or {@link
 * ServiceComponentFoundEvent}.
 */
@ApplicationScoped
public class DispatcherCache {

    private final Map<ComponentTypeLocation, Dispatcher> dispatcherMap = new ConcurrentHashMap<>();

    private final DispatcherFactory dispatcherFactory = new DispatcherFactory();

    /**
     * Return a {@link Dispatcher} for the given {@link InjectionPoint}.
     *
     * @param injectionPoint the given {@link InjectionPoint}
     * @return the {@link Dispatcher}
     */
    public Dispatcher dispatcherFor(final InjectionPoint injectionPoint) {
        return createDispatcherIfAbsent(new ComponentTypeLocation(
                componentFrom(injectionPoint), componentLocationFrom(injectionPoint)));
    }

    /**
     * Return the {@link Dispatcher} for the given {@link ServiceComponentFoundEvent}.
     *
     * @param event the given {@link ServiceComponentFoundEvent}
     * @return the {@link Dispatcher}
     */
    public Dispatcher dispatcherFor(final ServiceComponentFoundEvent event) {
        return createDispatcherIfAbsent(new ComponentTypeLocation(
                event.getComponentName(), event.getLocation()));
    }

    /**
     * Return the {@link Dispatcher} for the given {@link Component} and {@link
     * ServiceComponentLocation}.
     *
     * @param component the component type for which the dispatcher is for
     * @param location  whether the dispatcher is local or remote
     * @return the {@link Dispatcher}
     */
    public Dispatcher dispatcherFor(final String component, final ServiceComponentLocation location) {
        return createDispatcherIfAbsent(new ComponentTypeLocation(component, location));
    }

    private Dispatcher createDispatcherIfAbsent(final ComponentTypeLocation component) {
        return dispatcherMap.computeIfAbsent(component, c -> dispatcherFactory.createNew());
    }

    private class ComponentTypeLocation {

        private final String componentType;
        private final ServiceComponentLocation location;

        private ComponentTypeLocation(final String componentType, final ServiceComponentLocation location) {
            this.componentType = componentType;
            this.location = location;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final ComponentTypeLocation that = (ComponentTypeLocation) o;
            return Objects.equals(componentType, that.componentType) &&
                    location == that.location;
        }

        @Override
        public int hashCode() {
            return Objects.hash(componentType, location);
        }
    }
}
