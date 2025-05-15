
package acme.entities.recommendations;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.components.recommendation.RecommendationPOJO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Recommendation extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Optional
	@ValidString(max = 50)
	@Column(unique = true)
	private String				name;

	@Optional
	@ValidString(max = 50)
	@Automapped
	private String				city;

	@Optional
	@Automapped
	private BusinessStatus		businessStatus;

	@Optional
	@ValidString(max = 50)
	@Automapped
	private String				formattedAddress;

	@Optional
	@ValidNumber(min = 0., max = 5.)
	@Automapped
	private Double				rating;

	@Optional
	@ValidNumber(min = 0, max = 999999)
	@Automapped
	private Integer				userRatingsTotal;

	@Optional
	@Automapped
	private Boolean				openNow;

	@Optional
	@ValidUrl
	@Column(length = 1000)
	private String				photoReference;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	// Ancillary methods ------------------------------------------------------


	@SuppressWarnings("resource")
	public static Recommendation of(final RecommendationPOJO r, final String city) {
		Recommendation recommendation = new Recommendation();
		recommendation.setCity(city);
		recommendation.setName(r.getName() != null ? r.getName() : "N/A");
		recommendation.setBusinessStatus(r.getBusinessStatus() != null ? r.getBusinessStatus() : BusinessStatus.CLOSED_TEMPORARILY);
		recommendation.setFormattedAddress(r.getFormattedAddress() != null ? r.getFormattedAddress() : "N/A");
		recommendation.setRating(r.getRating() != null ? r.getRating() : 0.);
		recommendation.setUserRatingsTotal(r.getUserRatingsTotal() != null ? r.getUserRatingsTotal() : 0);
		if (r.getOpeningHours() != null && r.getOpeningHours().getOpenNow() != null)
			recommendation.setOpenNow(r.getOpeningHours().getOpenNow());
		else
			recommendation.setOpenNow(false);
		String noUbication = "https://www.shutterstock.com/image-vector/unavailable-photo-iconmissingimage-vector-illustration-260nw-1835554645.jpg";
		if (r.getPhotos() != null && r.getPhotos().get(0) != null && r.getPhotos().get(0).getPhotoReference() != null) {
			String apiKey = "AIzaSyCntaK3qwLvyueF-6yya9n8yW-ykmUpeQI";
			String photoReference = r.getPhotos().get(0).getPhotoReference();
			String ubication = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=" + apiKey;
			recommendation.setPhotoReference(ubication);
		} else
			recommendation.setPhotoReference(noUbication);
		return recommendation;
	}

}
