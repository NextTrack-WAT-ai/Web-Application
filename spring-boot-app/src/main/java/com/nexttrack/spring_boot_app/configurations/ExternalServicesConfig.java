package com.nexttrack.spring_boot_app.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "external")
public class ExternalServicesConfig {
    private String reshuffleUrl;
    private String featureExtractionUrl;

    public String getReshuffleUrl() { return reshuffleUrl; }
    public void setReshuffleUrl(String reshuffleUrl) { this.reshuffleUrl = reshuffleUrl; }

    public String getFeatureExtractionUrl() { return featureExtractionUrl; }
    public void setFeatureExtractionUrl(String featureExtractionUrl) { this.featureExtractionUrl = featureExtractionUrl; }
}
