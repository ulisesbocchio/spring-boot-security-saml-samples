package com.github.ulisesbocchio.demo;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.EnableSAMLSSO;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderBuilder;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderConfigurerAdapter;
import com.github.ulisesbocchio.spring.boot.security.saml.user.SAMLUserDetails;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.security.BasicSecurityConfiguration;
import org.opensaml.xml.signature.SignatureConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableSAMLSSO
public class Auth0SSODemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Auth0SSODemoApplication.class, args);
    }

    @Configuration
    public static class MvcConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("index");
            registry.addViewController("/protected").setViewName("protected");
            registry.addViewController("/unprotected/help").setViewName("help");

        }
    }

    static class CustomSAMLBootstrap extends SAMLBootstrap {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            super.postProcessBeanFactory(beanFactory);
            BasicSecurityConfiguration config = (BasicSecurityConfiguration) org.opensaml.Configuration.getGlobalSecurityConfiguration();
            config.registerSignatureAlgorithmURI("RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
            config.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        }
    }

    @Bean
    public SAMLUserDetailsService userDetailsService() {
        return new SAMLUserDetailsService() {
            @Override
            public Object loadUserBySAML(SAMLCredential samlCredential) throws UsernameNotFoundException {
                return new SAMLUserDetails(samlCredential) {
                    @Override
                    public Map<String, String> getAttributes() {
                        return samlCredential.getAttributes().stream()
                                .collect(Collectors.toMap(Attribute::getName, this::getValue));
                    }

                    private String getValue(Attribute attribute) {
                        return Optional.ofNullable(getAttribute(attribute.getName())).orElse("");
                    }
                };
            }
        };
    }

    @Bean
    public static SAMLBootstrap SAMLBootstrap() {
        return new CustomSAMLBootstrap();
    }

    @Configuration
    public static class MyServiceProviderConfig extends ServiceProviderConfigurerAdapter {

        @Autowired
        SAMLUserDetailsService userDetailsService;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeRequests()
                .antMatchers("/unprotected/**")
                .permitAll()
            .and()
                .anonymous();
            // @formatter:on
        }

        @Override
        public void configure(ServiceProviderBuilder serviceProvider) throws Exception {
            // @formatter:off
            serviceProvider
                .metadataGenerator()
                .entityId("localhost-demo")
                .bindingsSSO("post")
            .and()
                .sso()
                .defaultSuccessURL("/home")
                .idpSelectionPageURL("/idpselection")
            .and()
                .metadataManager()
                .metadataLocations("classpath:/idp-auth0-new.xml")
                .refreshCheckInterval(0)
            .and()
                .extendedMetadata()
                .idpDiscoveryEnabled(true)//set to false for no IDP Selection page.
            .and()
                .keyManager()
                .storeLocation("classpath:/KeyStore.jks")
                .storePass("123456")
                .defaultKey("localhost")
                .keyPassword("localhost", "123456")
            .and()
                .authenticationProvider()
                .userDetailsService(userDetailsService);
            // @formatter:on

        }
    }
}
