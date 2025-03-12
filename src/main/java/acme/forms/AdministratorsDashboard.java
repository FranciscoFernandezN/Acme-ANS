
package acme.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.airlines.AirlineType;
import acme.entities.airports.OperationalScope;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorsDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Map<OperationalScope, Integer>	totalNumAirportsByOperationalScope;
	Map<AirlineType, Integer>		numAirlinesByType;
	Double							ratioAirlinesWithEmailAndPhone;
	Double							ratioActiveInactiveAircrafts;
	Double							ratioReviewsWithScoreHigher5;
	Integer							countReviewsPostedLast10Weeks;
	Double							averageReviewsPostedLast10Weeks;
	Integer							minReviewsPostedLast10Weeks;
	Integer							maxReviewsPostedLast10Weeks;
	Double							stdDeviationReviewsPostedLast10Weeks;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
