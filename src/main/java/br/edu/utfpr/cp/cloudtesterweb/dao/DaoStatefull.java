package br.edu.utfpr.cp.cloudtesterweb.dao;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 * To solve LazyInitializationException.
 *
 * @author douglas
 */
@Stateful
public class DaoStatefull extends Dao {

    @PersistenceContext(unitName = "pu", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Override
    protected EntityManager getEM() {
        return em;
    }

}
