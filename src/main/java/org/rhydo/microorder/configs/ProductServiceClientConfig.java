package org.rhydo.microorder.configs;

import org.rhydo.microorder.clients.ProductServiceClient;
import org.rhydo.microorder.exceptions.AppException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ProductServiceClientConfig {

    @Bean
    public ProductServiceClient productServiceClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder
                .baseUrl("https://micro-product")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new AppException("Product not found");
                })
                .build();

        RestClientAdapter adpter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adpter)
                .build();

        return factory.createClient(ProductServiceClient.class);
    }
}
