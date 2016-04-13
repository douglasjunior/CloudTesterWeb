package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.model.FileEntity;
import java.io.File;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Douglas
 */
@Named(value = "indexController")
@ViewScoped
public class IndexController implements Serializable {

    @Inject
    private Dao dao;

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
        if (file != null) {
            FileEntity entity = new FileEntity();
            entity.setName(file.getFileName());
            entity.setContentLength(file.getSize());
            entity.setContentType(file.getContentType());
            entity.setContentPath("/caminho/arquivo.txt");
            dao.insert(entity);
            System.out.println(new File("teste.txt").getAbsolutePath());
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
