package com.fundwit.sys.shikra.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.MediaTypeServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Configuration
public class SecurityConfiguration {
    @Autowired
    private List<AuthenticationProvider> authenticationProviders;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtServiceProperties jwtProperties;

    @Value("${auth.channels.basic.realm}")
    private String basicAuthRealm;

    @Value("${auth.login.path}")
    public String authLoginPath;
    @Value("${auth.form.endpoint:/}")
    private String formEndPoint;

    @Bean
    public ReactiveAuthenticationManagerAdapter reactiveAuthenticationManagerAdapter(ProviderManager providerManager){
        return new ReactiveAuthenticationManagerAdapter(providerManager);
    }

    @Bean
    public ProviderManager providerManager(){
        return new ProviderManager(authenticationProviders);
    }

    @Bean
    public ServerSecurityContextRepository serverSecurityContextRepository(){
        return new WebSessionServerSecurityContextRepository();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
         http.authorizeExchange()
                .pathMatchers("/auth/**", "/health/**", "/build", "/build/**",
                        "/static/**",
                        "/register", "/register/**",
                        "/verifier/**",
                        "/error")
                .permitAll()
                .anyExchange().authenticated().and()
                .csrf().disable();
         this.addBasicAuthentication(http, providerManager());
         return http.build();
    }

    private void addBasicAuthentication(ServerHttpSecurity http, AuthenticationManager authenticationManager){
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(new ReactiveAuthenticationManagerAdapter(authenticationManager));

        // requires authentication:
        //     POST authLoginPath
        // or  AUTHORIZATION header is exist
        RequiresAuthenticationMatcher requiresAuthenticationMatcher = new RequiresAuthenticationMatcher(authLoginPath);
        authenticationFilter.setRequiresAuthenticationMatcher(requiresAuthenticationMatcher);

        authenticationFilter.setServerAuthenticationConverter(new AuthorizationHeaderServerAuthenticationConverter()); // support basic and bearer authentication
        authenticationFilter.setSecurityContextRepository(serverSecurityContextRepository());

        // success handling:
        //    POST authLoginPath: set token, send response directly
        //    other request: set token, continue filter chain
        authenticationFilter.setAuthenticationSuccessHandler(new JwtTokenSignAuthenticationSuccessHandler(
                requiresAuthenticationMatcher, objectMapper, jwtProperties, formEndPoint));

        ServerAuthenticationEntryPoint authenticationEntryPoint = serverAuthenticationEntryPoint();
        authenticationFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(authenticationEntryPoint));
        http.exceptionHandling().authenticationEntryPoint(serverAuthenticationEntryPoint());

        http.httpBasic().disable();
        http.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC);
    }

    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint(){
        DelegatingServerAuthenticationEntryPoint delegatingEntryPoint =
                new DelegatingServerAuthenticationEntryPoint(
                        basicEntryPointDelegate(),
                        statusEntryPointDelegate()
                        // redirectEntryPointDelegate()
                );
        delegatingEntryPoint.setDefaultEntryPoint(basicEntryPointDelegate().getEntryPoint());
        return delegatingEntryPoint;
    }

    private DelegatingServerAuthenticationEntryPoint.DelegateEntry basicEntryPointDelegate() {
        HttpBasicServerAuthenticationEntryPoint basicEntryPoint = new HttpBasicServerAuthenticationEntryPoint();
        if(!StringUtils.isNullOrEmpty(basicAuthRealm)) {
            basicEntryPoint.setRealm(basicAuthRealm);
        }
        ServerWebExchangeMatcher basicMatcher = exchange -> Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(v->v.toUpperCase().startsWith("basic "))
                .map(v-> ServerWebExchangeMatcher.MatchResult.match()).orElse(ServerWebExchangeMatcher.MatchResult.notMatch());
        return new DelegatingServerAuthenticationEntryPoint.DelegateEntry(basicMatcher, basicEntryPoint);
    }

    private DelegatingServerAuthenticationEntryPoint.DelegateEntry statusEntryPointDelegate() {
        HttpStatusServerEntryPoint basicEntryPoint = new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED);
        MediaTypeServerWebExchangeMatcher restMatcher = new MediaTypeServerWebExchangeMatcher(
                MediaType.APPLICATION_JSON);
        restMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        return new DelegatingServerAuthenticationEntryPoint.DelegateEntry(restMatcher, basicEntryPoint);
    }

//    private DelegatingServerAuthenticationEntryPoint.DelegateEntry redirectEntryPointDelegate() {
//        RedirectServerAuthenticationEntryPoint redirectEntryPoint = new RedirectServerAuthenticationEntryPoint("/auth/login");
//        MediaTypeServerWebExchangeMatcher restMatcher = new MediaTypeServerWebExchangeMatcher(
//                MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM);
//        restMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
//        return new DelegatingServerAuthenticationEntryPoint.DelegateEntry(restMatcher, redirectEntryPoint);
//    }
}
