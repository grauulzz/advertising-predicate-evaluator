package com.amazon.ata.advertising.service.dependency;

import com.amazon.ata.advertising.service.targeting.predicate.AgeTargetingPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.CategorySpendFrequencyTargetingPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.CategorySpendValueTargetingPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.ParentPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.PrimeBenefitTargetingPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.RecognizedTargetingPredicate;

import dagger.MembersInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.MockitoAnnotations.initMocks;

public class TargetingPredicateInjectorTest {

    @Mock
    private MembersInjector<AgeTargetingPredicate> agePredicateInjector;

    @Mock
    private MembersInjector<CategorySpendFrequencyTargetingPredicate> spendFrequencyPredicateInjector;

    @Mock
    private MembersInjector<CategorySpendValueTargetingPredicate> spendValuePredicateInjector;

    @Mock
    private MembersInjector<PrimeBenefitTargetingPredicate> primePredicateInjector;

    @Mock
    private MembersInjector<ParentPredicate> parentPredicateInjector;

    @Mock
    private MembersInjector<RecognizedTargetingPredicate> recognizedPredicateInjector;

    private TargetingPredicateInjector targetingPredicateInjector;

    @BeforeEach
    public void setup() {
        initMocks(this);
        targetingPredicateInjector = new TargetingPredicateInjector(agePredicateInjector,
                spendFrequencyPredicateInjector,
                spendValuePredicateInjector,
                primePredicateInjector,
                parentPredicateInjector,
                recognizedPredicateInjector);
    }

    @Test
    public void inject_agePredicate_isInject() {
        // GIVEN
        AgeTargetingPredicate predicate = new AgeTargetingPredicate();

        // WHEN
        targetingPredicateInjector.inject(predicate);

        // THEN
        Mockito.verify(agePredicateInjector).injectMembers(predicate);
        Mockito.verifyNoInteractions(spendFrequencyPredicateInjector);
        Mockito.verifyNoInteractions(spendValuePredicateInjector);
        Mockito.verifyNoInteractions(primePredicateInjector);
        Mockito.verifyNoInteractions(parentPredicateInjector);
        Mockito.verifyNoInteractions(recognizedPredicateInjector);
    }

    @Test
    public void inject_spendFrequencyPredicate_isInject() {
        // GIVEN
        CategorySpendFrequencyTargetingPredicate predicate = new CategorySpendFrequencyTargetingPredicate();

        // WHEN
        targetingPredicateInjector.inject(predicate);

        // THEN
        Mockito.verify(spendFrequencyPredicateInjector).injectMembers(predicate);
        Mockito.verifyNoInteractions(agePredicateInjector);
        Mockito.verifyNoInteractions(spendValuePredicateInjector);
        Mockito.verifyNoInteractions(primePredicateInjector);
        Mockito.verifyNoInteractions(parentPredicateInjector);
        Mockito.verifyNoInteractions(recognizedPredicateInjector);
    }

    @Test
    public void inject_spendValuePredicate_isInject() {
        // GIVEN
        CategorySpendValueTargetingPredicate predicate = new CategorySpendValueTargetingPredicate();

        // WHEN
        targetingPredicateInjector.inject(predicate);

        // THEN
        Mockito.verify(spendValuePredicateInjector).injectMembers(predicate);
        Mockito.verifyNoInteractions(spendFrequencyPredicateInjector);
        Mockito.verifyNoInteractions(agePredicateInjector);
        Mockito.verifyNoInteractions(primePredicateInjector);
        Mockito.verifyNoInteractions(parentPredicateInjector);
        Mockito.verifyNoInteractions(recognizedPredicateInjector);
    }

    @Test
    public void inject_primePredicate_isInject() {
        // GIVEN
        PrimeBenefitTargetingPredicate predicate = new PrimeBenefitTargetingPredicate();

        // WHEN
        targetingPredicateInjector.inject(predicate);

        // THEN
        Mockito.verify(primePredicateInjector).injectMembers(predicate);
        Mockito.verifyNoInteractions(spendFrequencyPredicateInjector);
        Mockito.verifyNoInteractions(spendValuePredicateInjector);
        Mockito.verifyNoInteractions(agePredicateInjector);
        Mockito.verifyNoInteractions(parentPredicateInjector);
        Mockito.verifyNoInteractions(recognizedPredicateInjector);
    }

    @Test
    public void inject_parentPredicate_isInject() {
        // GIVEN
        ParentPredicate predicate = new ParentPredicate();

        // WHEN
        targetingPredicateInjector.inject(predicate);

        // THEN
        Mockito.verify(parentPredicateInjector).injectMembers(predicate);
        Mockito.verifyNoInteractions(spendFrequencyPredicateInjector);
        Mockito.verifyNoInteractions(spendValuePredicateInjector);
        Mockito.verifyNoInteractions(primePredicateInjector);
        Mockito.verifyNoInteractions(agePredicateInjector);
        Mockito.verifyNoInteractions(recognizedPredicateInjector);
    }

    @Test
    public void inject_recognizedPredicate_isInject() {
        // GIVEN
        RecognizedTargetingPredicate predicate = new RecognizedTargetingPredicate();

        // WHEN
        targetingPredicateInjector.inject(predicate);

        // THEN
        Mockito.verify(recognizedPredicateInjector).injectMembers(predicate);
        Mockito.verifyNoInteractions(spendFrequencyPredicateInjector);
        Mockito.verifyNoInteractions(spendValuePredicateInjector);
        Mockito.verifyNoInteractions(primePredicateInjector);
        Mockito.verifyNoInteractions(parentPredicateInjector);
        Mockito.verifyNoInteractions(agePredicateInjector);
    }

}
