package io.pilju.gateway.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.pilju.gateway.gatewayException.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;

import static io.pilju.gateway.gatewayException.ExceptionEnum.*;

@Component
@Slf4j
public class CustomJwtFilter extends AbstractGatewayFilterFactory<CustomJwtFilter.Config> {

    @Value("{jwt.key}")
    private String key;

    final static private String BEARER = "Bearer";

    public CustomJwtFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            try {
                verifyToken(request);
            } catch (ApiException e) {
                return handleUnAuthorized(exchange);
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {
    }

    private void verifyToken(ServerHttpRequest request) throws ApiException {
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(HttpHeaders.AUTHORIZATION);

        if (ObjectUtils.isEmpty(tokens)) { // 토큰이 없음.
            log.info("NOT_FOUND_TOKEN_EXCEPTION :: {}" , NOT_FOUND_TOKEN_EXCEPTION.getMessage());
            throw new ApiException(NOT_FOUND_TOKEN_EXCEPTION);
        }

        String token = getToken(tokens.get(0));
        String[] parts = token.split("\\.");
        String tokenHeader = decodeBase64(parts[0]);

        // 지원하지 않은 알고리즘
        if (!tokenHeader.contains(SignatureAlgorithm.HS256.getValue())) {
            log.info("INVALID_TOKEN_EXCEPTION :: {}" , INVALID_TOKEN_EXCEPTION.getMessage());
            throw new ApiException(INVALID_TOKEN_EXCEPTION);
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJwt(getToken(tokens.get(0)));
        } catch (ExpiredJwtException e) { // 만료된 토큰
            log.info("ExpiredJwtException :: {}", e.getMessage());
            log.info(e.getMessage());
            throw new ApiException(EXPIRED_TOKEN_EXCEPTION);
        } catch (DecodingException e) { // 잘못된 토큰형식
            log.info("DecodingException message :: {}", e.getMessage());
            throw new ApiException(INVALID_TOKEN_EXCEPTION);
        } catch (UnsupportedJwtException | SecurityException | MalformedJwtException | IllegalArgumentException e ) { // 기타 Exception
            log.info("ETC Exception :: {}", e.getMessage());
            throw new ApiException(INVALID_TOKEN_EXCEPTION);
        }

    }

    private static String decodeBase64(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    private String getToken(String token) {
        return token.replace(BEARER + " ", "");
    }

    private Mono<Void> handleUnAuthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }}
