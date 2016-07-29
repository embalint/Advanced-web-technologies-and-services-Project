package org.foi.nwtis.embalint.web.poruke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.foi.nwtis.embalint.konfiguracije.Konfiguracija;
import org.foi.nwtis.embalint.web.klase.Poruka;


@ManagedBean
@SessionScoped
public class PregledSvihPoruka {

        private Konfiguracija konfig;
        private String server;
        private String korisnik;
        private String lozinka;
        private List<Poruka> poruke;
        private Map<String, String> mape;
        private String odabranaMapa = "INBOX";
        private int brojPorukaPoStranici;
        public Map<String, String> sadrzajiPoruka;

        public static Poruka pr;
        public static String sadrzajPoruke;

        public PregledSvihPoruka(Konfiguracija konfig) {
                this.konfig = konfig;
                server = konfig.dajPostavku("mail.server");
                korisnik = konfig.dajPostavku("mail.username");
                lozinka = konfig.dajPostavku("mail.password");

                try {
                        // Start the session
                        java.util.Properties properties = System.getProperties();
                        properties.put("mail.smtp.host", server);
                        Session session = Session.getInstance(properties, null);

                        // Connect to the store
                        Store store = session.getStore("imap");
                        store.connect(server, korisnik, lozinka);

                        Folder osnovnaMapa = store.getDefaultFolder();
                        Folder[] podMape = osnovnaMapa.list();

                        mape = new HashMap<>();
                        for (Folder f : podMape) {
                                mape.put(f.getName(), f.getName());
                        }

                        store.close();

                } catch (NoSuchProviderException ex) {
                        Logger.getLogger(PregledSvihPoruka.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MessagingException ex) {
                        Logger.getLogger(PregledSvihPoruka.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        /**
         * Metoda koja daje stranici koja ju poziva potrebne poruke
         *
         * @return lista poruka po zadanoj grupi i stranici
         */
        public List<Poruka> getPoruke() {
                try {
                        sadrzajiPoruka = new HashMap<>();
                        // Start the session
                        java.util.Properties properties = System.getProperties();
                        properties.put("mail.smtp.host", server);
                        Session session = Session.getInstance(properties, null);

                        // Connect to the store
                        Store store = session.getStore("imap");
                        store.connect(server, korisnik, lozinka);

                        // Open the INBOX folder
                        Folder folder = store.getFolder(odabranaMapa);
                        folder.open(Folder.READ_ONLY);

                        Message[] messages = folder.getMessages();
                        poruke = new ArrayList<>();
                        // Print messages
                        int counter = brojPorukaPoStranici;
                        for (int i = 0; i < messages.length; ++i) {
                                MimeMessage m = (MimeMessage) messages[i];
                                String tip = m.getContentType().toLowerCase();
                                Poruka p = new Poruka(m.getHeader("Message-ID")[0], m.getReceivedDate(),
                                        m.getFrom()[0].toString(), m.getSubject(), tip,
                                        m.getSize(), 0, m.getFlags(), null, false, true);
                                sadrzajiPoruka.put(p.getId(), (String) m.getContent());
                                poruke.add(p);
                                counter--;
                                if (counter <= 0) {
                                        break;
                                }
                        }
                        folder.close(true);
                        store.close();

                } catch (NoSuchProviderException ex) {
                        Logger.getLogger(PregledSvihPoruka.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MessagingException | IOException ex) {
                        Logger.getLogger(PregledSvihPoruka.class.getName()).log(Level.SEVERE, null, ex);
                }
                return poruke;
        }

        public void setPoruke(List<Poruka> poruke) {
                this.poruke = poruke;
        }

        public Map<String, String> getMape() {
                return mape;
        }

        public void setMape(Map<String, String> mape) {
                this.mape = mape;
        }

        public String getOdabranaMapa() {
                return odabranaMapa;
        }

        public void setOdabranaMapa(String odabranaMapa) {
                this.odabranaMapa = odabranaMapa;
        }

        public String odaberiMapu() {
                return "";
        }

        /**
         * Metoda koja postavlja vrijednosti varijabli pr i sadrzajPoruke koje
         * će dohvaćati klasa PrikazPoruke
         *
         * @param id ID poruke koji nam govori koju poruku proslijeđujemo klasi
         * PrikazPoruke
         * @return vraće vrijednost "OK" nakon čega će aplikacija navigirati na
         * novu stranicu
         */
        public String pregledPoruke(String id) {
                for (Poruka p : poruke) {
                        if (p.getId().equals(id)) {
                                pr = p;
                                sadrzajPoruke = sadrzajiPoruka.get(p.getId());
                                break;
                        }
                }
                System.err.println("ID poruke: " + pr.getId());
                return "OK";
        }
}
