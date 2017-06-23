package uk.gov.justice.services.core.interceptor;

import java.util.List;


public interface InterceptorChainProvider {

    /**
     * Provide the Component to where the InterceptorChainTypes will be used.
     *
     * @return the Component
     */
    String component();

    /**
     * Provide a List containing {@link PriorityInterceptorType} for the InterceptorCache to
     * create an InterceptorChain.  Priority order is low is highest. e.g. 1 = is highest priority
     *
     * @return Deque containing Interceptor Classes
     */
    List<PriorityInterceptorType> interceptorChainTypes();
}