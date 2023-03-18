package dev.alpari.fujitsutrialtask.repository;

import dev.alpari.fujitsutrialtask.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Integer> {
    Optional<WeatherData> findWeatherDataByStationName(String stationName);
}
