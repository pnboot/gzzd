package qunar.tc.bistoury.application.k8s.utils;

public class AppCodeUtil {

    private static final String SPLITER = ":";

    public static String buildAppCode(String namespace, String name) {
        return namespace + SPLITER + name;
    }

    public static AppCodeBean parseAppCode(String appCode) {
        int pos = appCode.indexOf(SPLITER);
        return new AppCodeBean(appCode.substring(0, pos ), appCode.substring(pos+1));
    }

    public static class AppCodeBean {
        String namespace;
        String name;

        public AppCodeBean(String namespace, String name) {
            this.namespace = namespace;
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getName() {
            return name;
        }


    }
}
