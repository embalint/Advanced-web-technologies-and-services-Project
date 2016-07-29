package org.foi.nwtis.embalint.web.aplikacija;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.embalint.ejb.eb.Adrese;
import org.foi.nwtis.embalint.ejb.eb.Dnevnik;
import org.foi.nwtis.embalint.ejb.eb.Korisnici;
import org.foi.nwtis.embalint.ejb.eb.Meteopodaci;
import org.foi.nwtis.embalint.ejb.sb.AdreseFacade;
import org.foi.nwtis.embalint.ejb.sb.DnevnikFacade;
import org.foi.nwtis.embalint.ejb.sb.KorisniciFacade;
import org.foi.nwtis.embalint.ejb.sb.MeteopodaciFacade;
import org.foi.nwtis.embalint.web.klase.Lokacija;
import org.foi.nwtis.embalint.web.prognoze.GMKlijent;
import org.foi.nwtis.embalint.web.slusaci.SlusacAplikacije;

@ManagedBean
@SessionScoped
public class Aplikacija {

        @EJB
        private MeteopodaciFacade meteopodaciFacade;
        @EJB
        private KorisniciFacade korisniciFacade;
        @EJB
        private DnevnikFacade dnevnikFacade;
        @EJB
        private AdreseFacade adreseFacade;
        /*    
        @Resource(mappedName = "jms/nwtis_embalint_2")
        private Queue nwtis_embalint_2;
        @Inject
        @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
        private JMSContext context;
        */
        private List<Korisnici> korisnici = new ArrayList<>();
        private boolean korisnikNijeUcitan = true;
        private boolean lozinkaNijeUcitana = true;
        private boolean dnevnikNijeUcitan = true;

        public Aplikacija() {
        }

        /*Upravljanje korisnicima*/
        private List<Korisnici> neaktiviraniKorisnici = new ArrayList<>();
        private boolean nemaNeaktivnih = false;
        private Korisnici kategorijaKorisnik = new Korisnici();
        private boolean kategorijaAktivna = false;
        private boolean kategorijaUpActive = true;
        private boolean kategorijaDownActive = true;
        private String kategorijaUsername = "";
        private boolean kategorijaAlert = false;

        public void pronadjiKorisnika() {
                setKategorijaAktivna(false);
                setKategorijaAlert(false);

                for (Korisnici kor : korisniciFacade.findAll()) {
                        if (kor.getAktiviran() == 1 && kor.getKorIme().equals(getKategorijaUsername())) {
                                setKategorijaAktivna(true);
                                setKategorijaKorisnik(kor);

                                if (kor.getKategorija() == 1) {
                                        setKategorijaDownActive(false);
                                } else {
                                        setKategorijaDownActive(true);
                                }

                                if (kor.getKategorija() == 5) {
                                        setKategorijaUpActive(false);
                                } else {
                                        setKategorijaUpActive(true);
                                }

                                break;
                        }
                }
        }

        public void categoryUp() {
                try {
                        FacesContext context = FacesContext.getCurrentInstance();
                        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                        Korisnici kor = (Korisnici) session.getAttribute("korisnik");

                        kategorijaKorisnik.setKategorija(kategorijaKorisnik.getKategorija() + 1);
                        korisniciFacade.edit(kategorijaKorisnik);
                        
                        String socketOdgovor = "UP " + kategorijaKorisnik.getKorIme() + ";";
                        //TODO pošalji poruku preko socketa
                        
                        setKategorijaAlert(true);
                        setKategorijaAktivna(false);

                        Dnevnik dnevnikUpdate = new Dnevnik();
                        dnevnikUpdate.setIpadresa(InetAddress.getLocalHost().toString());
                        dnevnikUpdate.setKorisnik(kor.getKorIme());
                        dnevnikUpdate.setOperacija("Povećana kategorija korisnika  " + kategorijaKorisnik.getKorIme());
                        Date date = new Date();
                        dnevnikUpdate.setVrijeme(date);
                        dnevnikUpdate.setRezultat("Kategorija korisnika povećana");
                        dnevnikUpdate.setTrajanje(1);
                        dnevnikFacade.create(dnevnikUpdate);
                } catch (UnknownHostException ex) {
                        Logger.getLogger(Aplikacija.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        public void categoryDown() {
                try {
                        FacesContext context = FacesContext.getCurrentInstance();
                        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                        Korisnici kor = (Korisnici) session.getAttribute("korisnik");

                        kategorijaKorisnik.setKategorija(kategorijaKorisnik.getKategorija() - 1);
                        korisniciFacade.edit(kategorijaKorisnik);
                        
                        String socketOdgovor = "DOWN " + kategorijaKorisnik.getKorIme() + ";";
                        //TODO pošalji poruku preko socketa

                        setKategorijaAlert(true);
                        setKategorijaAktivna(false);
                        
                        Dnevnik dnevnikUpdate = new Dnevnik();
                        dnevnikUpdate.setIpadresa(InetAddress.getLocalHost().toString());
                        dnevnikUpdate.setKorisnik(kor.getKorIme());
                        dnevnikUpdate.setOperacija("Smanjena kategorija korisnika  " + kategorijaKorisnik.getKorIme());
                        Date date = new Date();
                        dnevnikUpdate.setVrijeme(date);
                        dnevnikUpdate.setRezultat("Kategorija korisnika smanjena");
                        dnevnikUpdate.setTrajanje(1);
                        dnevnikFacade.create(dnevnikUpdate);
                } catch (UnknownHostException ex) {
                        Logger.getLogger(Aplikacija.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        public boolean isKategorijaAlert() {
                return kategorijaAlert;
        }

        public void setKategorijaAlert(boolean kategorijaAlert) {
                this.kategorijaAlert = kategorijaAlert;
        }

        public String getKategorijaUsername() {
                return kategorijaUsername;
        }

        public void setKategorijaUsername(String kategorijaUsername) {
                this.kategorijaUsername = kategorijaUsername;
        }

        public boolean isKategorijaUpActive() {
                return kategorijaUpActive;
        }

        public void setKategorijaUpActive(boolean kategorijaUpActive) {
                this.kategorijaUpActive = kategorijaUpActive;
        }

        public boolean isKategorijaDownActive() {
                return kategorijaDownActive;
        }

        public void setKategorijaDownActive(boolean kategorijaDownActive) {
                this.kategorijaDownActive = kategorijaDownActive;
        }

        public Korisnici getKategorijaKorisnik() {
                return kategorijaKorisnik;
        }

        public void setKategorijaKorisnik(Korisnici kategorijaKorisnik) {
                this.kategorijaKorisnik = kategorijaKorisnik;
        }

        public boolean isKategorijaAktivna() {
                return kategorijaAktivna;
        }

        public void setKategorijaAktivna(boolean kategorijaAktivna) {
                this.kategorijaAktivna = kategorijaAktivna;
        }

        public void odbijKorisnika(Korisnici korisnik) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                Korisnici kor = (Korisnici) session.getAttribute("korisnik");

                korisnik.setAktiviran((short) -1);
                korisniciFacade.edit(korisnik);

                String socketOdgovor = "ADD " + korisnik.getKorIme() + "; PASSWD " + korisnik.getLozinka() + "; ROLE ";
                if (korisnik.getVrsta() == 1) {
                        socketOdgovor += "USER;";
                } else {
                        socketOdgovor += "ADMIN;";
                }
                //TODO pošalji poruku preko Socketa

                Dnevnik dnevnikUpdate = new Dnevnik();
                try {
                        dnevnikUpdate.setIpadresa(InetAddress.getLocalHost().toString());
                        dnevnikUpdate.setKorisnik(kor.getKorIme());
                        dnevnikUpdate.setOperacija("Odbijena registracija korisnika " + korisnik.getKorIme());
                        Date date = new Date();
                        dnevnikUpdate.setVrijeme(date);
                        dnevnikUpdate.setRezultat("OK: Korisnik odbijen");
                        dnevnikUpdate.setTrajanje(1);
                        dnevnikFacade.create(dnevnikUpdate);
                } catch (UnknownHostException ex) {
                        Logger.getLogger(Aplikacija.class.getName()).log(Level.SEVERE, null, ex);
                }

                getNeaktiviraniKorisnici();
        }

        public void aktivirajKorisnika(Korisnici korisnik) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                Korisnici kor = (Korisnici) session.getAttribute("korisnik");

                korisnik.setAktiviran((short) 1);
                korisniciFacade.edit(korisnik);

                Dnevnik dnevnikUpdate = new Dnevnik();
                try {
                        dnevnikUpdate.setIpadresa(InetAddress.getLocalHost().toString());
                        dnevnikUpdate.setKorisnik(kor.getKorIme());
                        dnevnikUpdate.setOperacija("Prihvaćena registracija korisnika " + korisnik.getKorIme());
                        Date date = new Date();
                        dnevnikUpdate.setVrijeme(date);
                        dnevnikUpdate.setRezultat("OK: Korisnik prihvaćen");
                        dnevnikUpdate.setTrajanje(1);
                        dnevnikFacade.create(dnevnikUpdate);
                } catch (UnknownHostException ex) {
                        Logger.getLogger(Aplikacija.class.getName()).log(Level.SEVERE, null, ex);
                }

                //TODO: pošalji poruku preko socketa
                getNeaktiviraniKorisnici();
        }

        public List<Korisnici> getNeaktiviraniKorisnici() {
                nemaNeaktivnih = true;
                neaktiviraniKorisnici.clear();
                for (Korisnici k : korisniciFacade.findAll()) {
                        if (k.getAktiviran() == 0) {
                                neaktiviraniKorisnici.add(k);
                                nemaNeaktivnih = false;
                        }
                }
                return neaktiviraniKorisnici;
        }

        public void setNeaktiviraniKorisnici(List<Korisnici> neaktiviraniKorisnici) {
                this.neaktiviraniKorisnici = neaktiviraniKorisnici;
        }

        public boolean isNemaNeaktivnih() {
                return nemaNeaktivnih;
        }

        public void setNemaNeaktivnih(boolean nemaNeaktivnih) {
                this.nemaNeaktivnih = nemaNeaktivnih;
        }

        /*OWM klijent - pregled podataka*/
        private boolean owmPrikaz = false;
        private List<Meteopodaci> meteoPodaci = new ArrayList<>();

        public Object prikaziMeteoPodatke() {
                owmPrikaz = true;
                return "";
        }

        /*OWM klijent - dodavanje i odabiranje adresa*/
        private boolean showMeteoPrikaz = false;
        private List<String> adrese = new ArrayList<>();
        private List<String> odabraneAdrese = new ArrayList<>();
        private String adresaZaDodati;
        private String adresaZaUkloniti;
        private String novaAdresa;
        private boolean errorAdresaVecPostoji;
        private boolean adreseDohvacene = false;

        public Object prikaziMeteoKontrole() {
                showMeteoPrikaz = true;
                return "";
        }

        public Object sakrijMeteoKontrole() {
                showMeteoPrikaz = false;
                return "";
        }

        public Object stvoriAdresu() {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                Korisnici kor = (Korisnici) session.getAttribute("korisnik");

                long start = System.currentTimeMillis();
                Dnevnik dnevnikUpdate = new Dnevnik();

                try {
                        dnevnikUpdate.setIpadresa(InetAddress.getLocalHost().toString());
                        dnevnikUpdate.setKorisnik(kor.getKorIme());
                        dnevnikUpdate.setOperacija("Dodavanje nove adrese");
                        Date date = new Date();
                        dnevnikUpdate.setVrijeme(date);
                } catch (UnknownHostException ex) {
                        Logger.getLogger(Aplikacija.class.getName()).log(Level.SEVERE, null, ex);
                }

                for (Adrese a : dajSveAdrese()) {
                        if (a.getIzbrisana() == 1 && a.getAdresa().equals(novaAdresa)) {
                                dnevnikUpdate.setRezultat("ERR: adresa već postoji");
                                long end = System.currentTimeMillis();
                                dnevnikUpdate.setTrajanje((int) (end - start));
                                dnevnikFacade.create(dnevnikUpdate);

                                errorAdresaVecPostoji = true;
                                return "";
                        }
                }

                Adrese nova = new Adrese();
                nova.setAdresa(novaAdresa);
                nova.setIzbrisana((short) 0);
                nova.setKorisnik(kor.getKorIme());

                GMKlijent gmk = new GMKlijent();
                Lokacija l = gmk.getGeoLocation(novaAdresa);

                nova.setLatitude(l.getLatitude());
                nova.setLongitude(l.getLongitude());

                for (Adrese a : dajSveAdrese()) {
                        if (a.getAdresa().equals(novaAdresa)) {
                                urediAdresu(nova);
                        } else {
                                stvoriAdresu(nova);
                        }
                }

                adrese.add(novaAdresa);
                novaAdresa = "";
                errorAdresaVecPostoji = false;

                //sendJMSMessageToNwtis_embalint_2(nova.getAdresa());

                dnevnikUpdate.setRezultat("OK: Adresa je dodana u bazu podataka");
                long end = System.currentTimeMillis();
                dnevnikUpdate.setTrajanje((int) (end - start));
                dnevnikFacade.create(dnevnikUpdate);

                return "";
        }

        private List<Adrese> dajSveAdrese() {
                return adreseFacade.findAll();
        }

        private void stvoriAdresu(Adrese adresa) {
                adreseFacade.create(adresa);
        }

        private void urediAdresu(Adrese adresa) {
                adreseFacade.edit(adresa);
        }

        public Object dodajSveAdrese() {
                for (String a : adrese) {
                        odabraneAdrese.add(a);
                }
                adrese.clear();
                return "";
        }

        public Object dodajAdresu() {
                adrese.remove(adresaZaDodati);
                odabraneAdrese.add(adresaZaDodati);
                return "";
        }

        public Object ukloniAdresu() {
                odabraneAdrese.remove(adresaZaUkloniti);
                adrese.add(adresaZaUkloniti);
                return "";
        }

        public Object ukloniSveAdrese() {
                for (String a : odabraneAdrese) {
                        adrese.add(a);
                }
                odabraneAdrese.clear();
                return "";
        }

        /*Odabir jezika*/
        public Object odabirJezika() {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                session.setAttribute("povratakNaLink", "APP");
                return "JEZIK";
        }

        /*Dnevničke kontrole*/
        private boolean showDnevnik = true;
        private List<Dnevnik> dnevnickiZapisi = new ArrayList<>();
        private String filterKorisnik;
        private String filterIP;
        private String filterOperacija;
        private String filterRezultat;

        private int stranica;
        private final int stranicenje = Integer.parseInt(SlusacAplikacije.konfig.dajPostavku("paging"));
        private boolean zadrziStranicu = false;
        private String zadanaStranica;

        public Object prikaziDnevnik() {
                showDnevnik = true;
                return "";
        }

        public Object sakrijDnevnik() {
                showDnevnik = false;
                return "";
        }

        public Object idiNaStranicu() {
                int idiNaStr = Integer.parseInt(zadanaStranica) * stranicenje - 1;
                if (idiNaStr < 0 || idiNaStr > dnevnikFacade.count()) {
                        zadanaStranica = "";
                        return "";
                }
                stranica = idiNaStr;
                zadrziStranicu = true;
                return filtriraj();
        }

        public Object straniciKraj() {
                stranica = dnevnikFacade.count() - stranicenje;
                zadrziStranicu = true;
                return filtriraj();

        }

        public Object straniciPocetak() {
                stranica = 0;
                zadrziStranicu = true;
                return filtriraj();
        }

        public Object straniciLijevo() {
                stranica -= stranicenje;
                if (stranica < 0) {
                        stranica = 0;
                }
                zadrziStranicu = true;
                return filtriraj();
        }

        public Object straniciDesno() {
                int temp = stranica;
                stranica += stranicenje;
                if (stranica > dnevnikFacade.count()) {
                        stranica = temp;
                }
                zadrziStranicu = true;
                return filtriraj();
        }

        public Object filtriraj() {
                if (!zadrziStranicu) {
                        stranica = 0;
                }
                zadrziStranicu = false;

                dnevnickiZapisi.clear();
                int pageCounter = 0;
                for (Dnevnik zapis : dnevnikFacade.findAll()) {
                        boolean dodaj = true;
                        if (!filterKorisnik.isEmpty()) {
                                if (!zapis.getKorisnik().equals(filterKorisnik)) {
                                        dodaj = false;
                                }
                        }

                        if (!filterIP.isEmpty()) {
                                if (!zapis.getIpadresa().equals(filterIP)) {
                                        dodaj = false;
                                }
                        }

                        if (!filterOperacija.isEmpty()) {
                                if (!zapis.getOperacija().equals(filterOperacija)) {
                                        dodaj = false;
                                }
                        }

                        if (!filterRezultat.isEmpty()) {
                                if (!zapis.getRezultat().equals(filterRezultat)) {
                                        dodaj = false;
                                }
                        }

                        if (dodaj && pageCounter < stranica) {
                                dodaj = false;
                                pageCounter++;
                        }

                        if (dodaj) {
                                dnevnickiZapisi.add(zapis);
                                pageCounter++;
                        }

                        if (dnevnickiZapisi.size() >= stranicenje) {
                                break;
                        }
                }

                return "";
        }

        /*Prikaz Admin. opcija*/
        private boolean showAdmin = false;

        public boolean isShowAdmin() {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                Korisnici k = (Korisnici) session.getAttribute("korisnik");

                if (k.getVrsta() == 2) {
                        showAdmin = true;
                } else {
                        showAdmin = false;
                }

                return showAdmin;
        }

        public void setShowAdmin(boolean showAdmin) {
                this.showAdmin = showAdmin;
        }

        public List<Korisnici> getKorisnici() {
                korisnici = korisniciFacade.findAll();

                return korisnici;
        }

        public int getStranica() {
                Double rezultat = (double) stranica / (double) stranicenje;
                return rezultat.intValue() + 1;
        }

        public void setStranica(int stranica) {
                this.stranica = stranica;
        }

        public String getZadanaStranica() {
                return zadanaStranica;
        }

        public void setZadanaStranica(String zadanaStranica) {
                this.zadanaStranica = zadanaStranica;
        }

        public List<Dnevnik> getDnevnickiZapisi() {
                if (dnevnikNijeUcitan) {
                        int velicinaStranice = Integer.parseInt(SlusacAplikacije.konfig.dajPostavku("paging"));
                        for (Dnevnik zapis : dnevnikFacade.findAll()) {
                                dnevnickiZapisi.add(zapis);
                                velicinaStranice--;
                                if (velicinaStranice == 0) {
                                        break;
                                }
                        }
                        dnevnikNijeUcitan = false;

                        return dnevnickiZapisi;
                }

                return dnevnickiZapisi;
        }

        public void setDnevnickiZapisi(List<Dnevnik> dnevnickiZapisi) {
                this.dnevnickiZapisi = dnevnickiZapisi;
        }

        public String getFilterKorisnik() {
                return filterKorisnik;
        }

        public void setFilterKorisnik(String filterKorisnik) {
                this.filterKorisnik = filterKorisnik;
        }

        public String getFilterIP() {
                return filterIP;
        }

        public void setFilterIP(String filterIP) {
                this.filterIP = filterIP;
        }

        public String getFilterOperacija() {
                return filterOperacija;
        }

        public void setFilterOperacija(String filterOperacija) {
                this.filterOperacija = filterOperacija;
        }

        public String getFilterRezultat() {
                return filterRezultat;
        }

        public void setFilterRezultat(String filterRezultat) {
                this.filterRezultat = filterRezultat;
        }

        public boolean isShowMeteoPrikaz() {
                return showMeteoPrikaz;
        }

        public void setShowMeteoPrikaz(boolean showMeteoPrikaz) {
                this.showMeteoPrikaz = showMeteoPrikaz;
        }

        public List<String> getAdrese() {
                if (adreseDohvacene) {
                        return adrese;
                }

                adrese.clear();
                for (Adrese a : dajSveAdrese()) {
                        if (a.getIzbrisana() == 1 && !adrese.contains(a.getAdresa())) {
                                adrese.add(a.getAdresa());
                        }
                }

                adreseDohvacene = true;
                return adrese;
        }

        public void setAdrese(List<String> adrese) {
                this.adrese = adrese;
        }

        public String getAdresaZaDodati() {
                return adresaZaDodati;
        }

        public void setAdresaZaDodati(String adresaZaDodati) {
                this.adresaZaDodati = adresaZaDodati;
        }

        public List<String> getOdabraneAdrese() {
                return odabraneAdrese;
        }

        public void setOdabraneAdrese(List<String> odabraneAdrese) {
                this.odabraneAdrese = odabraneAdrese;
        }

        public String getAdresaZaUkloniti() {
                return adresaZaUkloniti;
        }

        public void setAdresaZaUkloniti(String adresaZaUkloniti) {
                this.adresaZaUkloniti = adresaZaUkloniti;
        }

        public String getNovaAdresa() {
                return novaAdresa;
        }

        public void setNovaAdresa(String novaAdresa) {
                this.novaAdresa = novaAdresa;
        }

        public boolean isErrorAdresaVecPostoji() {
                return errorAdresaVecPostoji;
        }

        public void setErrorAdresaVecPostoji(boolean errorAdresaVecPostoji) {
                this.errorAdresaVecPostoji = errorAdresaVecPostoji;
        }

        public boolean isOwmPrikaz() {
                return owmPrikaz;
        }

        public void setOwmPrikaz(boolean owmPrikaz) {
                this.owmPrikaz = owmPrikaz;
        }

        public List<Meteopodaci> getMeteoPodaci() {
                meteoPodaci.clear();
                for (String adresa : odabraneAdrese) {
                        meteoPodaci.add(dajMeteoPodatkePoAdresi(adresa));
                }

                return meteoPodaci;
        }

        private Meteopodaci dajMeteoPodatkePoAdresi(String adresa) {
                for (Meteopodaci mp : meteopodaciFacade.findAll()) {
                        if (mp.getAddress().equals(adresa)) {
                                return mp;
                        }
                }
                return null;
        }

        public void setMeteoPodaci(List<Meteopodaci> meteoPodaci) {
                this.meteoPodaci = meteoPodaci;
        }
/*
        private void sendJMSMessageToNwtis_embalint_2(String messageData) {
                context.createProducer().send(nwtis_embalint_2, messageData);
        }
*/
}
