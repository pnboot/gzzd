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

package qunar.tc.bistoury.proxy.web.controller;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.application.api.ApplicationService;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @author leix.xie
 * @date 2019/5/23 16:22
 * @describe
 */
@Controller
public class ProxyConfigForAgentGetController {

    private ProxyConfig proxyConfig;

    @Value("${proxy.server.nodePort:31480}")
    int serverNodePort;
    @Value("${proxy.server.uiIp:127.0.0.1}")
    String uiIp;


    @Value("${proxy.server.proxyIp:127.0.0.1}")
    String proxyIp;

    @Value("${proxy.agent.newport:9014}")
    int agentNewport;

    @Value("${proxy.agent.heartbeatSec:30}")
    int heartbeatSec;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    private AgentConnectionStore agentConnectionStore;

    @PostConstruct
    public void init() {
        proxyConfig = new ProxyConfig(
                proxyIp,
                agentNewport, heartbeatSec);
    }

    /**
     * 前端页面获取指定 agent 对应 proxy的 websocket 访问地址
     *
     * @param agentIp
     * @return
     */
    @RequestMapping("getProxyWebSocketUrl")
    @ResponseBody
    public ApiResult getProxyWebSocketUrl(@RequestParam String agentIp, @RequestParam String host) {
        if (Strings.isNullOrEmpty(agentIp)) {
            return ResultHelper.fail(-2, "no agent ip");
        }
        // 访问一下agent接口，判断agent是否存在
        Optional<AgentConnection> connection = agentConnectionStore.getConnection(agentIp);
        if (!connection.isPresent() || !connection.get().isActive()) {
            // 拉起 agent
            applicationService.loadAgent(host);
        }
        // 组装 websocket 链接地址给页面，一般1-2秒就可以拉起 agent
        String proxyWebSocketUrl = "ws://" + uiIp + ":" + serverNodePort + "/ws";
        return ResultHelper.success(100, "new proxy", proxyWebSocketUrl);
    }


    /**
     * agent 启动时请求这个地址获取 proxy 的 websocket 地址
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/proxy/config/foragent")
    public ApiResult getProxyConfig() {
        return ResultHelper.success(proxyConfig);
    }

    private static class ProxyConfig {

        private final String ip;

        private final int port;

        private final int heartbeatSec;

        private ProxyConfig(String ip, int port, int heartbeatSec) {
            this.ip = ip;
            this.port = port;
            this.heartbeatSec = heartbeatSec;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public int getHeartbeatSec() {
            return heartbeatSec;
        }

        @Override
        public String toString() {
            return "ProxyConfig{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", heartbeatSec=" + heartbeatSec +
                    '}';
        }
    }
}
