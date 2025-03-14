
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.airports.Airport;
import acme.entities.legs.LegStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Integer						rankingByYearsOfExperience;
	Integer						yearsToRetire;
	Double						ratioOfOnTimeLegs;
	Double						ratioOfDelayedLegs;
	List<Airport>				mostPopularAirportsOfFlights;
	List<Airport>				lessPopularAirportsOfFlights;
	Map<LegStatus, Integer>		numberOfLegsByItsStatus;
	Double						averageCostOfFlights;
	Double						minCostOfFlights;
	Double						maxCostOfFlights;
	Double						stdDeviationCostOfFlights;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
