package org.foi.nwtis.embalint.web.kontrole;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.ServletContextEvent;
import org.foi.nwtis.embalint.konfiguracije.Konfiguracija;

@ManagedBean
@SessionScoped
public class EmailPovezivanje {

        private static Konfiguracija konfig;
        private String server;
        private String korisnik;
        private String lozinka;

        public EmailPovezivanje(ServletContextEvent sce) {
                konfig = (Konfiguracija) sce.getServletContext().getAttribute("Konfig");
                
                server = konfig.dajPostavku("mail.server");
                korisnik = konfig.dajPostavku("mail.username");
                lozinka = konfig.dajPostavku("mail.password");
        }

        public String getServer() {
                return server;
        }

        public void setServer(String server) {
                this.server = server;
        }

        public String getKorisnik() {
                return korisnik;
        }

        public void setKorisnik(String korisnik) {
                this.korisnik = korisnik;
        }

        public String getLozinka() {
                return lozinka;
        }

        public void setLozinka(String lozinka) {
                this.lozinka = lozinka;
        }

        public String saljiPoruku() {
                return "OK";
        }

        public String citajPoruke() {
                return "OK";
        }
}

