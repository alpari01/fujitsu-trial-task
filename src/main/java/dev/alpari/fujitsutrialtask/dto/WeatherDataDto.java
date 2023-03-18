package dev.alpari.fujitsutrialtask.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
public class WeatherDataDto {

    private String stationName;
    private String stationWmoCode;
    private float airTemperature;
    private float windSpeed;
    private String weatherPhenomenon;
    private String observationTimestamp;
}
