package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import java.util.Optional;
import java.util.concurrent.TimeUnit;



public class CacheMe {

    static final CacheLoader<AdvertisementContent, Optional<TargetingGroup>> contentLoader = new CacheLoader<>() {
        @Override
        public Optional<TargetingGroup> load(AdvertisementContent content) throws Exception {
            return Optional.empty();
        }
    };

    private static final RemovalListener<AdvertisementContent, Optional<TargetingGroup>> notifier = n -> {
        if (n.wasEvicted()) {
            String cause = n.getCause().name();
            System.out.printf("Cache hit -> {%s} %nCause -> {%s}", n.getKey(), cause);
        }
    };

    static final LoadingCache<AdvertisementContent, Optional<TargetingGroup>> contentCacher =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .removalListener(notifier)
                    .build(contentLoader);

}

