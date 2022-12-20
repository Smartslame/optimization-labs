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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class WeatherService {
    private static final String TOKEN = "ac1830efeff59c748d212052f27d49aa";
    private static final String BASE_URI = "https://api.darksky.net/forecast/" + TOKEN + "/";
    private static final String LA_COORDINATES = "34.053044,-118.243750,";
    private static final String EXCLUDE = "exclude=daily";
    private static final int N_THREADS = 4;
    private final RestTemplate restTemplate;
    private final ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Double> getTemperatureForLastDays(int days) throws InterruptedException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        List<Double> temps = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            int finalI = i;
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(
                    () -> {
                        try {
                            temps.add(getTempForDay(finalI));
                        } catch (Exception e) {
                            log.error("Error during completable future", e);
                        }
                    },
                    executor
            );
            futures.add(completableFuture);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                             .exceptionally(e -> {
                                 log.error("Error during completable future", e);
                                 return null;
                             }).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return temps;
    }

    private Double getTempForDay(int dayShift) throws JSONException {
        Long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
        Long oneDayInSec = 24 * 60 * 60L;
        Long curDateSec = currentDayInSec - dayShift * oneDayInSec;
        return getTemperatureFromInfo(curDateSec.toString());
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
