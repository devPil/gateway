package io.pilju.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LogFilter extends AbstractGatewayFilterFactory<LogFilter.Config> {

    public LogFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(LogFilter.Config config) {
        return new OrderedGatewayFilter(((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Request [{}] {}", request.getMethod(), request.getURI());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Response [{}] {}, {}", request.getMethod(), request.getURI(), response.getStatusCode());
            }));
        }), Ordered.LOWEST_PRECEDENCE);
    }
    public static class Config {
    }
}
