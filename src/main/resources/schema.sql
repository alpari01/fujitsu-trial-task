DROP TABLE IF EXISTS WEATHER_DATA;

CREATE TABLE WEATHER_DATA (
  id INT AUTO_INCREMENT,
  station_name VARCHAR(255),
  station_wmo_code VARCHAR(255),
  air_temperature FLOAT,
  wind_speed FLOAT,
  weather_phenomenon VARCHAR(255),
  observation_timestamp VARCHAR(255) NOT NULL
);
