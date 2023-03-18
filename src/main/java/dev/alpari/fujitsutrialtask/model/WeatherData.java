package dev.alpari.fujitsutrialtask.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WeatherData {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    private String stationName;
    private String stationWmoCode;
    private float airTemperature;
    private float windSpeed;
    private String weatherPhenomenon;
    private String observationTimestamp;

    public String toJson() {

        return "{\n" +
                "    \"stationName\": " + "\"" + stationName + "\"" + ",\n" +
                "    \"stationWmoCode\": " + "\"" + stationWmoCode + "\"" + ",\n" +
                "    \"airTemperature\": " + "\"" + airTemperature + "\"" + ",\n" +
                "    \"windSpeed\": " + "\"" + windSpeed + "\"" + ",\n" +
                "    \"weatherPhenomenon\": " + "\"" + weatherPhenomenon + "\"" + ",\n" +
                "    \"observationTimestamp\": " + "\"" + observationTimestamp + "\"" + "\n" +
                "}";
    }
}