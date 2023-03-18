package dev.alpari.fujitsutrialtask.service;

import dev.alpari.fujitsutrialtask.delivery.DeliveryManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Getter
@Service
public class DeliveryDataService {

    private final DeliveryManager deliveryManager;

    /**
     * Get delivery fee based on location and vehicle type.
     *
     * @param location location name
     * @param vehicleType vehicle type
     * @return total delivery fee
     */
    public String getDeliveryFee(String location, String vehicleType) {

        DeliveryManager.Location locationEnum = null;
        DeliveryManager.Vehicle vehicleTypeEnum = null;

        for (DeliveryManager.Location location1 : DeliveryManager.Location.values()) {
            if (location1.name().equals(location.toUpperCase())) locationEnum = location1;
        }
        if (locationEnum == null) return "No such location with name: " + location;

        for (DeliveryManager.Vehicle vehicleType1 : DeliveryManager.Vehicle.values()) {
            if (vehicleType1.name().equals(vehicleType.toUpperCase())) vehicleTypeEnum = vehicleType1;
        }
        if (vehicleTypeEnum == null) return "No such vehicle type: " + vehicleType;

        return deliveryManager.calculateAndGetDeliveryFee(vehicleTypeEnum, locationEnum);
    }
}
