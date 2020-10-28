package qunar.tc.bistoury.proxy.config.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProxyGlobalConf {

    @Autowired
    Environment environment;


    /**
     * ip:http_port:websocket_port
     */
    @Value("${proxy.url:bistoury-proxy:9095:9013}")
    String proxyUrl;

    @Value("${proxy.server.port}")
    String serverPort;

    @Value("${proxy.agent.newport}")
    String agentPort;

    @Value("${proxy.jstack.location:jstack}")
    String jstackLocation;

    @Value("${proxy.jsatat.location:jstat}")
    String jstatLocation;

    Map<String, String> map = new HashMap() {
        @Override
        public Object get(Object key) {
            return environment.getProperty("proxy." + key);
        }
    };


    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(String agentPort) {
        this.agentPort = agentPort;
    }

    public String getJstackLocation() {
        return jstackLocation;
    }

    public void setJstackLocation(String jstackLocation) {
        this.jstackLocation = jstackLocation;
    }

    public String getJstatLocation() {
        return jstatLocation;
    }

    public void setJstatLocation(String jstatLocation) {
        this.jstatLocation = jstatLocation;
    }
}
