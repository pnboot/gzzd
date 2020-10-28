package qunar.tc.bistoury.ui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "config")
@EnableConfigurationProperties(UrlRedirectMapConfig.class)
public class UrlRedirectMapConfig {
    private Map<String, String> urlRedirect = new HashMap<>();


    public Map<String, String> getUrlRedirect() {
        return urlRedirect;
    }

    public void setUrlRedirect(Map<String, String> urlRedirect) {
        this.urlRedirect = urlRedirect;
    }
}
