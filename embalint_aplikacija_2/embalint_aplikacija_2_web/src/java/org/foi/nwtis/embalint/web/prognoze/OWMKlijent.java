/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.web.prognoze;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foi.nwtis.embalint.web.klase.MeteoPodaci;
import org.foi.nwtis.embalint.web.klase.MeteoPrognoza;

public class OWMKlijent {

        String apiKey;
        OWMRESTHelper helper;
        Client client;

        public OWMKlijent(String apiKey) {
                this.apiKey = apiKey;
                helper = new OWMRESTHelper(apiKey);
                client = ClientBuilder.newClient();
        }

        /**
         * Metoda koja nam daje trenutne metereološke podatke
         * @param latitude
         * @param longitude
         * @return Metereološki podaci ovisno o lokaciji
         */
        public MeteoPodaci getRealTimeWeather(String latitude, String longitude) {
                WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                        .path(OWMRESTHelper.getOWM_Current_Path());
                webResource = webResource.queryParam("lat", latitude);
                webResource = webResource.queryParam("lon", longitude);
                webResource = webResource.queryParam("lang", "hr");
                webResource = webResource.queryParam("units", "metric");
                webResource = webResource.queryParam("APIKEY", apiKey);

                String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
                try {
                        JSONObject jo = new JSONObject(odgovor);
                        MeteoPodaci mp = new MeteoPodaci();
                        mp.setSunRise(new Date(jo.getJSONObject("sys").getLong("sunrise") * 1000));
                        mp.setSunSet(new Date(jo.getJSONObject("sys").getLong("sunset") * 1000));

                        mp.setTemperatureValue(Float.parseFloat(jo.getJSONObject("main").getString("temp")));
                        mp.setTemperatureMin(Float.parseFloat(jo.getJSONObject("main").getString("temp_min")));
                        mp.setTemperatureMax(Float.parseFloat(jo.getJSONObject("main").getString("temp_max")));
                        mp.setTemperatureUnit("celsius");

                        mp.setHumidityValue(Float.parseFloat(jo.getJSONObject("main").getString("pressure")));
                        mp.setHumidityUnit("%");

                        mp.setPressureValue(Float.parseFloat(jo.getJSONObject("main").getString("humidity")));
                        mp.setPressureUnit("hPa");

                        mp.setWindSpeedValue(Float.parseFloat(jo.getJSONObject("wind").getString("speed")));
                        mp.setWindSpeedName("");

                        mp.setWindDirectionValue(Float.parseFloat(jo.getJSONObject("wind").getString("deg")));
                        mp.setWindDirectionCode("");
                        mp.setWindDirectionName("");

                        mp.setCloudsValue(jo.getJSONObject("clouds").getInt("all"));
                        mp.setCloudsName(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
                        mp.setPrecipitationMode("");

                        mp.setWeatherNumber(jo.getJSONArray("weather").getJSONObject(0).getInt("id"));
                        mp.setWeatherValue(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
                        mp.setWeatherIcon(jo.getJSONArray("weather").getJSONObject(0).getString("icon"));

                        mp.setLastUpdate(new Date(jo.getLong("dt") * 1000));

                        return mp;

                } catch (JSONException ex) {
                        Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
        }

        /**
         * Metoda koja nam daje metereološke prognoze za određenu lokaciju
         * @param adresa Adresa lokacije, ne koristi se kao parametar pretrage
         * @param latitude latitude lokacija
         * @param longitude longitude lokacija
         * @param brojDana Broj dana, s time količina podataka, koji nam se vraćaju
         * @return polje podataka o prognozama
         */
        public MeteoPrognoza[] getWeatherForecast(String adresa, String latitude, String longitude, int brojDana) {
                MeteoPrognoza[] mp = new MeteoPrognoza[brojDana];

                WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                        .path(OWMRESTHelper.getOWM_ForecastDaily_Path());
                webResource = webResource.queryParam("lat", latitude);
                webResource = webResource.queryParam("lon", longitude);
                webResource = webResource.queryParam("cnt ", brojDana);
                webResource = webResource.queryParam("lang", "hr");
                webResource = webResource.queryParam("units", "metric");
                webResource = webResource.queryParam("APIKEY", apiKey);

                String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);

                try {
                        JSONObject jo = new JSONObject(odgovor);
                        for(int i = 0; i < brojDana; i++) {
                                mp[i] = new MeteoPrognoza();
                                mp[i].setDan(i+1);
                                mp[i].setAdresa(adresa);
                                
                                mp[i].getPrognoza().setTemperatureMin(Float.parseFloat(jo.getJSONArray("list").getJSONObject(i).getJSONObject("temp").getString("min")));
                                mp[i].getPrognoza().setTemperatureMax(Float.parseFloat(jo.getJSONArray("list").getJSONObject(i).getJSONObject("temp").getString("max")));
                                mp[i].getPrognoza().setTemperatureUnit("celsius");
                                
                                mp[i].getPrognoza().setHumidityValue(Float.parseFloat(jo.getJSONArray("list").getJSONObject(i).getString("humidity")));
                                mp[i].getPrognoza().setHumidityUnit("%");
                                
                                mp[i].getPrognoza().setPressureValue(Float.parseFloat(jo.getJSONArray("list").getJSONObject(i).getString("pressure")));
                                mp[i].getPrognoza().setPressureUnit("hPa");
                                mp[i].getPrognoza().setLastUpdate(new Date(jo.getJSONArray("list").getJSONObject(i).getLong("dt") * 1000));
                        }
                } catch (JSONException ex) {
                        Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                return mp;
        }
}
