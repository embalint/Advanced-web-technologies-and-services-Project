/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.ejb.eb;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lugerovac
 */
@Entity
@Table(name = "ADRESE")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Adrese.findAll", query = "SELECT a FROM Adrese a"),
        @NamedQuery(name = "Adrese.findById", query = "SELECT a FROM Adrese a WHERE a.id = :id"),
        @NamedQuery(name = "Adrese.findByAdresa", query = "SELECT a FROM Adrese a WHERE a.adresa = :adresa"),
        @NamedQuery(name = "Adrese.findByIzbrisana", query = "SELECT a FROM Adrese a WHERE a.izbrisana = :izbrisana"),
        @NamedQuery(name = "Adrese.findByKorisnik", query = "SELECT a FROM Adrese a WHERE a.korisnik = :korisnik"),
        @NamedQuery(name = "Adrese.findByLatitude", query = "SELECT a FROM Adrese a WHERE a.latitude = :latitude"),
        @NamedQuery(name = "Adrese.findByLongitude", query = "SELECT a FROM Adrese a WHERE a.longitude = :longitude")})
public class Adrese implements Serializable {
        private static final long serialVersionUID = 1L;
        @Id
        @Basic(optional = false)
        @NotNull
        @Column(name = "ID")
        private Integer id;
        @Size(max = 255)
        @Column(name = "ADRESA")
        private String adresa;
        @Column(name = "IZBRISANA")
        private Short izbrisana;
        @Size(max = 255)
        @Column(name = "KORISNIK")
        private String korisnik;
        @Size(max = 255)
        @Column(name = "LATITUDE")
        private String latitude;
        @Size(max = 255)
        @Column(name = "LONGITUDE")
        private String longitude;

        public Adrese() {
        }

        public Adrese(Integer id) {
                this.id = id;
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getAdresa() {
                return adresa;
        }

        public void setAdresa(String adresa) {
                this.adresa = adresa;
        }

        public Short getIzbrisana() {
                return izbrisana;
        }

        public void setIzbrisana(Short izbrisana) {
                this.izbrisana = izbrisana;
        }

        public String getKorisnik() {
                return korisnik;
        }

        public void setKorisnik(String korisnik) {
                this.korisnik = korisnik;
        }

        public String getLatitude() {
                return latitude;
        }

        public void setLatitude(String latitude) {
                this.latitude = latitude;
        }

        public String getLongitude() {
                return longitude;
        }

        public void setLongitude(String longitude) {
                this.longitude = longitude;
        }

        @Override
        public int hashCode() {
                int hash = 0;
                hash += (id != null ? id.hashCode() : 0);
                return hash;
        }

        @Override
        public boolean equals(Object object) {
                // TODO: Warning - this method won't work in the case the id fields are not set
                if (!(object instanceof Adrese)) {
                        return false;
                }
                Adrese other = (Adrese) object;
                if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
                        return false;
                }
                return true;
        }

        @Override
        public String toString() {
                return "org.foi.nwtis.embalint.ejb.eb.Adrese[ id=" + id + " ]";
        }
        
}
