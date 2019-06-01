package com.fundwit.sys.shikra.authentication;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authentication.ServerHttpBasicAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.MediaTypeServerWebExchangeMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfiguration {
    @Autowired
    private List<AuthenticationProvider> authenticationProviders;

    @Value("${auth.channels.basic.realm}")
    private String basicAuthRealm = "Realm";

    @Bean
    public ReactiveAuthenticationManagerAdapter reactiveAuthenticationManagerAdapter(ProviderManager providerManager){
        return new ReactiveAuthenticationManagerAdapter(providerManager);
    }

    @Bean
    public ProviderManager providerManager(){
        return new ProviderManager(authenticationProviders);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
         http.authorizeExchange()
                .pathMatchers("/auth/**",
                        "/static/**",
                        "/register", "/register/**",
                        "/verifier/**",
                        "/error")
                .permitAll()
                .anyExchange().authenticated().and()
                .csrf().disable();

         this.addCustomBasicAuthenticationWebFilter(http, providerManager());

         return http.build();
    }

    private void addCustomBasicAuthenticationWebFilter(ServerHttpSecurity http, AuthenticationManager authenticationManager){
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(new ReactiveAuthenticationManagerAdapter(authenticationManager));
        authenticationFilter.setServerAuthenticationConverter(new ServerHttpBasicAuthenticationConverter());
        authenticationFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        HttpBasicServerAuthenticationEntryPoint entryPoint = new HttpBasicServerAuthenticationEntryPoint();
        if(!StringUtils.isNullOrEmpty(basicAuthRealm)) {
            entryPoint.setRealm(basicAuthRealm);
        }
        authenticationFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(entryPoint));

        MediaTypeServerWebExchangeMatcher restMatcher = new MediaTypeServerWebExchangeMatcher(
                MediaType.APPLICATION_ATOM_XML,
                MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML,
                MediaType.MULTIPART_FORM_DATA, MediaType.TEXT_XML);
        restMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));

        // WARNING 覆盖了 ServerHttpSecurity 的 exceptionHandling().authenticationEntryPoint 配置
        List<DelegatingServerAuthenticationEntryPoint.DelegateEntry> entryPoints = new ArrayList<>();
        entryPoints.add(new DelegatingServerAuthenticationEntryPoint.DelegateEntry(restMatcher, entryPoint));

        DelegatingServerAuthenticationEntryPoint result = new DelegatingServerAuthenticationEntryPoint(entryPoints);
        result.setDefaultEntryPoint(entryPoints.get(entryPoints.size() - 1).getEntryPoint());
        http.exceptionHandling().authenticationEntryPoint(result);

        http.httpBasic().disable();
        http.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC);
    }
}
