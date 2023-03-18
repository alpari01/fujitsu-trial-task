package dev.alpari.fujitsutrialtask.delivery;

import dev.alpari.fujitsutrialtask.model.WeatherData;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DeliveryManagerTest {

    private final String EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN = "Usage of selected vehicle type is forbidden";

    @Test
    public void testSetRegionalBaseFee() {

        DeliveryManager deliveryManager = new DeliveryManager();

        deliveryManager.setRegionalBaseFee(DeliveryManager.Vehicle.BIKE, DeliveryManager.Location.TALLINN, 3.0f);

        // Test if hashmap contains corresponding key.
        assertTrue(deliveryManager.getRegionalBaseFees()
                .containsKey(DeliveryManager.Vehicle.BIKE + "," + DeliveryManager.Location.TALLINN));

        // Test if value of this key is correct.
        assertEquals(3.0f, deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.BIKE,
                DeliveryManager.Location.TALLINN));
    }

    @Test
    public void testSetRegionalBaseFeeOverwritesOldValue() {

        DeliveryManager deliveryManager = new DeliveryManager();

        deliveryManager.setRegionalBaseFee(DeliveryManager.Vehicle.BIKE, DeliveryManager.Location.TALLINN, 3.0f);
        deliveryManager.setRegionalBaseFee(DeliveryManager.Vehicle.BIKE, DeliveryManager.Location.TALLINN, 5.0f);

        assertEquals(5.0f, deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.BIKE,
                DeliveryManager.Location.TALLINN));
    }

    @Test
    public void testRegionalBaseFeeIsCorrectForTallinn() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedRegionalBaseFeeForCar = 4.0f;
        float expectedRegionalBaseFeeForScooter = 3.5f;
        float expectedRegionalBaseFeeForBike = 3.0f;

        assertEquals(expectedRegionalBaseFeeForCar,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.CAR, DeliveryManager.Location.TALLINN));

        assertEquals(expectedRegionalBaseFeeForScooter,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.SCOOTER, DeliveryManager.Location.TALLINN));

        assertEquals(expectedRegionalBaseFeeForBike,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.BIKE, DeliveryManager.Location.TALLINN));
    }

    @Test
    public void testRegionalBaseFeeIsCorrectForTartu() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedRegionalBaseFeeForCar = 3.5f;
        float expectedRegionalBaseFeeForScooter = 3.0f;
        float expectedRegionalBaseFeeForBike = 2.5f;

        assertEquals(expectedRegionalBaseFeeForCar,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.CAR, DeliveryManager.Location.TARTU));

        assertEquals(expectedRegionalBaseFeeForScooter,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.SCOOTER, DeliveryManager.Location.TARTU));

        assertEquals(expectedRegionalBaseFeeForBike,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.BIKE, DeliveryManager.Location.TARTU));
    }

    @Test
    public void testRegionalBaseFeeIsCorrectForParnu() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedRegionalBaseFeeForCar = 3.0f;
        float expectedRegionalBaseFeeForScooter = 2.5f;
        float expectedRegionalBaseFeeForBike = 2.0f;

        assertEquals(expectedRegionalBaseFeeForCar,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.CAR, DeliveryManager.Location.PÄRNU));

        assertEquals(expectedRegionalBaseFeeForScooter,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.SCOOTER, DeliveryManager.Location.PÄRNU));

        assertEquals(expectedRegionalBaseFeeForBike,
                deliveryManager.getRegionalBaseFee(DeliveryManager.Vehicle.BIKE, DeliveryManager.Location.PÄRNU));
    }

    @Test
    public void testGetAirTemperatureFeeIsZeroIfNotVehicleScooterOrBike() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedAirTemperatureFee = 0f;

        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-11f, DeliveryManager.Vehicle.CAR));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-10f, DeliveryManager.Vehicle.CAR));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-9f, DeliveryManager.Vehicle.CAR));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(0f, DeliveryManager.Vehicle.CAR));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(5f, DeliveryManager.Vehicle.CAR));
    }

    @Test
    public void testGetAirTemperatureFeeIsOneIfAirTemperatureLessThanMinusTen() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedAirTemperatureFee = 1f;

        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-11f, DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-11f, DeliveryManager.Vehicle.SCOOTER));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-10.1f, DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-10.1f, DeliveryManager.Vehicle.SCOOTER));
    }

    @Test
    public void testGetAirTemperatureFeeIs05IfAirTemperatureBetweenZeroAndMinusTen() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedAirTemperatureFee = 0.5f;

        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-10f, DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-10f, DeliveryManager.Vehicle.SCOOTER));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-5f, DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-5f, DeliveryManager.Vehicle.SCOOTER));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-0f, DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedAirTemperatureFee, deliveryManager.getAirTemperatureFee(-0f, DeliveryManager.Vehicle.SCOOTER));
    }

    @Test
    public void testGetWindSpeedFeeIsZeroWhenVehicleTypeNotBike() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedWindSpeedFee = 0f;

        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(9f, DeliveryManager.Vehicle.SCOOTER));
        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(9f, DeliveryManager.Vehicle.CAR));
        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(10f, DeliveryManager.Vehicle.SCOOTER));
        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(10f, DeliveryManager.Vehicle.CAR));
        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(20f, DeliveryManager.Vehicle.SCOOTER));
        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(20f, DeliveryManager.Vehicle.CAR));
        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(21f, DeliveryManager.Vehicle.SCOOTER));
        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(21f, DeliveryManager.Vehicle.CAR));
    }

    @Test
    public void testGetWindSpeedForBikeIs05WhenWindSpeedBetween10And20() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedWindSpeedFee = 0.5f;

        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(10f, DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWindSpeedFee, deliveryManager.getWindSpeedFee(20f, DeliveryManager.Vehicle.BIKE));
    }

    @Test
    public void testGetWindSpeedForBikeThrowsExceptionIfWindSpeedGreaterThan20() {

        DeliveryManager deliveryManager = new DeliveryManager();

        try {
            deliveryManager.getWindSpeedFee(20.1f, DeliveryManager.Vehicle.BIKE);
            fail("Should have thrown the exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN, e.getMessage());
        }
    }

    @Test
    public void testGetWeatherPhenomenonFeeIsZeroIfVehicleTypeNotScooterOrBike() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedWeatherPhenomenonFee = 0f;

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light rain", DeliveryManager.Vehicle.CAR));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light snow shower", DeliveryManager.Vehicle.CAR));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("glaze", DeliveryManager.Vehicle.CAR));

    }

    @Test
    public void testGetWeatherPhenomenonFeeThrowsExceptionWhenGlazeAndVehicleScooter() {

        DeliveryManager deliveryManager = new DeliveryManager();

        try {
            deliveryManager.getWeatherPhenomenonFee("glaze", DeliveryManager.Vehicle.SCOOTER);
            fail("Should have thrown the exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN, e.getMessage());
        }
    }

    @Test
    public void testGetWeatherPhenomenonFeeThrowsExceptionWhenHailAndVehicleScooter() {

        DeliveryManager deliveryManager = new DeliveryManager();

        try {
            deliveryManager.getWeatherPhenomenonFee("hail", DeliveryManager.Vehicle.SCOOTER);
            fail("Should have thrown the exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN, e.getMessage());
        }
    }

    @Test
    public void testGetWeatherPhenomenonFeeThrowsExceptionWhenThunderAndVehicleScooter() {

        DeliveryManager deliveryManager = new DeliveryManager();

        try {
            deliveryManager.getWeatherPhenomenonFee("thunder", DeliveryManager.Vehicle.SCOOTER);
            fail("Should have thrown the exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN, e.getMessage());
        }
    }

    @Test
    public void testGetWeatherPhenomenonFeeThrowsExceptionWhenGlazeAndVehicleBike() {

        DeliveryManager deliveryManager = new DeliveryManager();

        try {
            deliveryManager.getWeatherPhenomenonFee("glaze", DeliveryManager.Vehicle.BIKE);
            fail("Should have thrown the exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN, e.getMessage());
        }
    }

    @Test
    public void testGetWeatherPhenomenonFeeThrowsExceptionWhenHailAndVehicleBike() {

        DeliveryManager deliveryManager = new DeliveryManager();

        try {
            deliveryManager.getWeatherPhenomenonFee("hail", DeliveryManager.Vehicle.BIKE);
            fail("Should have thrown the exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN, e.getMessage());
        }
    }

    @Test
    public void testGetWeatherPhenomenonFeeThrowsExceptionWhenThunderAndVehicleBike() {

        DeliveryManager deliveryManager = new DeliveryManager();

        try {
            deliveryManager.getWeatherPhenomenonFee("thunder", DeliveryManager.Vehicle.BIKE);
            fail("Should have thrown the exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN, e.getMessage());
        }
    }

    @Test
    public void testGetWeatherPhenomenonFeeIsOneWhenWeatherRelatedToSnow() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedWeatherPhenomenonFee = 1f;

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light snow shower", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light snow shower", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate snow shower", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate snow shower", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("heavy snow shower", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("heavy snow shower", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light snowfall", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light snowfall", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate snowfall", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate snowfall", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("heavy snowfall", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("heavy snowfall", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("blowing snow", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("blowing snow", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("drifting snow", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("drifting snow", DeliveryManager.Vehicle.SCOOTER));
    }

    @Test
    public void testGetWeatherPhenomenonFeeIsOneWhenWeatherRelatedToSleet() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedWeatherPhenomenonFee = 1f;

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light sleet", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light sleet", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate sleet", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate sleet", DeliveryManager.Vehicle.SCOOTER));
    }

    @Test
    public void testGetWeatherPhenomenonFeeIs05WhenWeatherRelatedToRain() {

        DeliveryManager deliveryManager = new DeliveryManager();

        float expectedWeatherPhenomenonFee = 0.5f;

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light rain", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light rain", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate rain", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate rain", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("heavy rain", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("heavy rain", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light shower", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("light shower", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate shower", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("moderate shower", DeliveryManager.Vehicle.SCOOTER));

        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("heavy shower", DeliveryManager.Vehicle.BIKE));
        assertEquals(expectedWeatherPhenomenonFee, deliveryManager.getWeatherPhenomenonFee
                ("heavy shower", DeliveryManager.Vehicle.SCOOTER));
    }

    @Test
    public void testGetWeatherDataForLocation() {

        DeliveryManager deliveryManager = new DeliveryManager();

        // Update weather data.
        deliveryManager.getWeatherDataManager().updateWeatherData(false);

        WeatherData weatherData = deliveryManager.getWeatherDataForLocation(DeliveryManager.Location.TALLINN);

        assertNotNull(weatherData);

        // Visualize weather data.
        System.out.println(weatherData.toJson());
    }
    @Test
    public void testGetDeliveryFeeWithDifferentParameters() {

        // Set System.out stream to support UTF-8 encoding.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        DeliveryManager deliveryManager = new DeliveryManager();

        // CHANGE HERE TO TEST VARIOUS COMBINATIONS
        DeliveryManager.Location location = DeliveryManager.Location.TALLINN;
        DeliveryManager.Vehicle vehicleType = DeliveryManager.Vehicle.BIKE;

        // Update weather data.
        deliveryManager.getWeatherDataManager().updateWeatherData(false);

        System.out.println(deliveryManager.calculateAndGetDeliveryFee(vehicleType, location));
    }
}
