package dev.alpari.fujitsutrialtask.delivery;

import dev.alpari.fujitsutrialtask.database.WeatherDataManager;
import dev.alpari.fujitsutrialtask.model.WeatherData;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Getter
public class DeliveryManager {

    private final String EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN = "Usage of selected vehicle type is forbidden";

    private final WeatherDataManager weatherDataManager = new WeatherDataManager();

    private final Map<String, Set<String>> weatherPhenomenonsDictionary = new HashMap<>();
    private final Map<String, Float> regionalBaseFees = new HashMap<>();

    public enum Vehicle {
        CAR,
        SCOOTER,
        BIKE
    }

    public enum Location {
        TALLINN,
        TARTU,
        PÄRNU
    }

    public enum Weather {
        SNOW,
        SLEET,
        RAIN,
        GLAZE,
        HAIL,
        THUNDER
    }

    /**
     * Constructor.
     */
    public DeliveryManager() {

        // Setup regional based fees.
        setRegionalBaseFee(Vehicle.CAR, Location.TALLINN, 4.0f);
        setRegionalBaseFee(Vehicle.SCOOTER, Location.TALLINN, 3.5f);
        setRegionalBaseFee(Vehicle.BIKE, Location.TALLINN, 3.0f);

        setRegionalBaseFee(Vehicle.CAR, Location.TARTU, 3.5f);
        setRegionalBaseFee(Vehicle.SCOOTER, Location.TARTU, 3.0f);
        setRegionalBaseFee(Vehicle.BIKE, Location.TARTU, 2.5f);

        setRegionalBaseFee(Vehicle.CAR, Location.PÄRNU, 3.0f);
        setRegionalBaseFee(Vehicle.SCOOTER, Location.PÄRNU, 2.5f);
        setRegionalBaseFee(Vehicle.BIKE, Location.PÄRNU, 2.0f);

        // Setup weather phenomenons.
        weatherPhenomenonsDictionary.put("rain", Set.of("light rain", "moderate rain", "heavy rain", "light shower",
                "moderate shower", "heavy shower"));
        weatherPhenomenonsDictionary.put("snow", Set.of("light snow shower", "moderate snow shower", "heavy snow shower",
                "light snowfall", "moderate snowfall", "heavy snowfall", "blowing snow", "drifting snow"));
        weatherPhenomenonsDictionary.put("sleet", Set.of("light sleet", "moderate sleet"));
    }

    /**
     * Calculate total delivery fee based on vehicle type and location.
     *
     * Total delivery fee = RBF + ATEF + WSEF + WPEF
     *
     * @param vehicleType vehicle type
     * @param location location name
     * @return all information about delivery fee.
     */
    public String calculateAndGetDeliveryFee(Vehicle vehicleType, Location location) {

        WeatherData locationWeatherData = getWeatherDataForLocation(location);

        float windSpeedFee;
        try {
            windSpeedFee = getWindSpeedFee(locationWeatherData.getWindSpeed(), vehicleType);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }

        float weatherPhenomenonFee;
        try {
            weatherPhenomenonFee = getWeatherPhenomenonFee(locationWeatherData.getWeatherPhenomenon(), vehicleType);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }

        float regionalBaseFee = getRegionalBaseFee(vehicleType, location);
        float airTemperatureFee = getAirTemperatureFee(locationWeatherData.getAirTemperature(), vehicleType);
        float totalDeliveryFee = regionalBaseFee + airTemperatureFee + windSpeedFee + weatherPhenomenonFee;

        return "Input parameters: " + location.toString().toUpperCase() + " and "
                + vehicleType.toString().toUpperCase() + " -> RBF = " + regionalBaseFee + " €\n"
                + "Latest weather data for " + location + " (" + locationWeatherData.getStationName() + "):\n"
                + " Air temperature = " + locationWeatherData.getAirTemperature() + "°C -> ATEF = " + airTemperatureFee + " €\n"
                + " Wind speed = " + locationWeatherData.getWindSpeed() + " m/s -> WSEF = " + windSpeedFee + " €\n"
                + " Weather phenomenon = " + locationWeatherData.getWeatherPhenomenon() + " -> WPEF = " + weatherPhenomenonFee + " €\n"
                + "Total delivery fee = RBF + ATEF + WSEF + WPEF = "
                + regionalBaseFee + " + " + airTemperatureFee + " + " + windSpeedFee + " + " + weatherPhenomenonFee + " + = "
                + totalDeliveryFee + " €";
    }

    /**
     * Get the latest weather data for specified location.
     *
     * @param location location name
     * @return latest weather data for location
     */
    public WeatherData getWeatherDataForLocation(Location location) {
        return weatherDataManager.getLatestWeatherDataForLocation(location.toString());
    }

    /**
     * Get weather phenomenon fee (WPEF).
     *
     * @param locationWeatherPhenomenon weather phenomenon at the location.
     * @param vehicleType vehicle type.
     * @return WPEF value (€).
     */
    public float getWeatherPhenomenonFee(String locationWeatherPhenomenon, Vehicle vehicleType) {

        if (vehicleType.equals(Vehicle.SCOOTER) || vehicleType.equals(Vehicle.BIKE)) {

            locationWeatherPhenomenon = locationWeatherPhenomenon.toLowerCase();

            if (locationWeatherPhenomenon.equals(Weather.GLAZE.toString().toLowerCase())
                    || locationWeatherPhenomenon.equals(Weather.HAIL.toString().toLowerCase())
                    || locationWeatherPhenomenon.equals(Weather.THUNDER.toString().toLowerCase())) {
                throw new IllegalArgumentException(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN);
            }

            String weatherType = null;
            for (Map.Entry<String, Set<String>> entry : weatherPhenomenonsDictionary.entrySet()) {
                // Find type of the weather.

                if (entry.getValue().contains(locationWeatherPhenomenon)) {
                    weatherType = entry.getKey();
                    break;
                }
            }

            if (weatherType == null) return 0f;

            if (weatherType.equals(Weather.SNOW.toString().toLowerCase())
                    || weatherType.equals(Weather.SLEET.toString().toLowerCase())) {
                return 1f;
            }

            if (weatherType.equals(Weather.RAIN.toString().toLowerCase())) return 0.5f;
        }

        return 0f;
    }

    /**
     * Get air temperature fee (ATEF).
     *
     * @param locationAirTemperature air temperature at the location.
     * @param vehicleType vehicle type.
     * @return ATEF value (€).
     */
    public float getAirTemperatureFee(float locationAirTemperature, Vehicle vehicleType) {

        if (vehicleType.equals(Vehicle.SCOOTER) || vehicleType.equals(Vehicle.BIKE)) {
            if (locationAirTemperature < -10.0f) return 1.0f;
            if (locationAirTemperature >= -10.f && locationAirTemperature <= 0f) return 0.5f;
        }
        return 0f;
    }

    /**
     * Get wind speed fee (WSEF).
     *
     * @param locationWindSpeed wind speed at the location.
     * @param vehicleType vihicle type.
     * @return WSEF value.
     */
    public float getWindSpeedFee(float locationWindSpeed, Vehicle vehicleType) {

        if (vehicleType.equals(Vehicle.BIKE)) {

            if (locationWindSpeed > 20.f) throw new IllegalArgumentException(EXCEPTION_MESSAGE_VEHICLE_USAGE_FORBIDDEN);

            if (locationWindSpeed >= 10.f) return 0.5f;
        }

        return 0f;
    }

    /**
     * Set regional fee based on vehicle type and location (RBF).
     *
     * @param vehicleType vehicle type.
     * @param location location.
     * @param fee RBF value (€).
     */
    public void setRegionalBaseFee(Vehicle vehicleType, Location location, float fee) {
        regionalBaseFees.put(vehicleType + "," + location, fee);
    }

    /**
     * Get regional base fee for specified region and vehicle type (RBF).
     *
     * @param vehicleType vehicle type.
     * @param location location name.
     * @return RBF value (€).
     */
    public float getRegionalBaseFee(Vehicle vehicleType, Location location) {

        if (!regionalBaseFees.containsKey(vehicleType + "," + location))
            throw new NoSuchElementException("No data for these regional base fee parameters.");

        return regionalBaseFees.get(vehicleType + "," + location);
    }
}
