package qunar.tc.bistoury.application.k8s.dao.impl;

import io.fabric8.kubernetes.api.model.PodList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.application.api.pojo.AppServer;
import qunar.tc.bistoury.application.k8s.dao.AppServerDao;
import qunar.tc.bistoury.application.k8s.service.K8sService;
import qunar.tc.bistoury.application.k8s.utils.AppCodeUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class K8sAppServerDaoImpl implements AppServerDao {

    @Value("${namespace:gzzd}")
    String namespace;

    @Autowired
    K8sService k8sService;

    @Override
    public List<AppServer> getAppServerByAppCode(String appCode) {
        AppCodeUtil.AppCodeBean appCodeBean = AppCodeUtil.parseAppCode(appCode);


        PodList podList = k8sService.podList(appCodeBean.getNamespace(), appCodeBean.getName());
        return podList.getItems().stream().map(pod -> {
            AppServer server = new AppServer();
            server.setAppCode(appCode);
            server.setHost(pod.getMetadata().getName());
            server.setIp(pod.getStatus().getPodIP());
            server.setLogDir("/tmp");
            server.setAutoJMapHistoEnable(true);
            server.setAutoJStackEnable(true);
            server.setPort(80);
            return server;
        }).collect(Collectors.toList());

    }

    @Override
    public AppServer getAppServerByIp(String ip) {
        return null;
    }

    @Override
    public AppServer getAppServerByServerId(String serverId) {
        return null;
    }

    @Override
    public int addAppServer(AppServer appServer) {
        return 0;
    }

    @Override
    public int updateAppServer(AppServer appServer) {
        return 0;
    }

    @Override
    public int changeAutoJMapHistoEnable(String serverId, boolean enable) {
        return 0;
    }

    @Override
    public int changeAutoJStackEnable(String serverId, boolean enable) {
        return 0;
    }

    @Override
    public int deleteAppServerByIp(String ip) {
        return 0;
    }

    @Override
    public int deleteAppServerByServerId(String serverId) {
        return 0;
    }
}
