package org.foi.nwtis.embalint.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.embalint.konfiguracije.Konfiguracija;
import org.foi.nwtis.embalint.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.embalint.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.embalint.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.embalint.web.poruke.PreglednikPoruka;

@WebListener()
public class SlusacAplikacije implements ServletContextListener {
    PreglednikPoruka dretva;
    public static Konfiguracija konfig;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dir = sce.getServletContext().
                getRealPath("/WEB-INF") + File.separator;
        String datoteka = dir + sce.getServletContext().
                getInitParameter("Konfiguracija");
        try {
            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            sce.getServletContext().setAttribute("Konfig", konfig);
            BP_Konfiguracija bpk = new BP_Konfiguracija(datoteka);
            sce.getServletContext().setAttribute("bpk", bpk);

            dretva = new PreglednikPoruka(sce);
            dretva.start();
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("BP_konfig");
        if (dretva != null && !dretva.isInterrupted()) {
            dretva.interrupt();
        }
    }
}
