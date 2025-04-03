package acme.components.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RainPOJO {
	
	@JsonProperty("1h")
	private Double value;
	
}
