package com.amazon.ata.advertising.service.dependency;

//import com.amazon.coral.dagger.annotations.CoralComponent;
//import com.amazon.coral.service.lambda.LambdaEndpoint;

import com.amazon.ata.advertising.service.activity.AddTargetingGroupActivity;
import com.amazon.ata.advertising.service.activity.CreateContentActivity;
import com.amazon.ata.advertising.service.activity.DeleteContentActivity;
import com.amazon.ata.advertising.service.activity.GenerateAdActivity;
import com.amazon.ata.advertising.service.activity.UpdateClickThroughRateActivity;
import com.amazon.ata.advertising.service.activity.UpdateContentActivity;
import com.amazon.ata.advertising.service.dao.ContentDao;
import com.amazon.ata.advertising.service.dao.TargetingGroupDao;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.atacustomerservicelambda.activity.GetCustomerProfileActivity;
import com.amazon.atacustomerservicelambda.activity.GetCustomerSpendCategoriesActivity;
import dagger.Component;
import dagger.Module;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        ExternalServiceModule.class,
        DaoModule.class,
        DynamoDBModule.class
})
public interface LambdaComponent {

    /**
     * Inject's targeting predicates with the DAOs they require.
     * @return a TargetingPredicateInjector
     */
    TargetingPredicateInjector getTargetingPredicateInjector();

    GenerateAdActivity provideGenerateAdActivity();

    AddTargetingGroupActivity provideAddTargetingGroupActivity();

    CreateContentActivity provideCreateContentActivity();

    DeleteContentActivity provideDeleteContentActivity();

    UpdateClickThroughRateActivity provideUpdateClickThroughRateActivity();

    UpdateContentActivity provideUpdateContentActivity();
    ContentDao provideContentDao();

    TargetingGroupDao provideTargetingGroupDao();
}
