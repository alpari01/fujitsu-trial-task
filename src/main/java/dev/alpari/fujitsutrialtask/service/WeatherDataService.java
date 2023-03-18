package dev.alpari.fujitsutrialtask.service;

import dev.alpari.fujitsutrialtask.dto.WeatherDataDto;
import dev.alpari.fujitsutrialtask.mapper.WeatherDataMapper;
import dev.alpari.fujitsutrialtask.model.WeatherData;
import dev.alpari.fujitsutrialtask.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WeatherDataService {

    private final WeatherDataRepository repository;
    private final WeatherDataMapper weatherDataMapper;

    public Iterable<WeatherData> getAll() {
        return repository.findAll();
    }

    public WeatherData getByStationName(String stationName) {
        return repository.findWeatherDataByStationName(stationName).orElseThrow();
    }

    public void add(WeatherDataDto weatherDataDto) {
        WeatherData weatherData = weatherDataMapper.dtoToEntity(weatherDataDto);
        repository.save(weatherData);
    }
}
