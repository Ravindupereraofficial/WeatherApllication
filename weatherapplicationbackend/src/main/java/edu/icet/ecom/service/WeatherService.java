package edu.icet.ecom.service;

import edu.icet.ecom.model.City;
import edu.icet.ecom.model.OpenWeatherResponse;
import edu.icet.ecom.model.WeatherInfo;
import edu.icet.ecom.model.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    
    private final RestTemplate restTemplate;
    private final CityService cityService;
    
    @Value("${openweathermap.api.key}")
    private String apiKey;
    
    @Value("${openweathermap.api.url}")
    private String apiUrl;
    
    @Cacheable(value = "weatherCache", key = "#cityCode")
    public WeatherInfo getWeatherByCityCode(String cityCode) {
        City city = cityService.getCityByCode(cityCode);
        if (city == null) {
            log.warn("City not found for code: {}", cityCode);
            return null;
        }
        
        try {
            String url = String.format("%s?id=%s&appid=%s&units=metric", apiUrl, cityCode, apiKey);
            log.debug("Fetching weather data from: {}", url);
            
            OpenWeatherResponse response = restTemplate.getForObject(url, OpenWeatherResponse.class);
            
            if (response != null) {
                WeatherInfo weatherInfo = new WeatherInfo();
                weatherInfo.setCityCode(cityCode);
                weatherInfo.setCityName(response.getName());
                weatherInfo.setTemp(String.valueOf(response.getMain().getTemp()));
                weatherInfo.setStatus(response.getWeather().get(0).getDescription());
                
                log.debug("Weather data fetched for city: {}", response.getName());
                return weatherInfo;
            }
        } catch (Exception e) {
            log.error("Error fetching weather data for city code: {}", cityCode, e);
        }
        
        return null;
    }
    
    public WeatherResponse getAllWeatherData() {
        List<City> cities = cityService.getAllCities();
        List<WeatherInfo> weatherList = new ArrayList<>();
        
        for (City city : cities) {
            WeatherInfo weatherInfo = getWeatherByCityCode(city.getCityCode());
            if (weatherInfo != null) {
                weatherList.add(weatherInfo);
            }
        }
        
        WeatherResponse response = new WeatherResponse();
        response.setList(weatherList);
        return response;
    }
}