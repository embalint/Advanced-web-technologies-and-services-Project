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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lugerovac
 */
@Entity
@Table(name = "DNEVNIK")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Dnevnik.findAll", query = "SELECT d FROM Dnevnik d"),
        @NamedQuery(name = "Dnevnik.findById", query = "SELECT d FROM Dnevnik d WHERE d.id = :id"),
        @NamedQuery(name = "Dnevnik.findByIpadresa", query = "SELECT d FROM Dnevnik d WHERE d.ipadresa = :ipadresa"),
        @NamedQuery(name = "Dnevnik.findByKorisnik", query = "SELECT d FROM Dnevnik d WHERE d.korisnik = :korisnik"),
        @NamedQuery(name = "Dnevnik.findByOperacija", query = "SELECT d FROM Dnevnik d WHERE d.operacija = :operacija"),
        @NamedQuery(name = "Dnevnik.findByRezultat", query = "SELECT d FROM Dnevnik d WHERE d.rezultat = :rezultat"),
        @NamedQuery(name = "Dnevnik.findByTrajanje", query = "SELECT d FROM Dnevnik d WHERE d.trajanje = :trajanje"),
        @NamedQuery(name = "Dnevnik.findByVrijeme", query = "SELECT d FROM Dnevnik d WHERE d.vrijeme = :vrijeme")})
public class Dnevnik implements Serializable {
        private static final long serialVersionUID = 1L;
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Basic(optional = false)
        @Column(name = "ID")
        private Integer id;
        @Size(max = 255)
        @Column(name = "IPADRESA")
        private String ipadresa;
        @Size(max = 255)
        @Column(name = "KORISNIK")
        private String korisnik;
        @Size(max = 255)
        @Column(name = "OPERACIJA")
        private String operacija;
        @Size(max = 255)
        @Column(name = "REZULTAT")
        private String rezultat;
        @Column(name = "TRAJANJE")
        private Integer trajanje;
        @Column(name = "VRIJEME")
        @Temporal(TemporalType.TIMESTAMP)
        private Date vrijeme;

        public Dnevnik() {
        }

        public Dnevnik(Integer id) {
                this.id = id;
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getIpadresa() {
                return ipadresa;
        }

        public void setIpadresa(String ipadresa) {
                this.ipadresa = ipadresa;
        }

        public String getKorisnik() {
                return korisnik;
        }

        public void setKorisnik(String korisnik) {
                this.korisnik = korisnik;
        }

        public String getOperacija() {
                return operacija;
        }

        public void setOperacija(String operacija) {
                this.operacija = operacija;
        }

        public String getRezultat() {
                return rezultat;
        }

        public void setRezultat(String rezultat) {
                this.rezultat = rezultat;
        }

        public Integer getTrajanje() {
                return trajanje;
        }

        public void setTrajanje(Integer trajanje) {
                this.trajanje = trajanje;
        }

        public Date getVrijeme() {
                return vrijeme;
        }

        public void setVrijeme(Date vrijeme) {
                this.vrijeme = vrijeme;
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
                if (!(object instanceof Dnevnik)) {
                        return false;
                }
                Dnevnik other = (Dnevnik) object;
                if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
                        return false;
                }
                return true;
        }

        @Override
        public String toString() {
                return "org.foi.nwtis.embalint.ejb.eb.Dnevnik[ id=" + id + " ]";
        }
        
}