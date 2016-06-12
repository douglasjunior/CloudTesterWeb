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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Douglas
 */
@Startup
@Singleton
public class CloudController {

    private static final String PROVIDER_AZURE_BLOB = "azureblob";
    private static final String PROVIDER_AWS_S3 = "aws-s3";
    private static final String PROVIDER_AWS_SQS = "aws-sqs";

    private final Properties credentials = new Properties();

    private final List<CloudConfiguration> configurations = new ArrayList<>();
    private Authentication authAzure;
    private String regionAzure;
    private Authentication authAWS;
    private String regionAws;

    @PostConstruct
    void startup() {
        try {
            loadCredentials();
            loadConfiguration();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadCredentials() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("credentials.properties")) {
            credentials.load(is);
            System.out.println("Credentials: " + credentials);
            authAzure = new Authentication(
                    credentials.getProperty("IDENTITY_AZURE"),
                    credentials.getProperty("CREDENTIAL_AZURE"),
                    PROVIDER_AZURE_BLOB, null);
            regionAzure = "";
            authAWS = new Authentication(
                    credentials.getProperty("IDENTITY_AWS"),
                    credentials.getProperty("CREDENTIAL_AWS"),
                    PROVIDER_AWS_S3, PROVIDER_AWS_SQS);
            regionAws = credentials.getProperty("REGION_AWS");
        }
    }

    private void loadConfiguration() {
        loadStorage();
        //loadQueue();
        //loadVM();
        //loadDatabase();

        System.out.println("Configurations: " + configurations);
    }

    private void loadStorage() {
        final String containerAWS = credentials.getProperty("CONTAINER_NAME_AWS");
        final String containerAzure = credentials.getProperty("CONTAINER_NAME_AZURE");

        // jclouds AZURE
        addConfiguration(new JCloudsServiceManagerFactory(authAzure, null, null), containerAzure,
                PlatformType.AZURE, ApiType.JCLOUDS,
                FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD, FeatureType.STORAGE_LIST, FeatureType.STORAGE_DELETE
        );
        // jclouds AWS
        addConfiguration(new JCloudsServiceManagerFactory(authAWS, regionAws, null), containerAWS,
                PlatformType.AWS, ApiType.JCLOUDS,
                FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD, FeatureType.STORAGE_LIST, FeatureType.STORAGE_DELETE);
        // Azure native
        addConfiguration(new AzureServiceManagerFactory(authAzure, null), containerAzure,
                PlatformType.AZURE, ApiType.AZURE_NATIVE,
                FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD, FeatureType.STORAGE_LIST, FeatureType.STORAGE_DELETE);
        // AWS native
        addConfiguration(new AWSServiceManagerFactory(authAWS, regionAws), containerAWS,
                PlatformType.AWS, ApiType.AWS_NATIVE,
                FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD, FeatureType.STORAGE_LIST, FeatureType.STORAGE_DELETE);

    }

    private void addConfiguration(ServiceManagerFactory factory, String containerName, PlatformType platform, ApiType api, FeatureType... features) {
        configurations.add(new CloudConfiguration(factory, containerName, platform, api, features));
    }

    public List<CloudConfiguration> getConfigurations() {
        return new ArrayList<>(configurations);
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
