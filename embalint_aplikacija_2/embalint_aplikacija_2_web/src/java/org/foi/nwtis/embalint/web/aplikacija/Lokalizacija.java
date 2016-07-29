/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.web.aplikacija;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class Lokalizacija {
        private static final Map<String, Object> jezici;
        private String odabraniJezik;
        private Locale vazecaLokalizacija;

        static {
                jezici = new HashMap<>();
                jezici.put("Hrvatski", new Locale("hr"));
                jezici.put("English", Locale.ENGLISH);
        }

        /**
         * Creates a new instance of Lokalizacija
         */
        public Lokalizacija() {
        }

        public String getOdabraniJezik() {
                return odabraniJezik;
        }

        public void setOdabraniJezik(String odabraniJezik) {
                this.odabraniJezik = odabraniJezik;
        }

        public Locale getVazecaLokalizacija() {
                return vazecaLokalizacija;
        }

        public void setVazecaLokalizacija(Locale vazecaLokalizacija) {
                this.vazecaLokalizacija = vazecaLokalizacija;
        }

        public Map<String, Object> getJezici() {
                return jezici;
        }

        public Object odaberiJezik() {
                Iterator i = jezici.keySet().iterator();
                while (i.hasNext()) {
                        String kljuc = (String) i.next();
                        Locale l = (Locale) jezici.get(kljuc);
                        if (odabraniJezik.equals(l.getLanguage())) {
                                FacesContext.getCurrentInstance().getViewRoot().setLocale(l);
                                vazecaLokalizacija = l;
                                return "OK";
                        }

                }
                return "ERROR";
        }
}
