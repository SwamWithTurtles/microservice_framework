package uk.gov.justice.services.core.interceptor;

import java.util.Objects;

/**
 * Wrapper for encapsulating the integer Priority with an Interceptor class type.  Used by the
 * {@link InterceptorChainProvider}
 */
public class PriorityInterceptorType {

    private final int priority;
    private final Class<? extends Interceptor> interceptorType;

    public PriorityInterceptorType(final int priority, final Class<? extends Interceptor> interceptorType) {
        this.priority = priority;
        this.interceptorType = interceptorType;
    }

    public int getPriority() {
        return priority;
    }

    public Class<? extends Interceptor> getInterceptorType() {
        return interceptorType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PriorityInterceptorType that = (PriorityInterceptorType) o;
        return priority == that.priority &&
                Objects.equals(interceptorType, that.interceptorType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, interceptorType);
    }
}
