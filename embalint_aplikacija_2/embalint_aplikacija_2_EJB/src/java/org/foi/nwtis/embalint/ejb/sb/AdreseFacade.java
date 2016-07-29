/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.ejb.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.embalint.ejb.eb.Adrese;

/**
 *
 * @author lugerovac
 */
@Stateless
public class AdreseFacade extends AbstractFacade<Adrese> {
        @PersistenceContext(unitName = "embalint_aplikacija_2_EJBPU")
        private EntityManager em;

        @Override
        protected EntityManager getEntityManager() {
                return em;
        }

        public AdreseFacade() {
                super(Adrese.class);
        }
        
}
