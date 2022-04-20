package com.amazon.ata.advertising.service.targeting;

import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;

import static com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult.FALSE;
import static com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult.TRUE;

/**
 * Evaluates TargetingPredicates for a given RequestContext.
 */
public class TargetingEvaluator {
    public static final boolean IMPLEMENTED_STREAMS = true;
    public static final boolean IMPLEMENTED_CONCURRENCY = true;
    private final RequestContext requestContext;

    /**
     * Creates an evaluator for targeting predicates.
     *
     * @param requestContext Context that can be used to evaluate the predicates.
     */
    public TargetingEvaluator(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    /**
     * Evaluate a TargetingGroup to determine if all of its TargetingPredicates are TRUE or not for the given
     * RequestContext.
     *
     * @param targetingGroup Targeting group for an advertisement, including TargetingPredicates.
     *
     * @return TRUE if all of the TargetingPredicates evaluate to TRUE against the RequestContext, FALSE otherwise.
     */
    public TargetingPredicateResult evaluate(TargetingGroup targetingGroup) {
        // TargetingEvaluator's evaluate method determines if all the TargetingPredicates
        // in a given TargetingGroup are true for the given RequestContext
        return targetingGroup.getTargetingPredicates().stream()
                       .map(targetingPredicate -> targetingPredicate.evaluate(requestContext))
                       .allMatch(TargetingPredicateResult::isTrue) ? TRUE : FALSE;
    }
}
