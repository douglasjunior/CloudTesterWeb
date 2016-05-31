package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.dao.DaoStatefull;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.transaction.Transactional;

/**
 *
 * @author Douglas
 */
@Named
@ViewScoped
public class IndexController implements Serializable {

    @EJB
    private DaoStatefull dao;

    private String jpql = "";

    public IndexController() {
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public void executeQuery() {
        try {
            List result = dao.createQuery(jpql).getResultList();
            System.out.println("Result: " + result);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesful", "Result count: " + result.size());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {Throwable.class})
    public void executeUpdate() {
        try {
            int updateds = dao.createQuery(jpql).executeUpdate();
            System.out.println("Updateds: " + updateds);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesful", "Updated count: " + updateds);
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public String getJpql() {
        return jpql;
    }

    public void setJpql(String jpql) {
        this.jpql = jpql;
    }

}
