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
@Table(name = "METEOPODACI")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Meteopodaci.findAll", query = "SELECT m FROM Meteopodaci m"),
        @NamedQuery(name = "Meteopodaci.findById", query = "SELECT m FROM Meteopodaci m WHERE m.id = :id"),
        @NamedQuery(name = "Meteopodaci.findByAddress", query = "SELECT m FROM Meteopodaci m WHERE m.address = :address"),
        @NamedQuery(name = "Meteopodaci.findByHumidity", query = "SELECT m FROM Meteopodaci m WHERE m.humidity = :humidity"),
        @NamedQuery(name = "Meteopodaci.findByPressure", query = "SELECT m FROM Meteopodaci m WHERE m.pressure = :pressure"),
        @NamedQuery(name = "Meteopodaci.findByTemperature", query = "SELECT m FROM Meteopodaci m WHERE m.temperature = :temperature"),
        @NamedQuery(name = "Meteopodaci.findByWeather", query = "SELECT m FROM Meteopodaci m WHERE m.weather = :weather"),
        @NamedQuery(name = "Meteopodaci.findByWind", query = "SELECT m FROM Meteopodaci m WHERE m.wind = :wind")})
public class Meteopodaci implements Serializable {
        private static final long serialVersionUID = 1L;
        @Id
        @Basic(optional = false)
        @NotNull
        @Column(name = "ID")
        private Integer id;
        @Size(max = 255)
        @Column(name = "ADDRESS")
        private String address;
        // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
        @Column(name = "HUMIDITY")
        private Double humidity;
        @Column(name = "PRESSURE")
        private Double pressure;
        @Column(name = "TEMPERATURE")
        private Double temperature;
        @Size(max = 255)
        @Column(name = "WEATHER")
        private String weather;
        @Size(max = 255)
        @Column(name = "WIND")
        private String wind;

        public Meteopodaci() {
        }

        public Meteopodaci(Integer id) {
                this.id = id;
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }

        public Double getHumidity() {
                return humidity;
        }

        public void setHumidity(Double humidity) {
                this.humidity = humidity;
        }

        public Double getPressure() {
                return pressure;
        }

        public void setPressure(Double pressure) {
                this.pressure = pressure;
        }

        public Double getTemperature() {
                return temperature;
        }

        public void setTemperature(Double temperature) {
                this.temperature = temperature;
        }

        public String getWeather() {
                return weather;
        }

        public void setWeather(String weather) {
                this.weather = weather;
        }

        public String getWind() {
                return wind;
        }

        public void setWind(String wind) {
                this.wind = wind;
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
                if (!(object instanceof Meteopodaci)) {
                        return false;
                }
                Meteopodaci other = (Meteopodaci) object;
                if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
                        return false;
                }
                return true;
        }

        @Override
        public String toString() {
                return "org.foi.nwtis.embalint.ejb.eb.Meteopodaci[ id=" + id + " ]";
        }
        
}
