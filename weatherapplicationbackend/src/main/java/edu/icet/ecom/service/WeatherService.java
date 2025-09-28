package edu.icet.ecom.service;

import edu.icet.ecom.model.City;
import edu.icet.ecom.model.OpenWeatherResponse;
import edu.icet.ecom.model.WeatherInfo;
import edu.icet.ecom.model.WeatherResponse;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    
    @Value("${openweathermap.api.key}")
    private String apiKey;
    
    @Value("${openweathermap.api.url}")
    private String apiUrl;
    
    @Cacheable(value = "weatherCache", key = "#cityCode")
    public WeatherInfo getWeatherByCityCode(String cityCode) {
        // Use cached responses where possible to limit OpenWeather API usage and rate limits
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
                // Map remote OpenWeather payload to our local DTO
                WeatherInfo weatherInfo = modelMapper.map(response, WeatherInfo.class);
                weatherInfo.setCityCode(cityCode);
                log.debug("Weather data fetched & mapped for city: {}", response.getName());
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
            String cityCode = city == null ? null : city.getCityCode();
            if (cityCode == null || cityCode.trim().isEmpty()) {
                log.warn("Skipping city with missing code: {}", city);
                continue;
            }

            WeatherInfo weatherInfo = getWeatherByCityCode(cityCode);
            if (weatherInfo != null) {
                weatherList.add(weatherInfo);
            }
        }
        
        WeatherResponse response = new WeatherResponse();
        response.setList(weatherList);
        return response;
    }
}