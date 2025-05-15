
package acme.components.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpeningHoursPOJO {

	@JsonProperty("open_now")
	private Boolean openNow;

}
