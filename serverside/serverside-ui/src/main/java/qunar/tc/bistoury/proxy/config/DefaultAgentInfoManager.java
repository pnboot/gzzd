/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.proxy.config;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.application.api.AppServerService;
import qunar.tc.bistoury.application.api.pojo.AppServer;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.proxy.generator.IdGenerator;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 14:21
 */
@Service
public class DefaultAgentInfoManager implements AgentInfoManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAgentInfoManager.class);

    private Map<String, String> agentConfig = new HashMap() {{
        put("agent.refresh.interval.min", "10");
        put("agent.push.interval.min=", "1");
        put("app.config.exclusion.file.suffix", "class,vm,css,js,vue,ts,jsp,sql,jar");
        put("app.config.exclusion.file.equal=", "web.xml");
        put("tomcat.user", "tomcat");
        put("tomcat.command", "/home/java/default/bin/java");
        put("debug.json.limit.kb", "10240");
        put("download.kb.per.second", "10240");
        put("profiler.compact.package.prefix", "java.;javax.;sun.;org.springframework.;org.jboss.;org.apache.;com.sun.;org.mybatis.;com.mysql.;io.netty.;com.google.;ch.qos.;org.slf4j.;io.termd.core.;com.taobao.arthas.;com.taobao.middleware.");

    }};

    @Autowired
    private IdGenerator generator;

    @Autowired
    private AgentConnectionStore agentConnectionStore;

//    @Autowired
//    private AgentInfoOverride agentInfoOverride;

    @Autowired
    private AppServerService appServerService;

    @PostConstruct
    public void init() {
//        DynamicConfigLoader.<LocalDynamicConfig>load("agent_config.properties", false)
//                .addListener(conf -> agentConfig = conf.asMap());
    }

    @Override
    public ListenableFuture<Map<String, String>> getAgentInfo(String ip) {
        SettableFuture<Map<String, String>> resultFuture = SettableFuture.create();
        AppServer appServer = this.appServerService.getAppServerByIp(ip);
        Map<String, String> agentInfo = new HashMap<>();
        if (appServer != null) {
            agentInfo.put("port", String.valueOf(appServer.getPort()));
            agentInfo.put("cpuJStackOn", String.valueOf(appServer.isAutoJStackEnable()));
            agentInfo.put("heapJMapHistoOn", String.valueOf(appServer.isAutoJMapHistoEnable()));
        }
        //这里可以覆盖所有版本配置
        agentInfo.putAll(agentConfig);

        final int version = getVersion(ip);
        //这里可以覆盖版本低于指定版本的配置
        // agentInfoOverride.overrideAgentInfo(agentInfo, version);
        resultFuture.set(agentInfo);
        return resultFuture;
    }

    @Override
    public void updateAgentInfo(List<String> agentIds) {
        agentIds.forEach(agentId -> {
            Optional<AgentConnection> optionalAgentConnection = agentConnectionStore.getConnection(agentId);
            if (optionalAgentConnection.isPresent()) {
                logger.info("notify agent {} update meta info ", agentId);
                AgentConnection agentConnection = optionalAgentConnection.get();
                agentConnection.write(RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_REFRESH_TIP.getCode(), generator.generateId(), null));
            }
        });
    }

    private int getVersion(String ip) {
        Optional<AgentConnection> connection = agentConnectionStore.getConnection(ip);
        if (connection.isPresent()) {
            return connection.get().getVersion();
        }
        return -1;
    }
}
