package br.edu.utfpr.cp.cloudtesterweb.model;

import br.edu.utfpr.cp.cloudtester.tool.ServiceManagerFactory;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Douglas
 */
public class CloudConfiguration {

    private final ServiceManagerFactory factory;
    private final String containerName;
    private final PlatformType platform;
    private final ApiType api;
    private final FeatureType[] features;

    public CloudConfiguration(ServiceManagerFactory factory, String containerName,
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
    
    public String getFeaturesToString(){
        return Arrays.toString(features);
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.containerName);
        hash = 73 * hash + Objects.hashCode(this.platform);
        hash = 73 * hash + Objects.hashCode(this.api);
        hash = 73 * hash + Arrays.deepHashCode(this.features);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CloudConfiguration other = (CloudConfiguration) obj;
        if (!Objects.equals(this.containerName, other.containerName)) {
            return false;
        }
        if (this.platform != other.platform) {
            return false;
        }
        if (this.api != other.api) {
            return false;
        }
        if (!Arrays.deepEquals(this.features, other.features)) {
            return false;
        }
        return true;
    }

    public boolean match(PlatformType platform, ApiType api, FeatureType feature) {
        return this.platform.equals(platform) && this.api.equals(api)
                && Arrays.binarySearch(features, feature) >= 0;
    }

}
