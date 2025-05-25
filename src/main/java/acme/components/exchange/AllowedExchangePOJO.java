package acme.components.exchange;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllowedExchangePOJO {
	
	@JsonProperty("data")
	private Map<String, Map<String, String>> data;

	@Override
	public String toString() {
		return "ExchangePOJO [data=" + data + "]";
	}
	
}
