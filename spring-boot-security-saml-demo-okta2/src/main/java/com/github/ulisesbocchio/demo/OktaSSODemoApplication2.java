package com.github.ulisesbocchio.demo;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.EnableSAMLSSO;
import com.github.ulisesbocchio.spring.boot.security.saml.bean.SAMLConfigurerBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableSAMLSSO
public class OktaSSODemoApplication2 {

    public static void main(String[] args) {
        SpringApplication.run(OktaSSODemoApplication2.class, args);
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

    @Configuration
    public static class MyServiceProviderConfig extends WebSecurityConfigurerAdapter {

        public MyServiceProviderConfig() {
            super(false);
        }

        @Bean
        SAMLConfigurerBean saml() {
            return new SAMLConfigurerBean();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);
        }

        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeRequests()
                    .antMatchers("/unprotected/**")
                    .permitAll()
                .and()
                    .httpBasic()
                    .disable()
                    .csrf()
                    .disable()
                    .anonymous()
                .and()
                    .apply(saml())
                    .serviceProvider()
                        .metadataGenerator()
                        .entityId("localhost-demo")
                        .bindingsSSO("artifact", "post", "paos")
                    .and()
                        .ecpProfile()
                    .and()
                        .sso()
                        .defaultSuccessURL("/home")
                        .idpSelectionPageURL("/idpselection")
                    .and()
                        .metadataManager()
                        .metadataLocations("classpath:/idp-okta.xml")
                        .refreshCheckInterval(0)
                    .and()
                        .extendedMetadata()
                        .ecpEnabled(true)
                        .idpDiscoveryEnabled(true)//set to false for no IDP Selection page.
                    .and()
                        .keyManager()
                        .privateKeyDERLocation("classpath:/localhost.key.der")
                        .publicKeyPEMLocation("classpath:/localhost.cert")
                    .and()
                .http()
                    .authorizeRequests()
                    .requestMatchers(saml().endpointsMatcher()).permitAll()
                .and()
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated();
            // @formatter:on
        }
    }
}
