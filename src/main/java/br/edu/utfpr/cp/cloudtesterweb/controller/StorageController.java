package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.csv.StorageHeaders;
import br.edu.utfpr.cp.cloudtesterweb.dao.DaoStatefull;
import br.edu.utfpr.cp.cloudtesterweb.model.ErrorMessageEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.FileEntity;
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
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import org.primefaces.model.UploadedFile;
import static br.edu.utfpr.cp.cloudtesterweb.util.Constants.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
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

    @Inject
    private DaoStatefull dao;

    @Resource
    private UserTransaction tx;

    private List<FileEntity> files;
    private String errorMessage = "";
    private UploadedFile uploadedFile;
    private int times = 1;

    public StorageController() {
    }

    public void upload() {
        try {
            tx.begin();
            File directory = new File(UPLOADED_FOLDER);
            directory.mkdirs();
            File fileOut = new File(directory, uploadedFile.getFileName());
            try (FileOutputStream fos = new FileOutputStream(fileOut);
                    InputStream is = uploadedFile.getInputstream();) {
                IOUtils.copy(is, fos);
            }
            FileEntity entity = new FileEntity();
            entity.setDateTime(new Date());
            entity.setName(uploadedFile.getFileName());
            entity.setContentLength(uploadedFile.getSize());
            entity.setContentType(uploadedFile.getContentType());
            entity.setContentPath(fileOut.getAbsolutePath());
            entity.setTestTimesConfig(times);
            dao.insert(entity);
            tx.commit();
            files.add(entity);
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

    public void delete(FileEntity file) {
        System.out.println("Delete: " + file);
        try {
            tx.begin();
            dao.createNamedQuery(ErrorMessageEntity.DELETE_BY_FILE, new String[]{"file"}, new Object[]{file}).executeUpdate();
            dao.delete(file);
            tx.commit();
            files.remove(file);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesful", file.getName() + " deleted.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                tx.rollback();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", file.getName() + " is not deleted.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public List<FileEntity> getFiles() {
        if (files == null) {
            refresh();
        }
        return files;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public void refresh() {
        files = dao.createNamedQuery(FileEntity.FIND_ALL, FileEntity.class).getResultList();
        dao.refreshAll(files);
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public void viewError(TestExecutionEntity exec) {
        ErrorMessageEntity error = dao.findById(ErrorMessageEntity.class, exec.getErrorMessageId());
        if (error != null) {
            errorMessage = error.getMessage();
        } else {
            errorMessage = "";
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public StreamedContent downloadCSV(FileEntity file) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(baos);
                CSVPrinter printer = CSVFormat.DEFAULT.withHeader(StorageHeaders.class).print(osw)) {

            for (TestEntity test : file.getTests()) {
                for (TestExecutionEntity exec : test.getExecutions()) {
                    printer.print(test.getPlatform());
                    printer.print(test.getApi());
                    printer.print(test.getService());
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
}
