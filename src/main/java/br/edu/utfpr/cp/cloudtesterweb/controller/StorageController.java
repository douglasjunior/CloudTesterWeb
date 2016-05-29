package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.csv.StorageHeaders;
import br.edu.utfpr.cp.cloudtesterweb.dao.DaoStatefull;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiType;
import br.edu.utfpr.cp.cloudtesterweb.model.ErrorMessageEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.FeatureType;
import br.edu.utfpr.cp.cloudtesterweb.model.StorageEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.PlatformType;
import br.edu.utfpr.cp.cloudtesterweb.model.TestEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.TestExecutionEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.transaction.UserTransaction;
import org.primefaces.model.UploadedFile;
import static br.edu.utfpr.cp.cloudtesterweb.util.Constants.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Douglas
 */
@Named
@ViewScoped
public class StorageController implements Serializable {

    @EJB
    private DaoStatefull dao;

    @Resource
    private UserTransaction tx;

    private List<StorageEntity> storages;
    private String errorMessage = "";
    private UploadedFile uploadedFile;
    private int times = 1;

    public StorageController() {
    }

    public void upload() {
        try {
            File fileOut = copyFile();
            tx.begin();
            StorageEntity entity = new StorageEntity();
            entity.setDateTime(new Date());
            entity.setName(uploadedFile.getFileName());
            entity.setContentLength(uploadedFile.getSize());
            entity.setContentType(uploadedFile.getContentType());
            entity.setContentPath(fileOut.getAbsolutePath());
            entity.setConfigTestTimes(times);
            dao.insert(entity);
            tx.commit();
            storages.add(entity);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesful", uploadedFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                tx.rollback();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", uploadedFile.getFileName() + " is not uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void delete(StorageEntity storage) {
        System.out.println("Delete: " + storage);
        try {
            tx.begin();
            dao.createNamedQuery(ErrorMessageEntity.DELETE_BY_FILE, new String[]{"sotrageId"}, new Object[]{storage.getId()}).executeUpdate();
            dao.delete(storage);
            tx.commit();
            storages.remove(storage);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesful", storage.getName() + " deleted.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                tx.rollback();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", storage.getName() + " is not deleted.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public List<StorageEntity> getFiles() {
        if (storages == null) {
            refresh();
        }
        return storages;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public void refresh() {
        storages = dao.createNamedQuery(StorageEntity.FIND_ALL, StorageEntity.class).getResultList();
        dao.refreshAll(storages);
    }

    public void viewError(TestExecutionEntity exec) {
        ErrorMessageEntity error = dao.findById(ErrorMessageEntity.class, exec.getErrorMessageId());
        if (error != null) {
            errorMessage = error.getMessage();
        } else {
            errorMessage = "";
        }
    }

    public StreamedContent downloadCSV(StorageEntity file) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(baos);
                CSVPrinter printer = CSVFormat.DEFAULT.withHeader(StorageHeaders.class).print(osw)) {

            for (TestEntity test : file.getTests()) {
                for (TestExecutionEntity exec : test.getExecutions()) {
                    printer.print(test.getPlatform());
                    printer.print(test.getApi());
                    printer.print(test.getFeature());
                    printer.print(exec.getDuration());
                    printer.print(exec.getSuccess());
                    printer.println();
                }
            }
            printer.flush();

            InputStream stream = new ByteArrayInputStream(baos.toByteArray());
            StreamedContent content = new DefaultStreamedContent(stream, "text/csv", file.getName() + ".csv");
            return content;
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Download failed.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return null;
    }

    public UploadedFile getFile() {
        return uploadedFile;
    }

    public void setFile(UploadedFile file) {
        this.uploadedFile = file;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private File copyFile() throws IOException {
        File directory = new File(UPLOADED_FOLDER);
        directory.mkdirs();
        File fileOut = new File(directory, uploadedFile.getFileName());
        try (FileOutputStream fos = new FileOutputStream(fileOut);
                InputStream is = uploadedFile.getInputstream();) {
            IOUtils.copy(is, fos);
        }
        return fileOut;
    }

}
