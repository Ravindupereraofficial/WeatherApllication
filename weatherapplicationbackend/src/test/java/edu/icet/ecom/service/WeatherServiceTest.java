package edu.icet.ecom.service;

import edu.icet.ecom.model.City;
import edu.icet.ecom.model.OpenWeatherResponse;
import edu.icet.ecom.model.WeatherInfo;
import edu.icet.ecom.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CityService cityService;

    @InjectMocks
    private WeatherService weatherService;

    private City testCity;
    private OpenWeatherResponse testResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(weatherService, "apiUrl", "http://test-api.com");

        testCity = new City("1248991", "Colombo");
        
        testResponse = new OpenWeatherResponse();
        testResponse.setName("Colombo");
        
        OpenWeatherResponse.Weather weather = new OpenWeatherResponse.Weather();
        weather.setDescription("Clear");
        testResponse.setWeather(Arrays.asList(weather));
        
        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(33.0);
        testResponse.setMain(main);
    }

    @Test
    void testGetWeatherByCityCode_Success() {
        // Given
        String cityCode = "1248991";
        when(cityService.getCityByCode(cityCode)).thenReturn(testCity);
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenReturn(testResponse);

        // When
        WeatherInfo result = weatherService.getWeatherByCityCode(cityCode);

        // Then
        assertNotNull(result);
        assertEquals(cityCode, result.getCityCode());
        assertEquals("Colombo", result.getCityName());
        assertEquals("33.0", result.getTemp());
        assertEquals("Clear", result.getStatus());
        
        verify(cityService).getCityByCode(cityCode);
        verify(restTemplate).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }

    @Test
    void testGetWeatherByCityCode_CityNotFound() {
        // Given
        String cityCode = "invalid";
        when(cityService.getCityByCode(cityCode)).thenReturn(null);

        // When
        WeatherInfo result = weatherService.getWeatherByCityCode(cityCode);

        // Then
        assertNull(result);
        verify(cityService).getCityByCode(cityCode);
        verify(restTemplate, never()).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }

    @Test
    void testGetAllWeatherData() {
        // Given
        List<City> cities = Arrays.asList(testCity);
        when(cityService.getAllCities()).thenReturn(cities);
        when(cityService.getCityByCode("1248991")).thenReturn(testCity);
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenReturn(testResponse);

        // When
        WeatherResponse result = weatherService.getAllWeatherData();

        // Then
        assertNotNull(result);
        assertNotNull(result.getList());
        assertEquals(1, result.getList().size());
        
        WeatherInfo weatherInfo = result.getList().get(0);
        assertEquals("1248991", weatherInfo.getCityCode());
        assertEquals("Colombo", weatherInfo.getCityName());
        
        verify(cityService).getAllCities();
    }
}