package edu.icet.ecom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    @JsonProperty("CityCode")
    private String CityCode;

    @JsonProperty("CityName")
    private String CityName;
}