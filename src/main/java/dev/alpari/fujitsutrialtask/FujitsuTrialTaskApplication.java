package dev.alpari.fujitsutrialtask;

import dev.alpari.fujitsutrialtask.delivery.DeliveryManager;
import dev.alpari.fujitsutrialtask.dto.WeatherDataDto;
import dev.alpari.fujitsutrialtask.mapper.WeatherDataMapper;
import dev.alpari.fujitsutrialtask.model.WeatherData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FujitsuTrialTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(FujitsuTrialTaskApplication.class, args);
    }

    @Bean
    public DeliveryManager deliveryManager() {
        return new DeliveryManager();
    }

    @Bean
    public WeatherDataMapper weatherDataMapper() {
        return new WeatherDataMapper() {
            @Override
            public WeatherDataDto entityToDto(WeatherData weatherData) {
                return null;
            }

            @Override
            public WeatherData dtoToEntity(WeatherDataDto weatherDataDto) {
                return WeatherData.builder()
                        .stationName(weatherDataDto.getStationName())
                        .stationWmoCode(weatherDataDto.getStationWmoCode())
                        .airTemperature(weatherDataDto.getAirTemperature())
                        .windSpeed(weatherDataDto.getWindSpeed())
                        .weatherPhenomenon(weatherDataDto.getWeatherPhenomenon())
                        .observationTimestamp(weatherDataDto.getObservationTimestamp())
                        .build();
            }
        };
    }
}
