package br.edu.utfpr.cp.cloudtesterweb.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author Douglas
 */
public abstract class Dao implements Serializable {

    public void insert(Object entity) {
        getEM().persist(entity);
    }

    public <T> TypedQuery<T> createNamedQuerie(String nameQuerie, Class<T> clazz) {
        TypedQuery<T> query = getEM().createNamedQuery(nameQuerie, clazz);
        return query;
    }

    public <T> TypedQuery<T> createNamedQuerie(String nameQuerie, Class<T> clazz, String[] params, Object[] values) {
        TypedQuery<T> query = getEM().createNamedQuery(nameQuerie, clazz);
        if (params != null && values != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(params[i], values[i]);
            }
        }
        return query;
    }

    public void update(Object entity) {
        getEM().merge(entity);
    }

    public void refreshAll(List entities) {
        for (Object entity : entities) {
            refresh(entity);
        }
    }

    private void refresh(Object entity) {
        getEM().refresh(entity);
    }

    protected abstract EntityManager getEM();

}
