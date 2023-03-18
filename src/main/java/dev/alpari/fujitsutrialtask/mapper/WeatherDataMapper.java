package dev.alpari.fujitsutrialtask.mapper;

import dev.alpari.fujitsutrialtask.dto.WeatherDataDto;
import dev.alpari.fujitsutrialtask.model.WeatherData;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WeatherDataMapper {
    WeatherDataDto entityToDto(WeatherData weatherData);
    WeatherData dtoToEntity(WeatherDataDto weatherDataDto);
}
