package com.n11bootcamp.payment_service.config;

import com.iyzipay.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IyzicoConfig {

    @Value("${iyzico.api.key}")
    private String apiKey;

    @Value("${iyzico.api.secret}")
    private String secretKey;

    @Value("${iyzico.api.base-url}")
    private String baseUrl;

    @Bean // 2. Bu metodun üzerinde @Bean olduğundan emin ol
    public Options options() {
        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);
        return options;
    }
}