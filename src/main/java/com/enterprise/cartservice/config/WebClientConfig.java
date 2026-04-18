package com.enterprise.cartservice.config;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import java.time.Duration; import java.util.concurrent.TimeUnit;
@Configuration @Slf4j
public class WebClientConfig {
    @Value("${product.service.base-url}") private String productServiceBaseUrl;
    @Bean public WebClient productServiceWebClient() {
        HttpClient http = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofSeconds(10))
            .doOnConnected(c -> c
                .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));
        return WebClient.builder().baseUrl(productServiceBaseUrl)
            .clientConnector(new ReactorClientHttpConnector(http))
            .filter(ExchangeFilterFunction.ofRequestProcessor(r -> {
                log.info("[WC] {} {}", r.method(), r.url()); return Mono.just(r);
            }))
            .defaultHeader("Content-Type","application/json").build();
    }
}
