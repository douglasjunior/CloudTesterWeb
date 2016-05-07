package br.edu.utfpr.cp.cloudtesterweb.model;

import br.edu.utfpr.cp.cloudtester.tool.ServiceManagerFactory;

/**
 *
 * @author Douglas
 */
public class ApiConfiguration {

    private final ServiceManagerFactory factory;
    private final String containerName;
    private final PlatformType platform;
    private final ApiType api;
    private final ServiceType[] services;

    public ApiConfiguration(ServiceManagerFactory factory, String containerName,
            PlatformType platform, ApiType api, ServiceType... services) {
        this.factory = factory;
        this.containerName = containerName;
        this.platform = platform;
        this.api = api;
        this.services = services;
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

    public ServiceType[] getServices() {
        return services;
    }

}
