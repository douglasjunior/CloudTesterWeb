package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.model.FileEntity;
import java.io.Serializable;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Douglas
 */
@Stateless
public class Dao implements Serializable {

    @PersistenceContext(unitName = "pu")
    private EntityManager entityManager;

    public void insert(Object entity) {
        entityManager.persist(entity);
    }

}
