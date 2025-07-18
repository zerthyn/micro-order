package org.rhydo.microorder.configs;

import org.rhydo.microorder.clients.UserServiceClient;
import org.rhydo.microorder.exceptions.AppException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class UserServiceClientConfig {

    @Bean
    public UserServiceClient userServiceClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder
                .baseUrl("https://micro-user")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new AppException("User not found");
                })
                .build();

        RestClientAdapter adpter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adpter)
                .build();

        return factory.createClient(UserServiceClient.class);
    }
}
