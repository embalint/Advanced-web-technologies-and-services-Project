/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lugerovac
 */
@Entity
@Table(name = "KORISNICI")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Korisnici.findAll", query = "SELECT k FROM Korisnici k"),
        @NamedQuery(name = "Korisnici.findByKorIme", query = "SELECT k FROM Korisnici k WHERE k.korIme = :korIme"),
        @NamedQuery(name = "Korisnici.findByAktiviran", query = "SELECT k FROM Korisnici k WHERE k.aktiviran = :aktiviran"),
        @NamedQuery(name = "Korisnici.findByDatumKreiranja", query = "SELECT k FROM Korisnici k WHERE k.datumKreiranja = :datumKreiranja"),
        @NamedQuery(name = "Korisnici.findByDatumPromjene", query = "SELECT k FROM Korisnici k WHERE k.datumPromjene = :datumPromjene"),
        @NamedQuery(name = "Korisnici.findByEmailAdresa", query = "SELECT k FROM Korisnici k WHERE k.emailAdresa = :emailAdresa"),
        @NamedQuery(name = "Korisnici.findByIme", query = "SELECT k FROM Korisnici k WHERE k.ime = :ime"),
        @NamedQuery(name = "Korisnici.findByKategorija", query = "SELECT k FROM Korisnici k WHERE k.kategorija = :kategorija"),
        @NamedQuery(name = "Korisnici.findByLozinka", query = "SELECT k FROM Korisnici k WHERE k.lozinka = :lozinka"),
        @NamedQuery(name = "Korisnici.findByPrezime", query = "SELECT k FROM Korisnici k WHERE k.prezime = :prezime"),
        @NamedQuery(name = "Korisnici.findByVrsta", query = "SELECT k FROM Korisnici k WHERE k.vrsta = :vrsta")})
public class Korisnici implements Serializable {
        private static final long serialVersionUID = 1L;
        @Id
        @Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 255)
        @Column(name = "KOR_IME")
        private String korIme;
        @Column(name = "AKTIVIRAN")
        private Short aktiviran;
        @Column(name = "DATUM_KREIRANJA")
        @Temporal(TemporalType.TIMESTAMP)
        private Date datumKreiranja;
        @Column(name = "DATUM_PROMJENE")
        @Temporal(TemporalType.TIMESTAMP)
        private Date datumPromjene;
        @Size(max = 255)
        @Column(name = "EMAIL_ADRESA")
        private String emailAdresa;
        @Size(max = 255)
        @Column(name = "IME")
        private String ime;
        @Column(name = "KATEGORIJA")
        private Integer kategorija;
        @Size(max = 255)
        @Column(name = "LOZINKA")
        private String lozinka;
        @Size(max = 255)
        @Column(name = "PREZIME")
        private String prezime;
        @Column(name = "VRSTA")
        private Integer vrsta;

        public Korisnici() {
        }

        public Korisnici(String korIme) {
                this.korIme = korIme;
        }

        public String getKorIme() {
                return korIme;
        }

        public void setKorIme(String korIme) {
                this.korIme = korIme;
        }

        public Short getAktiviran() {
                return aktiviran;
        }

        public void setAktiviran(Short aktiviran) {
                this.aktiviran = aktiviran;
        }

        public Date getDatumKreiranja() {
                return datumKreiranja;
        }

        public void setDatumKreiranja(Date datumKreiranja) {
                this.datumKreiranja = datumKreiranja;
        }

        public Date getDatumPromjene() {
                return datumPromjene;
        }

        public void setDatumPromjene(Date datumPromjene) {
                this.datumPromjene = datumPromjene;
        }

        public String getEmailAdresa() {
                return emailAdresa;
        }

        public void setEmailAdresa(String emailAdresa) {
                this.emailAdresa = emailAdresa;
        }

        public String getIme() {
                return ime;
        }

        public void setIme(String ime) {
                this.ime = ime;
        }

        public Integer getKategorija() {
                return kategorija;
        }

        public void setKategorija(Integer kategorija) {
                this.kategorija = kategorija;
        }

        public String getLozinka() {
                return lozinka;
        }

        public void setLozinka(String lozinka) {
                this.lozinka = lozinka;
        }

        public String getPrezime() {
                return prezime;
        }

        public void setPrezime(String prezime) {
                this.prezime = prezime;
        }

        public Integer getVrsta() {
                return vrsta;
        }

        public void setVrsta(Integer vrsta) {
                this.vrsta = vrsta;
        }

        @Override
        public int hashCode() {
                int hash = 0;
                hash += (korIme != null ? korIme.hashCode() : 0);
                return hash;
        }

        @Override
        public boolean equals(Object object) {
                // TODO: Warning - this method won't work in the case the id fields are not set
                if (!(object instanceof Korisnici)) {
                        return false;
                }
                Korisnici other = (Korisnici) object;
                if ((this.korIme == null && other.korIme != null) || (this.korIme != null && !this.korIme.equals(other.korIme))) {
                        return false;
                }
                return true;
        }

        @Override
        public String toString() {
                return "org.foi.nwtis.embalint.ejb.eb.Korisnici[ korIme=" + korIme + " ]";
        }
        
}
