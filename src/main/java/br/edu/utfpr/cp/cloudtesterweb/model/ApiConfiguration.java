package br.edu.utfpr.cp.cloudtesterweb.model;

import br.edu.utfpr.cp.cloudtester.tool.ServiceManagerFactory;
import java.util.Arrays;

/**
 *
 * @author Douglas
 */
public class ApiConfiguration {

    private final ServiceManagerFactory factory;
    private final String containerName;
    private final PlatformType platform;
    private final ApiType api;
    private final FeatureType[] features;

    public ApiConfiguration(ServiceManagerFactory factory, String containerName,
            PlatformType platform, ApiType api, FeatureType... features) {
        this.factory = factory;
        this.containerName = containerName;
        this.platform = platform;
        this.api = api;
        this.features = features;
    }

    public ServiceManagerFactory getFactory() {
        return factory;
    }

    public String getContainerName() {
        return containerName;
    }

    public PlatformType getPlatform() {
        return platform;
    }

    public ApiType getApi() {
        return api;
    }

    public FeatureType[] getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n============\n");
        builder.append("factory: ").append(factory).append('\n');
        builder.append("containerName: ").append(containerName).append('\n');
        builder.append("platform: ").append(platform).append('\n');
        builder.append("api: ").append(api).append('\n');
        builder.append("features: ").append(Arrays.toString(features));
        return builder.toString();
    }
}
