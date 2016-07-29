package org.foi.nwtis.embalint.web.klase;

public class JMSPoruka {
        private final long vrijemePocetka;
        private final long vrijemeZavrsetka;
        private final int brojProcitanihPoruka;
        private final int brojNwtisPoruka;
        private final int brojOdobrenihKorisnika;
        private final int brojNeodobrenihKorisnika;
        
        public JMSPoruka(long vrijemePocetka, long vrijemeZavrsetka, int brojProcitanihPoruka, int brojNwtisPoruka, int brojOdobrenihKorisnika, int brojNeodobrenihKorisnika) {
                this.vrijemePocetka = vrijemePocetka;
                this.vrijemeZavrsetka = vrijemeZavrsetka;
                this.brojProcitanihPoruka = brojProcitanihPoruka;
                this.brojNwtisPoruka = brojNwtisPoruka;
                this.brojOdobrenihKorisnika = brojOdobrenihKorisnika;
                this.brojNeodobrenihKorisnika = brojNeodobrenihKorisnika;
        }

        public long getVrijemePocetka() {
                return vrijemePocetka;
        }

        public long getVrijemeZavrsetka() {
                return vrijemeZavrsetka;
        }

        public int getBrojProcitanihPoruka() {
                return brojProcitanihPoruka;
        }

        public int getBrojNwtisPoruka() {
                return brojNwtisPoruka;
        }

        public int getBrojOdobrenihKorisnika() {
                return brojOdobrenihKorisnika;
        }

        public int getBrojNeodobrenihKorisnika() {
                return brojNeodobrenihKorisnika;
        }
}
