package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.dao.DaoStatefull;
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
import org.primefaces.model.UploadedFile;
import static br.edu.utfpr.cp.cloudtesterweb.util.Constants.*;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Douglas
 */
@Named
@ViewScoped
public class IndexController implements Serializable {

    @Inject
    private DaoStatefull dao;

    @Resource
    private UserTransaction tx;

    public IndexController() {
    }

}
