package br.edu.utfpr.cp.cloudtesterweb.controller;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author douglas
 */
@Stateless
public class DaoStateless extends Dao {

    @PersistenceContext(unitName = "pu", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Override
    protected EntityManager getEM() {
        return em;
    }

}
