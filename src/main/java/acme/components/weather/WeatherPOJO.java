package acme.components.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherPOJO {
	
	@JsonProperty("main")
	private MainPOJO main;
	
	@JsonProperty("visibility")
	private Integer visibility;
	
	@JsonProperty("wind")
	private WindPOJO wind;
	
	@JsonProperty("rain")
	private RainPOJO rain;
	
	@JsonProperty("snow")
	private SnowPOJO snow;

	@Override
	public String toString() {
		String result = "Weather{";
		if(main != null) {
			result += "temperature=" + main.getTemp() + ",";
		}
		if(visibility != null) {
			result += "visibility=" + visibility + ",";
		}
		if(wind != null) {
			result += "wind=" + wind.getSpeed() + ",";
		}
		if(rain != null) {
			result += "rain=" + rain.getValue() + ",";
		}
		if(snow != null) {
			result += "snow=" + snow.getValue() + ",";
		}
		return result + "}";
		
	}
	
	
}
