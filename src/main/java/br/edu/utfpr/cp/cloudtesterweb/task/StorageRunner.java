package br.edu.utfpr.cp.cloudtesterweb.task;

import br.edu.utfpr.cp.cloudtester.tool.ServiceManagerFactory;
import br.edu.utfpr.cp.cloudtester.tool.Resource;
import br.edu.utfpr.cp.cloudtester.tool.ResourceFile;
import br.edu.utfpr.cp.cloudtester.tool.StoreManager;
import br.edu.utfpr.cp.cloudtesterweb.controller.ApiController;
import br.edu.utfpr.cp.cloudtesterweb.dao.DaoStateless;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiType;
import br.edu.utfpr.cp.cloudtesterweb.model.ServiceType;
import br.edu.utfpr.cp.cloudtesterweb.model.TestEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.FileEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.PlatformType;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiConfiguration;
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
    private ApiController configurationController;

    @Inject
    private DaoStateless dao;

    public void run() {
        UserTransaction tx = context.getUserTransaction();
        try {
            tx.begin();
            List<FileEntity> files = dao.createNamedQuery(FileEntity.FIND_TO_EXECUTE, FileEntity.class).getResultList();
            if (!files.isEmpty()) {
                FileEntity file = files.get(0);
                // percorre cada configuração para execução
                for (ApiConfiguration config : configurationController.getConfigurations()) {
                    ServiceManagerFactory factory = config.getFactory();
                    PlatformType platform = config.getPlatform();
                    ApiType api = config.getApi();
                    String containerName = config.getContainerName();
                    // percorre cada service configurado
                    for (ServiceType service : config.getServices()) {
                        TestEntity test = new TestEntity(file, platform, api, service, containerName);
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

    private void executeTest(TestEntity test, ServiceManagerFactory factory) {
        switch (test.getService()) {
            case STORE_DOWNLOAD:
            case STORE_UPLOAD:
                downloadUploadTest(test, factory);
                break;
        }
    }

    private void downloadUploadTest(TestEntity test, ServiceManagerFactory factory) {
        System.out.println("Testing " + test.getService() + " service in " + factory);
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
                    if (test.getService() == ServiceType.STORE_UPLOAD) {
                        storeManager.stores(resource, test.getContainerName());
                    } else {
                        storeManager.retrieves(file.getName(), test.getContainerName());
                    }
                    end = new Date();
                    System.out.println(test.getService() + " file " + file.getName() + " in " + diff(start, end) + " millis");
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
