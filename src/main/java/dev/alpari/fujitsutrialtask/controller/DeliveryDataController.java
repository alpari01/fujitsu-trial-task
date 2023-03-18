package dev.alpari.fujitsutrialtask.controller;

import dev.alpari.fujitsutrialtask.dto.WeatherDataDto;
import dev.alpari.fujitsutrialtask.model.WeatherData;
import dev.alpari.fujitsutrialtask.service.DeliveryDataService;
import dev.alpari.fujitsutrialtask.service.WeatherDataService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Getter
@RestController
@RequestMapping("/api")
public class DeliveryDataController {

    private final WeatherDataService weatherDataService;
    private final DeliveryDataService deliveryDataService;

    @GetMapping("/weatherdata/getall")
    public Iterable<WeatherData> findAll() {
        System.out.println("Called /api/weatherdata");
        return weatherDataService.getAll();
    }

    @PostMapping(value = "/weatherdata/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addWeatherData(@RequestBody WeatherDataDto weatherDataDto) {
        weatherDataService.add(weatherDataDto);
    }

    @PostMapping(value = "/weatherdata/scheduler")
    public void setWeatherUpdateSchedulerParameters(boolean start) {
        if (start) deliveryDataService.getDeliveryManager().getWeatherDataManager().beginScheduledWeatherUpdate(true);
    }

    @GetMapping("/delivery/getfee/")
    public String getDeliveryFee(String location, String vehicle) {
        return deliveryDataService.getDeliveryFee(location, vehicle);
    }
}
