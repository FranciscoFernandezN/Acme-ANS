
package acme.components.exchange;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangePOJO {

	@JsonProperty("data")
	private Map<String, Map<String, Double>> data;


	@Override
	public String toString() {
		return "ExchangePOJO [data=" + this.data + "]";
	}

}
