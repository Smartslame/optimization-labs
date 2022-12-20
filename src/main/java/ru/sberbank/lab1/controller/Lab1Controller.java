package ru.sberbank.lab1.controller;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.lab1.service.WeatherService;

import java.util.List;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/lab1")
@Slf4j
public class Lab1Controller {
    private final WeatherService weatherService;

    public Lab1Controller(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    @Timed("LAB_1_weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            return weatherService.getTemperatureForLastDays(days);
        } catch (JSONException e) {
            log.error("Error to get weather for period", e);
            return emptyList();
        }
    }

}

