package edu.icet.ecom.controller;

import edu.icet.ecom.model.WeatherInfo;
import edu.icet.ecom.model.WeatherResponse;
import edu.icet.ecom.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WeatherController {
    
    private final WeatherService weatherService;
    
    @GetMapping("/{cityCode}")
    public ResponseEntity<WeatherInfo> getWeatherByCityCode(
            @PathVariable String cityCode,
            @AuthenticationPrincipal Jwt jwt) {
        
        log.info("Weather request for city code: {} by user: {}", 
                cityCode, jwt.getSubject());
        
        WeatherInfo weatherInfo = weatherService.getWeatherByCityCode(cityCode);
        
        if (weatherInfo != null) {
            return ResponseEntity.ok(weatherInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<WeatherResponse> getAllWeatherData(
            @AuthenticationPrincipal Jwt jwt) {
        
        log.info("All weather data request by user: {}", jwt.getSubject());
        
        WeatherResponse weatherResponse = weatherService.getAllWeatherData();
        return ResponseEntity.ok(weatherResponse);
    }
}