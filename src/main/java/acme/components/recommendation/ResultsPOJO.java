
package acme.components.recommendation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultsPOJO {

	@JsonProperty("results")
	private List<RecommendationPOJO> recommendations;

}
