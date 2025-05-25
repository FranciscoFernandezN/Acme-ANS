
package acme.components.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoPOJO {

	@JsonProperty("photo_reference")
	private String photoReference;

}
