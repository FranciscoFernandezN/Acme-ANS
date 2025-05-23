
package acme.forms;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	String						totalNumAirportsByOperationalScope;
	String						numAirlinesByType;
	Double						ratioAirlinesWithEmailAndPhone;
	Double						ratioActiveAircrafts;
	Double						ratioInactiveAircrafts;
	Double						ratioReviewsWithScoreHigher5;
	Integer						countReviewsPostedLast10Weeks;
	Double						averageReviewsPostedLast10Weeks;
	Integer						minReviewsPostedLast10Weeks;
	Integer						maxReviewsPostedLast10Weeks;
	Double						stdDeviationReviewsPostedLast10Weeks;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
