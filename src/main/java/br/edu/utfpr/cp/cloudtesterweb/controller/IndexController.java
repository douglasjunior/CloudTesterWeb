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

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

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
            entity.setName(file.getFileName());
            entity.setContentLength(file.getSize());
            entity.setContentType(file.getContentType());
            entity.setContentPath(fileOut.getAbsolutePath());
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
}