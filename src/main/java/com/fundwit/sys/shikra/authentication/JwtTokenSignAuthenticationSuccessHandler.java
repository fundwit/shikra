package com.fundwit.sys.shikra.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;


public class JwtTokenSignAuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler {
    private RequiresAuthenticationMatcher requiresAuthenticationMatcher;
    private JwtService jwtService;
    private JwtServiceProperties jwtProperties;

    private String formEndPoint;

    public JwtTokenSignAuthenticationSuccessHandler(
            RequiresAuthenticationMatcher requiresAuthenticationMatcher, ObjectMapper objectMapper, JwtServiceProperties jwtProperties,
            String fromEndPoint) {
        this.jwtProperties = jwtProperties;
        this.requiresAuthenticationMatcher = requiresAuthenticationMatcher;
        this.jwtService = new JwtService(objectMapper, jwtProperties);
        this.formEndPoint = fromEndPoint;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpRequest rawRequest = webFilterExchange.getExchange().getRequest();
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        // TODO provide user to reactor context
        ServerHttpRequest request = rawRequest.mutate().header("loginUser",  authentication.getName()).build();

        webFilterExchange.getExchange().getResponse().beforeCommit(()->{
            response.getHeaders().add("loginUser", authentication.getName());

            if(RequestHelper.isPostLoginPath(request, requiresAuthenticationMatcher.getAuthLoginPath())) {
                // 生成 JWT
                String authToken = jwtService.buildToken(authentication);
                response.getHeaders().add(HttpHeaders.AUTHORIZATION, authToken);
                // 生成 Cookie
                ResponseCookie authTokenCookie = ResponseCookie.from("auth_token", authToken)
                        .maxAge(jwtProperties.getExpiration())
                        .build();
                response.getCookies().add(authTokenCookie.getName(), authTokenCookie);
            }
            return Mono.empty();
        });

        if(RequestHelper.isPostLoginPath(request, requiresAuthenticationMatcher.getAuthLoginPath())) {
            // if accept json
            if(request.getHeaders().getAccept().stream().anyMatch(m->MediaType.valueOf("text/*").isCompatibleWith(m))) {
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().add(HttpHeaders.LOCATION, formEndPoint);
            }else{
                response.setStatusCode(HttpStatus.OK);
            }
            return Mono.empty();
        }else{
            // continue filter chain
            // ReactiveSecurityContextHolder.withAuthentication(authentication);  // will be done by AuthenticationWebFilter
            return super.onAuthenticationSuccess(webFilterExchange, authentication);
        }
    }
}
