package com.amazon.ata.advertising.service.dependency;

//import com.amazon.coral.dagger.annotations.CoralComponent;
//import com.amazon.coral.service.lambda.LambdaEndpoint;

import com.amazon.ata.advertising.service.activity.*;
import com.amazon.ata.advertising.service.dao.ContentDao;
import com.amazon.ata.advertising.service.dao.TargetingGroupDao;
import dagger.Component;
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
     *
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
