
package acme.forms;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	String						lastFiveDestinations;
	Money						moneySpentLastYear;
	String						numOfBookingsByTravelClass;
	Money						avgCostOfBookingsLastFiveYears;
	Money						minCostOfBookingsLastFiveYears;
	Money						maxCostOfBookingsLastFiveYears;
	Double						stdDeviationCostOfBookingsLastFiveYears;
	Double						avgNumOfPassengersInBookings;
	Integer						minNumOfPassengersInBookings;
	Integer						maxNumOfPassengersInBookings;
	Double						stdDeviationNumOfPassengersInBookings;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
