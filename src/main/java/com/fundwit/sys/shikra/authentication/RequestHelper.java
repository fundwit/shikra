package com.fundwit.sys.shikra.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class RequestHelper {
    public static boolean isPostLoginPath(ServerHttpRequest request, String loginPath) {
        return HttpMethod.POST.equals(request.getMethod()) && loginPath!=null && loginPath.equals(request.getPath().value());
    }
    public static boolean isWithAuthorization(ServerHttpRequest request) {
        return request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
    }
    public static boolean isBearerAuthorization(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return authorization!=null && authorization.toLowerCase().startsWith("bearer ");
    }
    public static boolean isBasicAuthorization(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return authorization!=null && authorization.toLowerCase().startsWith("basic ");
    }
}
