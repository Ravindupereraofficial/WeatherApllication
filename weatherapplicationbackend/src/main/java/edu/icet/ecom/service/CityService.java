package edu.icet.ecom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.icet.ecom.model.City;
import edu.icet.ecom.model.CityList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class CityService {
    private List<City> cities;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void loadCities() {
        // Load city metadata from classpath once at startup to avoid repeated IO
        try {
            ClassPathResource resource = new ClassPathResource("cities.json");
            CityList cityList = objectMapper.readValue(resource.getInputStream(), CityList.class);
            this.cities = cityList.getList();
            log.info("Loaded {} cities from cities.json", cities.size());
        } catch (IOException e) {
            log.error("Failed to load cities from cities.json", e);
            throw new RuntimeException("Failed to load cities configuration", e);
        }
    }
    
    public List<City> getAllCities() {
        // Return an immutable-safe empty list if cities haven't been loaded
        return cities == null ? Collections.emptyList() : cities;
    }
    
    public City getCityByCode(String cityCode) {
        if (cities == null) {
            return null;
        }
        return cities.stream()
                .filter(city -> Objects.equals(city.getCityCode(), cityCode))
                .findFirst()
                .orElse(null);
    }
}