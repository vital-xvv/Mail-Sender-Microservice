package ua.vital.mailsender.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticConfig extends ElasticsearchConfiguration {
    @Value("${elasticsearch.url}")
    private String elasticHostUrl;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticHostUrl)
                .build();
    }
}
