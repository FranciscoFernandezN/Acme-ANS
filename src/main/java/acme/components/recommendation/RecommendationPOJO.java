
package acme.components.recommendation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import acme.entities.recommendations.BusinessStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationPOJO {

	@JsonProperty("name")
	private String				name;

	@JsonProperty("business_status")
	private BusinessStatus		businessStatus;

	@JsonProperty("formatted_address")
	private String				formattedAddress;

	@JsonProperty("opening_hours")
	private OpeningHoursPOJO	openingHours;

	@JsonProperty("photos")
	private List<PhotoPOJO>		photos;

	@JsonProperty("rating")
	private Double				rating;

	@JsonProperty("types")
	private List<String>		types;

	@JsonProperty("user_ratings_total")
	private Integer				userRatingsTotal;

}
