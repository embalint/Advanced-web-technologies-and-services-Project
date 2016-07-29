package org.foi.nwtis.embalint.web.poruke;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.foi.nwtis.embalint.konfiguracije.Konfiguracija;
import org.foi.nwtis.embalint.web.klase.Poruka;

@ManagedBean
@RequestScoped
public class PregledPoruke {
        private Poruka poruka;
        private String sadrzaj;
        private final Konfiguracija konfig;
        
        private final String server;
        private final String korisnik;
        private final String lozinka;

        public PregledPoruke(Konfiguracija konfig) {
                this.konfig = konfig;
                server = konfig.dajPostavku("mail.server");
                korisnik = konfig.dajPostavku("mail.username");
                lozinka = konfig.dajPostavku("mail.password");
                
                poruka = PregledSvihPoruka.pr;
                sadrzaj = PregledSvihPoruka.sadrzajPoruke;
        }

        public String povratakPregledSvihPoruka() {
                return "OK";
        }

        public Poruka getPoruka() {
                return poruka;
        }

        public void setPoruka(Poruka poruka) {
                this.poruka = poruka;
        }

        public String getSadrzaj() {
                return sadrzaj;
        }

        public void setSadrzaj(String sadrzaj) {
                this.sadrzaj = sadrzaj;
        }
}
