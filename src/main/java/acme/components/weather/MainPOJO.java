package acme.components.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainPOJO {
	
	@JsonProperty("temp")
	private Double temp;
}
