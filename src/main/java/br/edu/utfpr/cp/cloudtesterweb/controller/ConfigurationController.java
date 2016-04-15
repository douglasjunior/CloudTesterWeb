package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtester.aws.AWSFeatureManagerFactory;
import br.edu.utfpr.cp.cloudtester.azure.AzureFeatureManagerFactory;
import br.edu.utfpr.cp.cloudtester.tool.Authentication;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiType;
import br.edu.utfpr.cp.cloudtesterweb.model.FeatureType;
import br.edu.utfpr.cp.cloudtesterweb.model.PlatformType;
import br.edu.utfpr.cp.cloudtesterweb.model.TestConfiguration;
import java.io.InputStream;
import java.util.ArrayList;
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
public class ConfigurationController {

    private static final String PROP_IDENTITY_AZURE = "IDENTITY_AZURE";
    private static final String PROP_CREDENTIAL_AZURE = "CREDENTIAL_AZURE";
    private static final String PROP_CONTAINER_NAME_AZURE = "CONTAINER_NAME_AZURE";

    private static final String PROP_IDENTITY_AWS = "IDENTITY_AWS";
    private static final String PROP_CREDENTIAL_AWS = "CREDENTIAL_AWS";
    private static final String PROP_CONTAINER_NAME_AWS = "CONTAINER_NAME_AWS";

    private static final String PROVIDER_AZURE = "azureblob";
    private static final String PROVIDER_AWS = "aws-s3";

    private final Properties credentials = new Properties();

    private final List<TestConfiguration> configurations = new ArrayList<>();

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
//        // jclouds AZURE
//        configurations.add(new TestConfiguration(new JCloudFeatureManagerFactory(authAzure),
//                containerAzure,
//                PlatformType.AZURE,
//                ApiType.JCLOUDS,
//                FeatureType.STORE_UPLOAD, FeatureType.STORE_DOWNLOAD
//        ));
//        // jclouds AWS
//        configurations.add(new TestConfiguration(new JCloudFeatureManagerFactory(authAWS),
//                containerAWS,
//                PlatformType.AWS,
//                ApiType.JCLOUDS,
//                 FeatureType.STORE_UPLOAD, FeatureType.STORE_DOWNLOAD
//        ));
        // Azure native
        configurations.add(new TestConfiguration(new AzureFeatureManagerFactory(authAzure),
                containerAzure,
                PlatformType.AZURE,
                ApiType.AZURE_NATIVE,
                FeatureType.STORE_UPLOAD, FeatureType.STORE_DOWNLOAD
        ));
        // AWS native
        configurations.add(new TestConfiguration(new AWSFeatureManagerFactory(authAWS),
                containerAWS,
                PlatformType.AWS,
                ApiType.AWS_NATIVE,
                FeatureType.STORE_UPLOAD, FeatureType.STORE_DOWNLOAD
        ));

        System.out.println("Configurations: " + configurations);
    }

    public List<TestConfiguration> getConfigurations() {
        return new ArrayList<>(configurations);
    }

}
