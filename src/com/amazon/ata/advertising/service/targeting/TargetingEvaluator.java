package com.amazon.ata.advertising.service.targeting;

import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult.FALSE;
import static com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult.TRUE;

/**
 * Evaluates TargetingPredicates for a given RequestContext.
 */
public class TargetingEvaluator {
    public static final boolean IMPLEMENTED_STREAMS = true;
    public static final boolean IMPLEMENTED_CONCURRENCY = true;
    private final RequestContext requestContext;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public TargetingPredicateResult evaluteTgConcurently(TargetingGroup group) {
        List<Future<TargetingPredicateResult>> futures =
                group.getTargetingPredicates().stream().map(predicate -> executor.submit(
                        () -> predicate.evaluate(requestContext))).collect(Collectors.toList());
        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).allMatch(TargetingPredicateResult::isTrue) ? TRUE : FALSE;
    }

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
        return targetingGroup.getTargetingPredicates().stream().parallel()
                       .map(targetingPredicate -> targetingPredicate.evaluate(requestContext))
                       .allMatch(TargetingPredicateResult::isTrue) ? TRUE : FALSE;
    }
}








//     public TargetingPredicateResult evaluate(TargetingGroup targetingGroup) {
//        // TargetingEvaluator's evaluate method determines if all the TargetingPredicates
//        // in a given TargetingGroup are true for the given RequestContext
//        List<TargetingPredicateResult> t = targetingGroup.getTargetingPredicates().stream().parallel()
//                                                   .map(predicate -> {
//            try {
//                Future<TargetingPredicateResult> res = executor.submit(() -> predicate.evaluate(requestContext));
//                return res.get().equals(TRUE) ? TRUE : FALSE;
//            } catch (ExecutionException | InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).collect(Collectors.toList());
//
//        return t.stream().allMatch(TargetingPredicateResult::isTrue) ? TRUE : FALSE;
//    }
