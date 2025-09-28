package edu.icet.ecom.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import edu.icet.ecom.model.OpenWeatherResponse;
import edu.icet.ecom.model.WeatherInfo;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setSkipNullEnabled(true);

    TypeMap<OpenWeatherResponse, WeatherInfo> typeMap = mapper.createTypeMap(OpenWeatherResponse.class, WeatherInfo.class);

    typeMap.addMappings(m -> m.map(OpenWeatherResponse::getName, WeatherInfo::setCityName));

    // Convert OpenWeather main.temp numeric value into a string for our DTO
    Converter<OpenWeatherResponse, String> tempConverter = ctx -> {
        OpenWeatherResponse src = ctx.getSource();
        if (src == null || src.getMain() == null || src.getMain().getTemp() == null) return null;
        return String.valueOf(src.getMain().getTemp());
    };

    Converter<OpenWeatherResponse, String> statusConverter = ctx -> {
        OpenWeatherResponse src = ctx.getSource();
        if (src == null || src.getWeather() == null || src.getWeather().isEmpty() || src.getWeather().get(0) == null) return null;
        return src.getWeather().get(0).getDescription();
    };

    typeMap.addMappings(m -> {
        m.using(tempConverter).map(src -> src, WeatherInfo::setTemp);
        m.using(statusConverter).map(src -> src, WeatherInfo::setStatus);
    });

        return mapper;
    }
}