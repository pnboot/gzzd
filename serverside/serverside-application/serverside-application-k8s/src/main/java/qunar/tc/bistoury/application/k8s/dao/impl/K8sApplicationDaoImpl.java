package qunar.tc.bistoury.application.k8s.dao.impl;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.application.api.pojo.Application;
import qunar.tc.bistoury.application.k8s.dao.ApplicationDao;
import qunar.tc.bistoury.application.k8s.service.K8sService;
import qunar.tc.bistoury.application.k8s.utils.AppCodeUtil;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class K8sApplicationDaoImpl implements ApplicationDao {
    private static final Logger logger = LoggerFactory.getLogger(K8sApplicationDaoImpl.class);

    @Value("${namespace:gzzd}")
    String namespace;

    @Autowired
    K8sService k8sService;

    /**
     * 此处的地址是从 pod 内访问proxy的地址，
     * 如果是在本地调试则是本地的ip地址
     * 如果是在集群部署则为 service name
     */
    @Value("${proxy.server.proxyIp:serverside-ui}")
    String proxyIp;

    @Value("${server.port:9091}")
    String proxyWebPort;


    @Override
    public int updateApplication(Application application) {
        return 0;
    }

    @Override
    public Application getApplicationByAppCode(String appCode) {
        return getApplication(deployment(appCode));
    }

    @Override
    public List<Application> getApplicationsByAppCodes(List<String> appCodes) {
        return appCodes.stream()
                .map(this::deployment)
                .filter(Objects::nonNull)
                .map(this::getApplication)
                .collect(Collectors.toList());
    }

    @Override
    public int createApplication(Application application) {
        return 0;
    }

    @Override
    public List<Application> getAllApplications() {
        DeploymentList deploymentList = k8sService.deploymentList(namespace);
        return deploymentList.getItems().stream().map(this::getApplication).collect(Collectors.toList());
    }

    private Deployment deployment(String appCode) {
        AppCodeUtil.AppCodeBean appCodeBean = AppCodeUtil.parseAppCode(appCode);
        return k8sService.deployment(appCodeBean.getNamespace(), appCodeBean.getName());
    }


    private Application getApplication(Deployment deployment) {
        Application application = new Application();
        String ts = deployment.getMetadata().getCreationTimestamp();
        Date date = new Date(ZonedDateTime.parse(ts).toInstant().toEpochMilli());
        application.setCreateTime(date);
        application.setCreator(deployment.getMetadata().getName());
        application.setGroupCode(deployment.getMetadata().getNamespace());
        application.setName(deployment.getMetadata().getName());
        application.setStatus(1);
        application.setCode(AppCodeUtil.buildAppCode(deployment.getMetadata().getNamespace(), deployment.getMetadata().getName()));
        return application;
    }

    @Override
    public boolean checkExist(String appCode) {
        return true;
    }

    private static final String START_AGENT_COMMAND = "wget -O /tmp/start-gzzd-agent.sh http://%s/start-gzzd-agent.sh && nohup sh /tmp/start-gzzd-agent.sh %s &";

    @Override
    public boolean loadAgent(String host) {
        String proxyServerUrl = proxyIp + ":" + proxyWebPort;
        String command = String.format(START_AGENT_COMMAND, proxyServerUrl, proxyServerUrl);
        logger.info("load agent , namespace: {} , host: {} , command:{}", namespace, host, command);
        System.out.println(command);
        return k8sService.exec(namespace, host, command);
    }

}
