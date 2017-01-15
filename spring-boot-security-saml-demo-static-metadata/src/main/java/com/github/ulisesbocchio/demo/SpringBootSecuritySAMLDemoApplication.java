package com.github.ulisesbocchio.demo;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.EnableSAMLSSO;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderBuilder;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderConfigurerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableSAMLSSO
public class SpringBootSecuritySAMLDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSecuritySAMLDemoApplication.class, args);
    }

    @Configuration
    public static class MvcConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("index");
            registry.addViewController("/protected").setViewName("protected");

        }
    }

    @Configuration
    public static class MyServiceProviderConfig extends ServiceProviderConfigurerAdapter {
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
                .defaultTargetURL("/")
            .and()
                .metadataManager()
                .metadataLocations("classpath:/idp-ssocircle.xml")
                .localMetadataLocation("classpath:/sp-ssocircle.xml")
                .refreshCheckInterval(0)
            .and()
                .extendedMetadata()
                .idpDiscoveryEnabled(true)
            .and()
                .localExtendedMetadata()
                .securityProfile("metaiop")
                .sslSecurityProfile("pkix")
                .signMetadata(true)
                .signingKey("localhost")
                .encryptionKey("localhost")
                .requireArtifactResolveSigned(false)
                .requireLogoutRequestSigned(false)
                .idpDiscoveryEnabled(true)
            .and()
                //This Keystore contains also the public key of idp.ssocircle.com
                .keyManager()
                .storeLocation("classpath:/localhost.jks")
                .storePass("foobar")
                .defaultKey("localhost")
                .keyPassword("localhost", "foobar");
            // @formatter:on
        }
    }
}
