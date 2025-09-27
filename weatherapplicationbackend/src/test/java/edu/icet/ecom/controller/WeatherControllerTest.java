package edu.icet.ecom.controller;

import edu.icet.ecom.model.WeatherInfo;
import edu.icet.ecom.model.WeatherResponse;
import edu.icet.ecom.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private WeatherController weatherController;

    private WeatherInfo testWeatherInfo;
    private WeatherResponse testWeatherResponse;

    @BeforeEach
    void setUp() {
        testWeatherInfo = new WeatherInfo("1248991", "Colombo", "33.0", "Clear");
        
        testWeatherResponse = new WeatherResponse();
        testWeatherResponse.setList(Arrays.asList(testWeatherInfo));
        
        when(jwt.getSubject()).thenReturn("test-user");
    }

    @Test
    void testGetWeatherByCityCode_Success() {
        // Given
        String cityCode = "1248991";
        when(weatherService.getWeatherByCityCode(cityCode)).thenReturn(testWeatherInfo);

        // When
        ResponseEntity<WeatherInfo> response = weatherController.getWeatherByCityCode(cityCode, jwt);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Colombo", response.getBody().getCityName());
        assertEquals("33.0", response.getBody().getTemp());
        
        verify(weatherService).getWeatherByCityCode(cityCode);
    }

    @Test
    void testGetWeatherByCityCode_NotFound() {
        // Given
        String cityCode = "invalid";
        when(weatherService.getWeatherByCityCode(cityCode)).thenReturn(null);

        // When
        ResponseEntity<WeatherInfo> response = weatherController.getWeatherByCityCode(cityCode, jwt);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(weatherService).getWeatherByCityCode(cityCode);
    }

    @Test
    void testGetAllWeatherData() {
        // Given
        when(weatherService.getAllWeatherData()).thenReturn(testWeatherResponse);

        // When
        ResponseEntity<WeatherResponse> response = weatherController.getAllWeatherData(jwt);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getList());
        assertEquals(1, response.getBody().getList().size());
        assertEquals("Colombo", response.getBody().getList().get(0).getCityName());
        
        verify(weatherService).getAllWeatherData();
    }
}