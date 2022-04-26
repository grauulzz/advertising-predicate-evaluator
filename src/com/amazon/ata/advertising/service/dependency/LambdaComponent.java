package com.amazon.ata.advertising.service.dependency;


import com.amazon.ata.advertising.service.activity.AddTargetingGroupActivity;
import com.amazon.ata.advertising.service.activity.CreateContentActivity;
import com.amazon.ata.advertising.service.activity.DeleteContentActivity;
import com.amazon.ata.advertising.service.activity.GenerateAdActivity;
import com.amazon.ata.advertising.service.activity.UpdateClickThroughRateActivity;
import com.amazon.ata.advertising.service.activity.UpdateContentActivity;
import com.amazon.ata.advertising.service.dao.ContentDao;
import com.amazon.ata.advertising.service.dao.TargetingGroupDao;

import dagger.Component;

import javax.inject.Singleton;

/**
 * The interface Lambda component.
 */
@Singleton
@Component(modules = {
        ExternalServiceModule.class,
        DaoModule.class,
        DynamoDBModule.class
})
public interface LambdaComponent {

    /**
     * Gets targeting predicate injector.
     *
     * @return the targeting predicate injector
     */
    TargetingPredicateInjector getTargetingPredicateInjector();

    /**
     * Provide generate ad activity generate ad activity.
     *
     * @return the generate ad activity
     */
    GenerateAdActivity provideGenerateAdActivity();

    /**
     * Provide add targeting group activity add targeting group activity.
     *
     * @return the add targeting group activity
     */
    AddTargetingGroupActivity provideAddTargetingGroupActivity();

    /**
     * Provide create content activity create content activity.
     *
     * @return the create content activity
     */
    CreateContentActivity provideCreateContentActivity();

    /**
     * Provide delete content activity delete content activity.
     *
     * @return the delete content activity
     */
    DeleteContentActivity provideDeleteContentActivity();

    /**
     * Provide update click through rate activity update click through rate activity.
     *
     * @return the update click through rate activity
     */
    UpdateClickThroughRateActivity provideUpdateClickThroughRateActivity();

    /**
     * Provide update content activity update content activity.
     *
     * @return the update content activity
     */
    UpdateContentActivity provideUpdateContentActivity();

    /**
     * Provide content dao content dao.
     *
     * @return the content dao
     */
    ContentDao provideContentDao();

    /**
     * Provide targeting group dao targeting group dao.
     *
     * @return the targeting group dao
     */
    TargetingGroupDao provideTargetingGroupDao();
}
