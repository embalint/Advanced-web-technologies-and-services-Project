package org.foi.nwtis.embalint.web.aplikacija;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.embalint.ejb.eb.Dnevnik;
import org.foi.nwtis.embalint.ejb.eb.Korisnici;
import org.foi.nwtis.embalint.ejb.sb.DnevnikFacade;
import org.foi.nwtis.embalint.ejb.sb.KorisniciFacade;

@ManagedBean
@RequestScoped
public class Prijava {
        @EJB
        private KorisniciFacade korisniciFacade;
        @EJB
        private DnevnikFacade dnevnikFacade;
        
        private String logInUsername;
        private String logInPassword;

        private String registerUsername;
        private String registerPassword;
        private String registerPasswordRepeat;
        private String registerIme;
        private String registerPrezime;
        private String registerEmail;
        private int registerVrsta = 1;

        private boolean logInUserDoesNotExist = false;
        private boolean logInWrongPassword = false;
        private boolean logInInactive = false;
        private boolean logInRejected = false;
        
        private boolean registerUserAlreadyExists = false;
        private boolean registerNoPassword = false;
        private boolean registerPasswordRetypeError = false;
        private boolean registerNoUsername = false;
        private boolean registerError = false;
        private boolean registerNemaImena = false;
        private boolean registerNemaPrezimena = false;
        private boolean registerNemaMaila = false;
        private boolean registerSuccess = false;

        public Prijava() {

        }

        public Object odabirJezika() {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                session.setAttribute("povratakNaLink", "PRIJAVA");

                return "JEZIK";
        }

        public Object prijavaKorisnika() {
                long start = System.currentTimeMillis();
                logInUserDoesNotExist = false;
                logInWrongPassword = false;
                logInInactive = false;
                logInRejected = false;
                
                Dnevnik dnevnikUpdate = new Dnevnik();
                try {
                        dnevnikUpdate.setIpadresa(InetAddress.getLocalHost().toString());
                        dnevnikUpdate.setKorisnik("");
                        dnevnikUpdate.setOperacija("Prijava u sustav");
                        Date date = new Date();
                        dnevnikUpdate.setVrijeme(date);
                } catch (UnknownHostException ex) {
                        Logger.getLogger(Prijava.class.getName()).log(Level.SEVERE, null, ex);
                }

                List<Korisnici> korisnici = korisniciFacade.findAll();
                boolean pronadjen = false;
                boolean valjanaLozinka = false;
                boolean aktiviran = false;
                for (Korisnici k : korisnici) {
                        if (k.getKorIme().equals(logInUsername)) {
                                pronadjen = true;
                                if (k.getLozinka().equals(logInPassword)) {
                                        valjanaLozinka = true;

                                        FacesContext context = FacesContext.getCurrentInstance();
                                        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                                        session.setAttribute("korisnik", k);
                                        
                                        if(k.getAktiviran() == 1) {
                                                aktiviran = true;
                                        } else if (k.getAktiviran() == 0 ) {
                                                aktiviran = false;
                                                logInInactive = true;
                                        } else if(k.getAktiviran() == -1) {
                                                aktiviran = false;
                                                logInRejected = true;
                                        }
                                }
                                break;
                        }
                }

                if (!pronadjen) {
                        logInUserDoesNotExist = true;
                        dnevnikUpdate.setRezultat("ERR: Korisnik " + logInUsername + " ne postoji u bazi");
                        long end = System.currentTimeMillis();
                        dnevnikUpdate.setTrajanje((int) (end - start));
                        dnevnikFacade.create(dnevnikUpdate);

                        return "";
                }

                if (!valjanaLozinka) {
                        logInWrongPassword = true;
                        dnevnikUpdate.setKorisnik(logInUsername);
                        dnevnikUpdate.setRezultat("ERR: Nevaljana lozinka");
                        long end = System.currentTimeMillis();
                        dnevnikUpdate.setTrajanje((int) (end - start));
                        dnevnikFacade.create(dnevnikUpdate);

                        return "";
                }
                
                if(!aktiviran) {
                        dnevnikUpdate.setKorisnik(logInUsername);
                        dnevnikUpdate.setRezultat("ERR: Korisnik nije aktiviran ili je odbijen");
                        long end = System.currentTimeMillis();
                        dnevnikUpdate.setTrajanje((int) (end - start));
                        dnevnikFacade.create(dnevnikUpdate);
                        
                        return "";
                }

                dnevnikUpdate.setKorisnik(logInUsername);
                dnevnikUpdate.setRezultat("OK: Uspješna prijava u sustav");
                long end = System.currentTimeMillis();
                dnevnikUpdate.setTrajanje((int) (end - start));
                dnevnikFacade.create(dnevnikUpdate);

                return "OK";
        }

        public Object registracijaKorisnika() {
                long start = System.currentTimeMillis();
                registerSuccess = false;

                Dnevnik dnevnikUpdate = new Dnevnik();
                try {
                        dnevnikUpdate.setIpadresa(InetAddress.getLocalHost().toString());
                        dnevnikUpdate.setKorisnik("");
                        dnevnikUpdate.setOperacija("Registracija novog korisnika");
                        Date date = new Date();
                        dnevnikUpdate.setVrijeme(date);
                } catch (UnknownHostException ex) {
                        Logger.getLogger(Prijava.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (registerUsername.isEmpty()) {
                        registerNoUsername = true;
                        return "";
                }

                if (registerPassword.isEmpty()) {
                        registerNoPassword = true;
                        return "";
                }

                if (!(registerPassword.equals(registerPasswordRepeat))) {
                        registerPasswordRetypeError = true;
                        return "";
                }

                if (registerIme.isEmpty()) {
                        registerNemaImena = true;
                        return "";
                }

                if (registerPrezime.isEmpty()) {
                        registerNemaPrezimena = true;
                        return "";
                }

                if (registerEmail.isEmpty()) {
                        registerNemaMaila = true;
                        return "";
                }

                List<Korisnici> korisnici = korisniciFacade.findAll();
                boolean pronadjen = false;
                for (Korisnici k : korisnici) {
                        if (k.getKorIme().equals(registerUsername)) {
                                pronadjen = true;
                                break;
                        }
                }

                if (pronadjen) {
                        registerUserAlreadyExists = true;
                        dnevnikUpdate.setRezultat("ERR: Korisnik " + registerUsername + " već postoji");
                        long end = System.currentTimeMillis();
                        dnevnikUpdate.setTrajanje((int) (end - start));
                        dnevnikFacade.create(dnevnikUpdate);

                        return "";
                }

                try {
                        Korisnici noviKorisnik = new Korisnici();
                        noviKorisnik.setKorIme(registerUsername);
                        noviKorisnik.setLozinka(registerPassword);
                        noviKorisnik.setVrsta(registerVrsta);
                        noviKorisnik.setKategorija(1);
                        noviKorisnik.setAktiviran((short) 0);
                        noviKorisnik.setIme(registerIme);
                        noviKorisnik.setPrezime(registerPrezime);
                        noviKorisnik.setEmailAdresa(registerEmail);
                        
                        korisniciFacade.create(noviKorisnik);
                        registerSuccess = true;
                } catch (Exception ex) {
                        registerError = true;
                        dnevnikUpdate.setRezultat("ERR: Nepoznata pogreška");
                        long end = System.currentTimeMillis();
                        dnevnikUpdate.setTrajanje((int) (end - start));
                        dnevnikFacade.create(dnevnikUpdate);

                        return "";
                }

                dnevnikUpdate.setKorisnik(registerUsername);
                dnevnikUpdate.setRezultat("OK: Uspješna registracija novog korisnika");
                long end = System.currentTimeMillis();
                dnevnikUpdate.setTrajanje((int) (end - start));
                dnevnikFacade.create(dnevnikUpdate);

                return "";
        }

        /*Getteri i Setteri*/
        public String getLogInUsername() {
                return logInUsername;
        }

        public void setLogInUsername(String logInUsername) {
                this.logInUsername = logInUsername;
        }

        public String getLogInPassword() {
                return logInPassword;
        }

        public void setLogInPassword(String logInPassword) {
                this.logInPassword = logInPassword;
        }

        public String getRegisterUsername() {
                return registerUsername;
        }

        public void setRegisterUsername(String registerUsername) {
                this.registerUsername = registerUsername;
        }

        public String getRegisterPassword() {
                return registerPassword;
        }

        public void setRegisterPassword(String registerPassword) {
                this.registerPassword = registerPassword;
        }

        public String getRegisterPasswordRepeat() {
                return registerPasswordRepeat;
        }

        public void setRegisterPasswordRepeat(String registerPasswordRepeat) {
                this.registerPasswordRepeat = registerPasswordRepeat;
        }

        public boolean isLogInUserDoesNotExist() {
                return logInUserDoesNotExist;
        }

        public void setLogInUserDoesNotExist(boolean logInUserDoesNotExist) {
                this.logInUserDoesNotExist = logInUserDoesNotExist;
        }

        public boolean isRegisterUserAlreadyExists() {
                return registerUserAlreadyExists;
        }

        public void setRegisterUserAlreadyExists(boolean registerUserAlreadyExists) {
                this.registerUserAlreadyExists = registerUserAlreadyExists;
        }

        public boolean isRegisterNoPassword() {
                return registerNoPassword;
        }

        public void setRegisterNoPassword(boolean registerNoPassword) {
                this.registerNoPassword = registerNoPassword;
        }

        public boolean isRegisterPasswordRetypeError() {
                return registerPasswordRetypeError;
        }

        public void setRegisterPasswordRetypeError(boolean registerPasswordRetypeError) {
                this.registerPasswordRetypeError = registerPasswordRetypeError;
        }

        public boolean isRegisterNoUsername() {
                return registerNoUsername;
        }

        public void setRegisterNoUsername(boolean registerNoUsername) {
                this.registerNoUsername = registerNoUsername;
        }

        public boolean isRegisterError() {
                return registerError;
        }

        public void setRegisterError(boolean registerError) {
                this.registerError = registerError;
        }

        public boolean isLogInWrongPassword() {
                return logInWrongPassword;
        }

        public void setLogInWrongPassword(boolean logInWrongPassword) {
                this.logInWrongPassword = logInWrongPassword;
        }

        public String getRegisterIme() {
                return registerIme;
        }

        public void setRegisterIme(String registerIme) {
                this.registerIme = registerIme;
        }

        public String getRegisterPrezime() {
                return registerPrezime;
        }

        public void setRegisterPrezime(String registerPrezime) {
                this.registerPrezime = registerPrezime;
        }

        public String getRegisterEmail() {
                return registerEmail;
        }

        public void setRegisterEmail(String registerEmail) {
                this.registerEmail = registerEmail;
        }

        public boolean isRegisterNemaImena() {
                return registerNemaImena;
        }

        public boolean isRegisterNemaPrezimena() {
                return registerNemaPrezimena;
        }

        public boolean isRegisterNemaMaila() {
                return registerNemaMaila;
        }

        public int getRegisterVrsta() {
                return registerVrsta;
        }

        public void setRegisterVrsta(int registerVrsta) {
                this.registerVrsta = registerVrsta;
        }       

        public boolean isRegisterSuccess() {
                return registerSuccess;
        }

        public void setRegisterSuccess(boolean registerSuccess) {
                this.registerSuccess = registerSuccess;
        }      

        public boolean isLogInInactive() {
                return logInInactive;
        }

        public void setLogInInactive(boolean logInInactive) {
                this.logInInactive = logInInactive;
        }

        public boolean isLogInRejected() {
                return logInRejected;
        }

        public void setLogInRejected(boolean logInRejected) {
                this.logInRejected = logInRejected;
        }
}
