package qunar.tc.bistoury.application.k8s.service;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.utils.InputStreamPumper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class K8sService {

    @Value("${masterUrl}")
    String masterUrl;
    @Value("${token}")
    String token;

    public KubernetesClient buildClient() {
        Config config = new ConfigBuilder()
                .withMasterUrl(masterUrl)
                .withOauthToken(token)
                .withTrustCerts(true)
                .build();
        return new DefaultKubernetesClient(config);
    }

    public DeploymentList deploymentList(String namespace) {
        try (KubernetesClient client = buildClient()) {
            return client.apps().deployments().inNamespace(namespace).list();
        }
    }

    public Deployment deployment(String namespace, String name) {

        try (KubernetesClient client = buildClient()) {
            return client.apps().deployments().inNamespace(namespace)
                    .withName(name).get();
        }
    }

    public boolean exec(String podNamespace, String podName, String command) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (KubernetesClient client = buildClient();

             ExecWatch watch = client.pods().inNamespace(podNamespace).withName(podName)
//                     .inContainer("")
                     .redirectingInput()
                     .redirectingOutput()
                     .redirectingError()
                     .redirectingErrorChannel()
                     .exec();

             InputStreamPumper pump = new InputStreamPumper(watch.getOutput(), new SystemOutCallback())) {

            executorService.submit(pump);
            watch.getInput().write((command + "\n").getBytes());
            Thread.sleep(1000);
        } catch (Exception e) {
//            throw KubernetesClientException.launderThrowable(e);
            e.printStackTrace();
            return false;
        } finally {
            executorService.shutdownNow();
        }
        return true;
    }

    private static class SystemOutCallback implements Callback<byte[]> {
        @Override
        public void call(byte[] data) {
            System.out.print(new String(data));
        }
    }

    public PodList podList(String namespace, String name) {
        try (KubernetesClient client = buildClient()) {
            Deployment deployment = client.apps().deployments().inNamespace(namespace)
                    .withName(name).get();
            Map<String, String> matchLabels = deployment.getSpec().getSelector().getMatchLabels();
            return client.pods().inNamespace(namespace)
                    .withLabels(matchLabels).list();
        }
    }


}
