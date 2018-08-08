package com.github.ulisesbocchio.demo;

import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderBuilder;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderConfigurerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableSAMLSSOWhenNotInTest
public class SpringBootSecuritySAMLDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSecuritySAMLDemoApplication.class, args);
    }

    @Configuration
    public static class MvcConfig implements WebMvcConfigurer {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("index");
            registry.addViewController("/protected").setViewName("protected");
            registry.addViewController("/afterlogout").setViewName("afterlogout");

        }
    }

    @Configuration
    public static class MyServiceProviderConfig extends ServiceProviderConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .regexMatchers("/")
                    .permitAll();
        }

        @Override
        public void configure(ServiceProviderBuilder serviceProvider) throws Exception {
            // @formatter:off
            serviceProvider
                .metadataGenerator()
                .entityId("localhost-demo")
            .and()
                .sso()
                .defaultSuccessURL("/home")
                .idpSelectionPageURL("/idpselection")
            .and()
                .logout()
                .defaultTargetURL("/afterlogout")
            .and()
                .metadataManager()
                .metadataLocations("classpath:/idp-ssocircle.xml")
                .refreshCheckInterval(0)
            .and()
                .extendedMetadata()
                .idpDiscoveryEnabled(true)
            .and()
                .keyManager()
                .privateKeyDERLocation("classpath:/localhost.key.der")
                .publicKeyPEMLocation("classpath:/localhost.cert")
            .and()
                .samlContextProviderLb()
                .scheme("http")
                .contextPath("/")
                .serverName("localhost")
                .serverPort(8080)
                .includeServerPortInRequestURL(true)
            .and();
            // @formatter:on

        }
    }
}
