package configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@EnableJpaRepositories(basePackages = "repository")
@org.springframework.context.annotation.Configuration
public class Configuration {

  @Bean
  RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  CacheManager cacheManager() {
    return new EhCacheCacheManager(cacheMangerFactory().getObject());
  }

  @Bean
  EhCacheManagerFactoryBean cacheMangerFactory() {
    EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
    bean.setShared(true);
    return bean;
  }
}
