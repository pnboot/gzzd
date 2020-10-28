package qunar.tc.bistoury.application.k8s.dao.impl;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.application.k8s.dao.ApplicationUserDao;
import qunar.tc.bistoury.application.k8s.service.K8sService;
import qunar.tc.bistoury.application.k8s.utils.AppCodeUtil;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationUserDaoImpl implements ApplicationUserDao {

    @Autowired
    K8sService k8sService;

    @Value("${namespace:gzzd}")
    String namespace;

    @Override
    public List<String> getAppCodesByUserCode(String userCode) {
        DeploymentList deploymentList = k8sService.deploymentList(namespace);
        return deploymentList.getItems().stream().map(
                this::getAppCode).collect(Collectors.toList());

    }

    private String getAppCode(Deployment deployment) {
        String namespace = deployment.getMetadata().getNamespace();
        String name = deployment.getMetadata().getName();
        return AppCodeUtil.buildAppCode(namespace, name);
    }

    @Override
    public List<String> getUsersByAppCode(String appCode) {
        return Collections.singletonList("admin");
    }

    @Override
    public int addAppUser(String userCode, String appCode) {
        return 0;
    }

    @Override
    public void batchAddAppUser(List<String> userCodes, String addCode) {

    }

    @Override
    public int removeAppUser(String userCode, String appCode) {
        return 0;
    }
}
