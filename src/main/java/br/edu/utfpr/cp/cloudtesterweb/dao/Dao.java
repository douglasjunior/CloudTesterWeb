package br.edu.utfpr.cp.cloudtesterweb.dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author Douglas
 */
abstract class Dao implements Serializable {

    public void insert(Object entity) {
        getEM().persist(entity);
    }

    public <T> TypedQuery<T> createNamedQuery(String nameQuerie, Class<T> clazz) {
        TypedQuery<T> query = getEM().createNamedQuery(nameQuerie, clazz);
        return query;
    }

    public Query createNamedQuery(String nameQuerie, String[] params, Object[] values) {
        Query query = getEM().createNamedQuery(nameQuerie);
        applyParameters(query, params, values);
        return query;
    }

    public <T> TypedQuery<T> createNamedQuery(String nameQuerie, Class<T> clazz, String[] params, Object[] values) {
        TypedQuery<T> query = getEM().createNamedQuery(nameQuerie, clazz);
        applyParameters(query, params, values);
        return query;
    }

    public Query createQuery(String jqpl, String[] params, Object[] values) {
        Query query = getEM().createQuery(jqpl);
        applyParameters(query, params, values);
        return query;
    }

    public Query createQuery(String jqpl) {
        return createQuery(jqpl, null, null);
    }

    public void update(Object entity) {
        getEM().merge(entity);
    }

    public void refreshAll(List entities) {
        for (Object entity : entities) {
            refresh(entity);
        }
    }

    public void refresh(Object entity) {
        getEM().refresh(entity);
    }

    public void delete(Object entity) {
        getEM().remove(entity);
    }

    public void flush() {
        getEM().flush();
    }

    protected abstract EntityManager getEM();

    private void applyParameters(Query query, String[] params, Object[] values) {
        if (params != null && values != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(params[i], values[i]);
            }
        }
    }

    public <T> T findById(Class<T> clazz, Object id) {
        return getEM().find(clazz, id);
    }
}
