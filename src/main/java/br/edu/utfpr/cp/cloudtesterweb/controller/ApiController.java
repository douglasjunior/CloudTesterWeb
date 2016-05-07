package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtester.aws.AWSServiceManagerFactory;
import br.edu.utfpr.cp.cloudtester.azure.AzureServiceManagerFactory;
import br.edu.utfpr.cp.cloudtester.jclouds.JCloudServiceManagerFactory;
import br.edu.utfpr.cp.cloudtester.tool.Authentication;
import br.edu.utfpr.cp.cloudtester.tool.ServiceManagerFactory;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiType;
import br.edu.utfpr.cp.cloudtesterweb.model.ServiceType;
import br.edu.utfpr.cp.cloudtesterweb.model.PlatformType;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiConfiguration;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Douglas
 */
@Startup
@Singleton
public class ApiController {

    private static final String PROP_IDENTITY_AZURE = "IDENTITY_AZURE";
    private static final String PROP_CREDENTIAL_AZURE = "CREDENTIAL_AZURE";
    private static final String PROP_CONTAINER_NAME_AZURE = "CONTAINER_NAME_AZURE";

    private static final String PROP_IDENTITY_AWS = "IDENTITY_AWS";
    private static final String PROP_CREDENTIAL_AWS = "CREDENTIAL_AWS";
    private static final String PROP_CONTAINER_NAME_AWS = "CONTAINER_NAME_AWS";

    private static final String PROVIDER_AZURE = "azureblob";
    private static final String PROVIDER_AWS = "aws-s3";

    private final Properties credentials = new Properties();

    private final List<ApiConfiguration> configurations = new ArrayList<>();

    @PostConstruct
    private void startup() {
        loadCredentials();
        loadConfiguration();
    }

    @PreDestroy
    private void shutdown() {
    }

    private void loadCredentials() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("credentials.properties")) {
            credentials.load(is);
            System.out.println("Credentials: " + credentials);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadConfiguration() {
        Authentication authAzure = new Authentication(
                credentials.getProperty(PROP_IDENTITY_AZURE),
                credentials.getProperty(PROP_CREDENTIAL_AZURE),
                PROVIDER_AZURE);
        String containerAzure = credentials.getProperty(PROP_CONTAINER_NAME_AZURE);

        Authentication authAWS = new Authentication(
                credentials.getProperty(PROP_IDENTITY_AWS),
                credentials.getProperty(PROP_CREDENTIAL_AWS),
                PROVIDER_AWS);
        String containerAWS = credentials.getProperty(PROP_CONTAINER_NAME_AWS);

        // jclouds AZURE
        addConfiguration(new JCloudServiceManagerFactory(authAzure), containerAzure,
                PlatformType.AZURE, ApiType.JCLOUDS,
                ServiceType.STORE_UPLOAD, ServiceType.STORE_DOWNLOAD
        );
        // jclouds AWS
        addConfiguration(new JCloudServiceManagerFactory(authAWS), containerAWS,
                PlatformType.AWS, ApiType.JCLOUDS,
                ServiceType.STORE_UPLOAD, ServiceType.STORE_DOWNLOAD);
        // Azure native
        addConfiguration(new AzureServiceManagerFactory(authAzure), containerAzure,
                PlatformType.AZURE, ApiType.AZURE_NATIVE,
                ServiceType.STORE_UPLOAD, ServiceType.STORE_DOWNLOAD);
        // AWS native
        addConfiguration(new AWSServiceManagerFactory(authAWS), containerAWS,
                PlatformType.AWS, ApiType.AWS_NATIVE,
                ServiceType.STORE_UPLOAD, ServiceType.STORE_DOWNLOAD);

        System.out.println("Configurations: " + configurations);
    }

    private void addConfiguration(ServiceManagerFactory factory, String containerName, PlatformType platform, ApiType api, ServiceType... services) {
        configurations.add(new ApiConfiguration(factory, containerName, platform, api, services));
    }

    public List<ApiConfiguration> getConfigurations() {
        return new ArrayList<>(configurations);
    }

    public List<ApiConfiguration> getConfigurations(List<PlatformType> platforms, List<ApiType> apis, List<ServiceType> services) {
        List<ApiConfiguration> configs = new ArrayList<>();
        for (ApiConfiguration conf : configurations) {
            if (platforms.contains(conf.getPlatform())
                    && apis.contains(conf.getApi())
                    && services.containsAll(Arrays.asList(conf.getServices()))) {
                configs.add(conf);
            }
        }
        return configs;
    }

}
