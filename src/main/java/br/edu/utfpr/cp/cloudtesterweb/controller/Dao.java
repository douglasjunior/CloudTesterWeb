package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.model.FileEntity;
import java.io.Serializable;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Douglas
 */
@Stateless
public class Dao implements Serializable {

    @PersistenceContext(unitName = "pu")
    private EntityManager em;

    public void insert(Object entity) {
        em.persist(entity);
    }

    public <T> TypedQuery<T> createNamedQuerie(String nameQuerie, Class<T> clazz) {
        TypedQuery<T> query = em.createNamedQuery(nameQuerie, clazz);
        return query;
    }

    public <T> TypedQuery<T> createNamedQuerie(String nameQuerie, Class<T> clazz, String[] params, Object[] values) {
        TypedQuery<T> query = em.createNamedQuery(nameQuerie, clazz);
        if (params != null && values != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(params[i], values[i]);
            }
        }
        return query;
    }

    public void update(FileEntity file) {
        em.merge(file);
    }

}
