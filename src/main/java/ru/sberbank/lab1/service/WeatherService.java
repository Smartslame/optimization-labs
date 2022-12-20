package ru.sberbank.lab1.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class WeatherService {
    public static final String TOKEN = "ac1830efeff59c748d212052f27d49aa";
    public static final String BASE_URI = "https://api.darksky.net/forecast/" + TOKEN + "/";
    public static final String LA_COORDINATES = "34.053044,-118.243750,";
    public static final String EXCLUDE = "exclude=daily";
    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        List<Double> temps = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            Long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
            Long oneDayInSec = 24 * 60 * 60L;
            Long curDateSec = currentDayInSec - i * oneDayInSec;
            Double curTemp = getTemperatureFromInfo(curDateSec.toString());
            temps.add(curTemp);
        }

        return temps;
    }

    private String getTodayWeather(String date) {
        String fooResourceUrl = BASE_URI + LA_COORDINATES + date + "?" + EXCLUDE;
        log.info("Url to send: {}", fooResourceUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        String info = response.getBody();
        log.info("Response : {}", info);
        return info;
    }

    private Double getTemperatureFromInfo(String date) throws JSONException {
        String info = getTodayWeather(date);
        return getTemperature(info);
    }

    private Double getTemperature(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");
        return new JSONObject(data.get(0).toString()).getDouble("temperature");
    }
}
