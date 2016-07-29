package org.foi.nwtis.embalint.web.poruke;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import org.foi.nwtis.embalint.konfiguracije.Konfiguracija;
import org.foi.nwtis.embalint.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.embalint.web.klase.JMSPoruka;

public class PreglednikPoruka extends Thread {

        private static BP_Konfiguracija bpk;
        private static Konfiguracija konfig;
        private static int interval;
        private static boolean zaustavi = false;
        private final String server;
        private final String korisnik;
        private final String lozinka;
        private int ukupanBrojPoruka = 0;
        private int ukupanBrojNwtisPoruka = 0;
        private int brojOdobrenihKorisnika = 0;
        private int brojNeodobrenihKorisnika = 0;
        private final SimpleDateFormat formatDatuma = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        public PreglednikPoruka(ServletContextEvent sce) {
                bpk = (BP_Konfiguracija) sce.getServletContext().getAttribute("bpk");
                konfig = (Konfiguracija) sce.getServletContext().getAttribute("Konfig");
                interval = Integer.parseInt(konfig.dajPostavku("dretva.interval"));
                server = konfig.dajPostavku("mail.server");
                korisnik = konfig.dajPostavku("mail.username");
                lozinka = konfig.dajPostavku("mail.password");
        }

        @Override
        public void interrupt() {
                zaustavi = true;
                super.interrupt();
        }

        @Override
        public void run() {
                while (true) {
                        long poc = System.currentTimeMillis();

                        try {
                                // Start the session
                                java.util.Properties properties = System.getProperties();
                                properties.put("mail.smtp.host", server);
                                Session session = Session.getInstance(properties, null);

                                System.err.println(server + "," + korisnik + "," + lozinka);

                                // Connect to the store
                                Store store = session.getStore("imap");
                                store.connect(server, korisnik, lozinka);

                                Folder osnovnaMapa = store.getDefaultFolder();

                                for (Enumeration e = konfig.dajPostavke(); e.hasMoreElements();) {
                                        String stavka = (String) e.nextElement();
                                        if (stavka.startsWith("mail.folder.")) {
                                                String novaMapa = (String) konfig.dajPostavku(stavka);
                                                if (!osnovnaMapa.getFolder(novaMapa).exists()) {
                                                        osnovnaMapa.getFolder(novaMapa).create(Folder.HOLDS_MESSAGES);
                                                }
                                        }
                                }

                                // Open the INBOX folder
                                Folder folder = store.getFolder("INBOX");
                                folder.open(Folder.READ_WRITE);

                                Message[] messages = folder.getMessages();
                                String NWTiS_predmet = konfig.dajPostavku("mail.subject");

                                System.out.println("Broj poruka: " + messages.length);
                                // Print each message
                                for (Message message : messages) {
                                        if (message.isSet(Flags.Flag.SEEN)) {
                                                continue;
                                        }

                                        ukupanBrojPoruka++;
                                        MimeMessage m = (MimeMessage) message;
                                        if (m.getSubject().startsWith(NWTiS_predmet)) {
                                                ukupanBrojNwtisPoruka++;
                                                String sadrzaj = (String) m.getContent();
                                                Matcher parametri = provjeraSadrzaja(sadrzaj);
                                                if (parametri == null) {
                                                        Folder mapa = store.getFolder(konfig.dajPostavku("mapa.nwtis.neispravno"));
                                                        brojNeodobrenihKorisnika++;
                                                        mapa.appendMessages(new Message[]{m});
                                                        m.setFlag(Flags.Flag.DELETED, true);
                                                } else {
                                                        boolean rezultatObrade = obradiPoruku(m, parametri);
                                                        if (rezultatObrade) {
                                                                Folder mapa = store.getFolder(konfig.dajPostavku("mapa.nwtis.uspjesno"));
                                                                mapa.appendMessages(new Message[]{m});
                                                                brojOdobrenihKorisnika++;
                                                        } else {
                                                                Folder mapa = store.getFolder(konfig.dajPostavku("mapa.nwtis.neuspjesno"));
                                                                brojNeodobrenihKorisnika++;
                                                                mapa.appendMessages(new Message[]{m});
                                                        }
                                                        m.setFlag(Flags.Flag.DELETED, true);
                                                }
                                        } else {
                                                m.setFlag(Flags.Flag.SEEN, true);
                                        }
                                }
                        } catch (NoSuchProviderException ex) {
                                Logger.getLogger(PreglednikPoruka.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MessagingException | IOException ex) {
                                Logger.getLogger(PreglednikPoruka.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //Ako je dana naredba da se dretva zaustavi
                        if (zaustavi) {
                                break;
                        }

                        //Uspavljivanje dretve
                        try {
                                long trajanje = System.currentTimeMillis() - poc;
                                saljiJMS(poc, trajanje);
                                sleep(interval * 1000 - trajanje);
                        } catch (InterruptedException ex) {
                                Logger.getLogger(PreglednikPoruka.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }

        @Override
        public synchronized void start() {
                super.start();
        }

        private Matcher provjeraSadrzaja(String sadrzaj) {
                System.out.println("Provjera sadrzaja poruke: " + sadrzaj);
                String sintaksa = "^ADD ([^\\s]+); PASSWD ([^\\s]+); ROLE (ADMIN|USER);";
                Pattern pattern = Pattern.compile(sintaksa);
                Matcher m = pattern.matcher(sadrzaj);
                boolean status = m.matches();
                if (status) {
                        return m;
                } else {
                        return null;
                }
        }

        private boolean obradiPoruku(MimeMessage poruka, Matcher parametri) {
                try {
                        String db_server = bpk.getServerDatabase();
                        String db_baza = server + bpk.getAdminDatabase();
                        String dn_korisnik = bpk.getAdminUsername();
                        String db_lozinka = bpk.getAdminPassword();
                        String db_driver = bpk.getDriverDatabase(db_baza);
                        Class.forName(db_driver);
                        java.sql.Connection veza;
                        veza = DriverManager.getConnection(db_baza, dn_korisnik, db_lozinka);
                        Statement naredba = veza.createStatement();

                        String sql = "SELECT (*) from korisnici WHERE kor_ime = ?";
                        PreparedStatement ps = veza.prepareStatement(sql);
                        ps.setString(1, parametri.group(1));
                        ResultSet rezultat = ps.executeQuery();
                        int count = -1;
                        if (rezultat.next()) {
                                count = rezultat.getInt(1);
                        }
                        
                        if(count  == -1) {
                                System.out.println("Pogreška kod dohvaćanja podataka!");
                                throw new Exception();
                        } else if(count != 0) {
                                System.out.println("Korisnik već postoji u bazi podataka!");
                                int aktiviran=Integer.parseInt(rezultat.getString("AKTIVIRAN"));
                                if (aktiviran==0){
                                    return true;
                                }
                                else {
                                    return false;
                                }
                        }
                        else {
                            return false ;
                        }

                        
                } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(PreglednikPoruka.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                } catch (Exception ex) {
                        Logger.getLogger(PreglednikPoruka.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                }
        }

        private boolean saljiJMS(long poc, long trajanje) {
                JMSPoruka poruka = new JMSPoruka(poc, trajanje, ukupanBrojPoruka, ukupanBrojNwtisPoruka, brojOdobrenihKorisnika, brojNeodobrenihKorisnika);
                try {
                        sendJMSMessageToNwtis_embalint_1(poruka);
                } catch (JMSException | NamingException ex) {
                        Logger.getLogger(PreglednikPoruka.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
        }

        private javax.jms.Message createJMSMessageForjmsNwtis_embalint_1(javax.jms.Session session, Object messageData) throws JMSException {
                // TODO create and populate message to send
                TextMessage tm = session.createTextMessage();
                tm.setText(messageData.toString());
                return tm;
        }

        private void sendJMSMessageToNwtis_embalint_1(Object messageData) throws JMSException, NamingException {
                Context c = new InitialContext();
                ConnectionFactory cf = (ConnectionFactory) c.lookup("java:comp/env/java:comp/DefaultJMSConnectionFactory");
                Connection conn = null;
                javax.jms.Session s = null;
                try {
                        conn = cf.createConnection();
                        s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
                        Destination destination = (Destination) c.lookup("java:comp/env/jms/nwtis_embalint_1");
                        MessageProducer mp = s.createProducer(destination);
                        mp.send(createJMSMessageForjmsNwtis_embalint_1(s, messageData));
                } finally {
                        if (s != null) {
                                try {
                                        s.close();
                                } catch (JMSException e) {
                                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                                }
                        }
                        if (conn != null) {
                                conn.close();
                        }
                }
        }

}
