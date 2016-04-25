package br.edu.utfpr.cp.cloudtesterweb.task;

import br.edu.utfpr.cp.cloudtester.tool.FeatureManagerFactory;
import br.edu.utfpr.cp.cloudtester.tool.Resource;
import br.edu.utfpr.cp.cloudtester.tool.ResourceFile;
import br.edu.utfpr.cp.cloudtester.tool.StoreManager;
import br.edu.utfpr.cp.cloudtesterweb.controller.ConfigurationController;
import br.edu.utfpr.cp.cloudtesterweb.dao.DaoStateless;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiType;
import br.edu.utfpr.cp.cloudtesterweb.model.FeatureType;
import br.edu.utfpr.cp.cloudtesterweb.model.TestEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.FileEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.PlatformType;
import br.edu.utfpr.cp.cloudtesterweb.model.TestConfiguration;
import br.edu.utfpr.cp.cloudtesterweb.model.TestExecutionEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author Douglas
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class StorageTestRunner implements Serializable {

    @javax.annotation.Resource
    private EJBContext context;

    @Inject
    private ConfigurationController configurationController;

    @Inject
    private DaoStateless dao;

    public void run() {
        UserTransaction tx = context.getUserTransaction();
        try {
            tx.begin();
            List<FileEntity> files = dao.createNamedQuerie(FileEntity.FIND_TO_EXECUTE, FileEntity.class).getResultList();
            if (!files.isEmpty()) {
                FileEntity file = files.get(0);
                // percorre cada configuração para execução
                for (TestConfiguration config : configurationController.getConfigurations()) {
                    FeatureManagerFactory factory = config.getFactory();
                    PlatformType platform = config.getPlatform();
                    ApiType api = config.getApi();
                    String containerName = config.getContainerName();
                    // percorre cada feature configurada
                    for (FeatureType feature : config.getFeatures()) {
                        TestEntity test = new TestEntity(file, platform, api, feature, containerName);
                        dao.insert(test);
                        executeTest(test, factory);
                    }
                }
                file.setCompleted(true);
                dao.update(file);
            }
            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                tx.rollback();
            } catch (Exception ex1) {
            }
        }
    }

    private void executeTest(TestEntity test, FeatureManagerFactory factory) {
        switch (test.getFeature()) {
            case STORE_DOWNLOAD:
            case STORE_UPLOAD:
                downloadUploadTest(test, factory);
                break;
        }
    }

    private void downloadUploadTest(TestEntity test, FeatureManagerFactory factory) {
        System.out.println("Testing feature " + test.getFeature() + " in " + factory);
        FileEntity file = test.getFile();
        Date start = null, end = null;
        List<TestExecutionEntity> executions = new ArrayList<>();
        try (StoreManager storeManager = factory.createStoreManager()) {
            for (int i = 0; i < file.getTestTimesConfig(); i++) {
                TestExecutionEntity exec = new TestExecutionEntity();
                exec.setTest(test);
                try {
                    Resource resource = new ResourceFile(file.getContentPath());
                    start = new Date();
                    if (test.getFeature() == FeatureType.STORE_UPLOAD) {
                        storeManager.stores(resource, test.getContainerName());
                    } else {
                        storeManager.retrieves(file.getName(), test.getContainerName());
                    }
                    end = new Date();
                    System.out.println(test.getFeature() + " file " + file.getName() + " in " + diff(start, end) + " millis");
                    exec.setDateTimeStart(start);
                    exec.setDateTimeEnd(end);
                    exec.setSuccess(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    String error = ExceptionUtils.getStackTrace(ex);
                    exec.setDateTimeStart(start);
                    exec.setDateTimeEnd(end);
                    exec.setSuccess(false);
                    exec.setErrorMessage(error);
                }
                executions.add(exec);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        insertExecutions(executions);
    }

    private static long diff(Date start, Date end) {
        return end.getTime() - start.getTime();
    }

    private void insertExecutions(List<TestExecutionEntity> executions) {
        System.out.println("Saving executions: " + executions);
        for (TestExecutionEntity exec : executions) {
            dao.insert(exec);
        }
    }
}
