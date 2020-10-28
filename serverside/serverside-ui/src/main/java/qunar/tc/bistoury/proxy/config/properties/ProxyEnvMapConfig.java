package qunar.tc.bistoury.proxy.config.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProxyEnvMapConfig {
    @Autowired
    Environment environment;

    private Map<String, String> map = new HashMap() {
        @Override
        public Object get(Object key) {
            return environment.getProperty("proxy." + key);
        }
    };

    public Map<String, String> getMap() {
        return map;
    }
}
