package qunar.tc.bistoury.proxy.config.properties;

import java.util.HashMap;


public class GlobalConfig {

    // cat agent_config.properties  | grep -v "#" | awk -F '=' '{print "put(\""$1"\", \""$2"\");"}'

//    static final public HashMap<String, String> globalMap = new HashMap() {{
//        put("server.port", "9013");
//        put("agent.newport", "9014");
//        put("jstack.location", "jstack");
//        put("jstat.location", "jstat");
//    }};


    static final public HashMap<String, String> profilerMap = new HashMap() {{
        put("default.duration", "120");
        put("default.interval", "1");
        put("default.threads", "false");
        put("default.mode", "0");
        put("default.event", "itimer");
    }};

    static final public HashMap<String, String> agent_configMap = new HashMap() {{
        put("agent.refresh.interval.min", "10");
        put("agent.push.interval.min", "1");
        put("app.config.exclusion.file.suffix", "class,vm,css,js,vue,ts,jsp,sql,jar");
        put("app.config.exclusion.file.equal", "web.xml");
        put("tomcat.user", "tomcat");
        put("tomcat.command", "/home/java/default/bin/java");
        put("debug.json.limit.kb", "10240");
        put("download.kb.per.second", "10240");
        put("profiler.compact.package.prefix", "java.;javax.;sun.;org.springframework.;org.jboss.;org.apache.;com.sun.;org.mybatis.;com.mysql.;io.netty.;com.google.;ch.qos.;org.slf4j.;io.termd.core.;com.taobao.arthas.;com.taobao.middleware.");

    }};
    static final public HashMap<String, String> serverMap = new HashMap() {{
        put("tomcat.port", "9090");
        put("tomcat.basedir", "/tmp/bistoury/tomcat/proxy");
    }};


}
