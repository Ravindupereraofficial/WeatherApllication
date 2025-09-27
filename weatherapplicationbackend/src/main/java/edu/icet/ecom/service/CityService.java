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

@Service
@Slf4j
@RequiredArgsConstructor
public class CityService {
    private List<City> cities;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void loadCities() {
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
        return cities;
    }
    
    public City getCityByCode(String cityCode) {
        return cities.stream()
                .filter(city -> city.getCityCode().equals(cityCode))
                .findFirst()
                .orElse(null);
    }
}