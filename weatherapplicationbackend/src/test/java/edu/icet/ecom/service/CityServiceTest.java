package edu.icet.ecom.service;

import edu.icet.ecom.model.City;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @InjectMocks
    private CityService cityService;

    @Test
    void testGetAllCities() {
        // Given
        List<City> testCities = Arrays.asList(
                new City("1248991", "Colombo"),
                new City("1850147", "Tokyo")
        );
        ReflectionTestUtils.setField(cityService, "cities", testCities);

        // When
        List<City> result = cityService.getAllCities();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Colombo", result.get(0).getCityName());
        assertEquals("Tokyo", result.get(1).getCityName());
    }

    @Test
    void testGetCityByCode_Found() {
        // Given
        List<City> testCities = Arrays.asList(
                new City("1248991", "Colombo"),
                new City("1850147", "Tokyo")
        );
        ReflectionTestUtils.setField(cityService, "cities", testCities);

        // When
        City result = cityService.getCityByCode("1248991");

        // Then
        assertNotNull(result);
        assertEquals("1248991", result.getCityCode());
        assertEquals("Colombo", result.getCityName());
    }

    @Test
    void testGetCityByCode_NotFound() {
        // Given
        List<City> testCities = Arrays.asList(
                new City("1248991", "Colombo"),
                new City("1850147", "Tokyo")
        );
        ReflectionTestUtils.setField(cityService, "cities", testCities);

        // When
        City result = cityService.getCityByCode("invalid");

        // Then
        assertNull(result);
    }
}