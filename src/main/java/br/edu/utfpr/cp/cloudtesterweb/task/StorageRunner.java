package br.edu.utfpr.cp.cloudtesterweb.task;

import br.edu.utfpr.cp.cloudtester.tool.ServiceManagerFactory;
import br.edu.utfpr.cp.cloudtester.tool.Resource;
import br.edu.utfpr.cp.cloudtester.tool.ResourceFile;
import br.edu.utfpr.cp.cloudtesterweb.controller.CloudController;
import br.edu.utfpr.cp.cloudtesterweb.dao.DaoStateless;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiType;
import br.edu.utfpr.cp.cloudtesterweb.model.TestEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.StorageEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.PlatformType;
import br.edu.utfpr.cp.cloudtesterweb.model.CloudConfiguration;
import br.edu.utfpr.cp.cloudtesterweb.model.ErrorMessageEntity;
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
import br.edu.utfpr.cp.cloudtester.tool.StorageManager;
import br.edu.utfpr.cp.cloudtesterweb.model.FeatureType;

/**
 *
 * @author Douglas
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class StorageRunner implements Serializable {

    @javax.annotation.Resource
    private EJBContext context;

    @Inject
    private CloudController cloudController;

    @Inject
    private DaoStateless dao;

    public void run() {
        UserTransaction tx = context.getUserTransaction();
        try {
            tx.begin();
            List<StorageEntity> storages = dao.createNamedQuery(StorageEntity.FIND_TO_EXECUTE, StorageEntity.class).getResultList();
            if (!storages.isEmpty()) {
                StorageEntity storage = storages.get(0);
                // percorre cada configuração para execução
                for (CloudConfiguration config : cloudController.getConfigurations()) {
                    ServiceManagerFactory factory = config.getFactory();
                    PlatformType platform = config.getPlatform();
                    ApiType api = config.getApi();
                    String containerName = config.getContainerName();
                    // percorre cada feature configurada
                    for (FeatureType feature : config.getFeatures()) {
                        TestEntity test = new TestEntity(platform, api, feature, containerName);
                        dao.insert(test);
                        storage.addTest(test);
                        dao.update(storage);
                        executeTest(storage, test, factory);
                    }
                }
                storage.setCompleted(true);
                dao.update(storage);
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

    private void executeTest(StorageEntity storage, TestEntity test, ServiceManagerFactory factory) {
        switch (test.getFeature()) {
            case STORAGE_DOWNLOAD:
                downloadUploadTest(storage, test, factory);
                break;
            case STORAGE_UPLOAD:
                downloadUploadTest(storage, test, factory);
                break;
        }
    }

    private void downloadUploadTest(StorageEntity storage, TestEntity test, ServiceManagerFactory factory) {
        System.out.println("Testing " + test.getFeature() + " feature in " + factory);
        Date start = null, end = null;
        List<TestExecutionEntity> executions = new ArrayList<>();
        try (StorageManager storeManager = factory.createStorageManager()) {
            for (int i = 0; i < storage.getConfigTestTimes(); i++) {
                TestExecutionEntity exec = new TestExecutionEntity();
                exec.setTest(test);
                try {
                    Resource resource = new ResourceFile(storage.getContentPath());
                    start = new Date();
                    if (test.getFeature() == FeatureType.STORAGE_UPLOAD) {
                        storeManager.stores(resource, test.getContainerName());
                    } else {
                        storeManager.retrieves(storage.getName(), test.getContainerName());
                    }
                    end = new Date();
                    System.out.println(test.getFeature() + " file " + storage.getName() + " in " + diff(start, end) + " millis");
                    exec.setDateTimeStart(start);
                    exec.setDateTimeEnd(end);
                    exec.setSuccess(true);
                } catch (Exception ex) {
                    System.err.println(ex.toString());
                    String error = ExceptionUtils.getStackTrace(ex);
                    exec.setDateTimeStart(start);
                    exec.setDateTimeEnd(end);
                    exec.setSuccess(false);
                    exec.setErrorMessage(new ErrorMessageEntity(error));
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
            if (!exec.getSuccess()) {
                ErrorMessageEntity em = exec.getErrorMessage();
                dao.insert(em);
                exec.setErrorMessageId(em.getId());
            }
            dao.insert(exec);
        }
    }
}
