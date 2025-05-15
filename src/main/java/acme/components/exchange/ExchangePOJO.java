package acme.components.exchange;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangePOJO {
	
	@JsonProperty("conversion_rates")
	private Map<String, Double> conversionRates;

	@Override
	public String toString() {
		return "ExchangePOJO [conversionRates=" + conversionRates + "]";
	}
	
	
	
}
