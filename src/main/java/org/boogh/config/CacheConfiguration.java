package org.boogh.config;

import io.github.jhipster.config.jcache.BeanClassLoaderAwareJCacheRegionFactory;
import org.boogh.domain.*;
import org.boogh.repository.UserRepository;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@EnableCaching
@Profile("!test")
public class CacheConfiguration {

    private final RedissonConfiguration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(ApplicationProperties applicationProperties) {
        BeanClassLoaderAwareJCacheRegionFactory.setBeanClassLoader(this.getClass().getClassLoader());
        String address = applicationProperties.getRedis().getAddress();
        String password = applicationProperties.getRedis().getPassword();
        Config config = new Config();
        config.useSingleServer()
            .setPassword(password)
            .setAddress(address);
        jcacheConfiguration = (RedissonConfiguration<Object, Object>) RedissonConfiguration.fromConfig(config);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            cm.createCache(UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            cm.createCache(User.class.getName(), jcacheConfiguration);
            cm.createCache(Authority.class.getName(), jcacheConfiguration);
            cm.createCache(User.class.getName() + ".authorities", jcacheConfiguration);
            cm.createCache(Reporter.class.getName(), jcacheConfiguration);
            cm.createCache(Report.class.getName(), jcacheConfiguration);
            cm.createCache(Report.class.getName() + ".comments", jcacheConfiguration);
            cm.createCache(Report.class.getName() + ".reportStatuses", jcacheConfiguration);
            cm.createCache(Report.class.getName() + ".honks", jcacheConfiguration);
            cm.createCache(Comment.class.getName(), jcacheConfiguration);
            cm.createCache(ReportStatus.class.getName(), jcacheConfiguration);
            cm.createCache(Vote.class.getName(), jcacheConfiguration);
            cm.createCache(Comment.class.getName() + ".votes", jcacheConfiguration);
            cm.createCache(Honk.class.getName(), jcacheConfiguration);
            cm.createCache(Document.class.getName(), jcacheConfiguration);
            cm.createCache(QA.class.getName(), jcacheConfiguration);
            cm.createCache(TelegramChat.class.getName(), jcacheConfiguration);
            cm.createCache(org.boogh.domain.Feedback.class.getName(), jcacheConfiguration);
            cm.createCache(org.boogh.domain.Memcache.class.getName(), jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
            cm.createCache("MemCache", jcacheConfiguration);
        };
    }
}
