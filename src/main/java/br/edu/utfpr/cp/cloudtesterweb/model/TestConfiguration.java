package br.edu.utfpr.cp.cloudtesterweb.model;

import br.edu.utfpr.cp.cloudtester.tool.FeatureManagerFactory;

/**
 *
 * @author Douglas
 */
public class TestConfiguration {

    private final FeatureManagerFactory factory;
    private final String containerName;
    private final PlatformType platform;
    private final ApiType api;
    private final FeatureType[] features;

    public TestConfiguration(FeatureManagerFactory factory, String containerName,
            PlatformType platform, ApiType api, FeatureType... features) {
        this.factory = factory;
        this.containerName = containerName;
        this.platform = platform;
        this.api = api;
        this.features = features;
    }

    public FeatureManagerFactory getFactory() {
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

}
