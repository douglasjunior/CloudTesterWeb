package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtester.aws.AWSServiceManagerFactory;
import br.edu.utfpr.cp.cloudtester.azure.AzureServiceManagerFactory;
import br.edu.utfpr.cp.cloudtester.jclouds.JCloudsServiceManagerFactory;
import br.edu.utfpr.cp.cloudtester.tool.Authentication;
import br.edu.utfpr.cp.cloudtester.tool.ServiceManagerFactory;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiType;
import br.edu.utfpr.cp.cloudtesterweb.model.PlatformType;
import br.edu.utfpr.cp.cloudtesterweb.model.CloudConfiguration;
import br.edu.utfpr.cp.cloudtesterweb.model.FeatureType;
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
public class CloudController {

    private static final String PROP_IDENTITY_AZURE = "IDENTITY_AZURE";
    private static final String PROP_CREDENTIAL_AZURE = "CREDENTIAL_AZURE";
    private static final String PROP_CONTAINER_NAME_AZURE = "CONTAINER_NAME_AZURE";

    private static final String PROP_IDENTITY_AWS = "IDENTITY_AWS";
    private static final String PROP_CREDENTIAL_AWS = "CREDENTIAL_AWS";
    private static final String PROP_CONTAINER_NAME_AWS = "CONTAINER_NAME_AWS";
    private static final String PROP_REGION_AWS = "REGION_AWS";

    private static final String PROVIDER_AZURE_BLOB = "azureblob";
    private static final String PROVIDER_AWS_S3 = "aws-s3";
    private static final String PROVIDER_AWS_SQS = "aws-sqs";

    private final Properties credentials = new Properties();

    private final List<CloudConfiguration> configurations = new ArrayList<>();

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
                PROVIDER_AZURE_BLOB, null);
        String containerAzure = credentials.getProperty(PROP_CONTAINER_NAME_AZURE);

        Authentication authAWS = new Authentication(
                credentials.getProperty(PROP_IDENTITY_AWS),
                credentials.getProperty(PROP_CREDENTIAL_AWS),
                PROVIDER_AWS_S3, PROVIDER_AWS_SQS);
        String containerAWS = credentials.getProperty(PROP_CONTAINER_NAME_AWS);
        String regionAws = credentials.getProperty(PROP_REGION_AWS);

        // jclouds AZURE
        addConfiguration(new JCloudsServiceManagerFactory(authAzure, null, null), containerAzure,
                PlatformType.AZURE, ApiType.JCLOUDS,
                FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD
        );
        // jclouds AWS
        addConfiguration(new JCloudsServiceManagerFactory(authAWS, regionAws, null), containerAWS,
                PlatformType.AWS, ApiType.JCLOUDS,
                FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD);
        // Azure native
        addConfiguration(new AzureServiceManagerFactory(authAzure, null), containerAzure,
                PlatformType.AZURE, ApiType.AZURE_NATIVE,
                FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD);
        // AWS native
        addConfiguration(new AWSServiceManagerFactory(authAWS, regionAws), containerAWS,
                PlatformType.AWS, ApiType.AWS_NATIVE,
                FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD);

        System.out.println("Configurations: " + configurations);
    }

    private void addConfiguration(ServiceManagerFactory factory, String containerName, PlatformType platform, ApiType api, FeatureType... features) {
        configurations.add(new CloudConfiguration(factory, containerName, platform, api, features));
    }

    public List<CloudConfiguration> getConfigurations() {
        return new ArrayList<>(configurations);
    }

    public List<CloudConfiguration> getConfigurations(List<PlatformType> platforms, List<ApiType> apis, List<FeatureType> features) {
        List<CloudConfiguration> configs = new ArrayList<>();
        for (CloudConfiguration conf : configurations) {
            if (platforms.contains(conf.getPlatform())
                    && apis.contains(conf.getApi())
                    && features.containsAll(Arrays.asList(conf.getFeatures()))) {
                configs.add(conf);
            }
        }
        return configs;
    }

    public CloudConfiguration getConfiguration(PlatformType platform, ApiType api, FeatureType feature) {
        for (CloudConfiguration config : configurations) {
            if (config.match(platform, api, feature)) {
                return config;
            }
        }
        return null;
    }

}
