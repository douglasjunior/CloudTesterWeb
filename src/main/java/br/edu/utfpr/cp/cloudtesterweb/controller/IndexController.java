package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.model.FileEntity;
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
import org.eclipse.persistence.tools.file.FileUtil;
import org.primefaces.model.UploadedFile;
import static br.edu.utfpr.cp.cloudtesterweb.util.Constants.*;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;

/**
 *
 * @author Douglas
 */
@Named(value = "indexController")
@ViewScoped
public class IndexController implements Serializable {

    @Inject
    private Dao dao;

    @Resource
    private UserTransaction tx;

    public IndexController() {
    }

    private UploadedFile file;

    private int times = 1;

    public void upload() {
        try {
            tx.begin();
            File directory = new File(UPLOADED_FOLDER);
            directory.mkdirs();
            File fileOut = new File(directory, file.getFileName());
            try (FileOutputStream fos = new FileOutputStream(fileOut);
                    InputStream is = file.getInputstream();) {
                FileUtil.copy(is, fos);
            }
            FileEntity entity = new FileEntity();
            entity.setDateTime(new Date());
            entity.setName(file.getFileName());
            entity.setContentLength(file.getSize());
            entity.setContentType(file.getContentType());
            entity.setContentPath(fileOut.getAbsolutePath());
            entity.setTestTimesConfig(times);
            dao.insert(entity);
            tx.commit();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                tx.rollback();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", file.getFileName() + " is not uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public List<FileEntity> getFiles() {
        return dao.createNamedQuerie(FileEntity.FIND_ALL, FileEntity.class).getResultList();
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public void refresh() {
        List<FileEntity> files = getFiles();
        dao.refreshAll(files);
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

}
