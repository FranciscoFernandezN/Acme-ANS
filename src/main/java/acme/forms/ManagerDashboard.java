
package acme.forms;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Integer						rankingByYearsOfExperience;
	Double						yearsToRetire;
	Double						ratioOfOnTimeLegs;
	Double						ratioOfDelayedLegs;
	String						mostPopularAirportOfFlights;
	String						lessPopularAirportOfFlights;
	Long						numberOfOnTimeLegs;
	Long						numberOfCancelledLegs;
	Long						numberOfDelayedLegs;
	Long						numberOfLandedLegs;
	Money						averageCostOfFlights;
	Money						minCostOfFlights;
	Money						maxCostOfFlights;
	Double						stdDeviationCostOfFlights;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
