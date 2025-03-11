
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import acme.entities.bookings.TravelClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	List<String>				lastFiveDestinations;
	Money						moneySpentLastYear;
	Map<TravelClass, Integer>	numOfBookingsByTravelClass;
	Money						avgCostOfBookingsLastFiveYears;
	Money						minCostOfBookingsLastFiveYears;
	Money						maxCostOfBookingsLastFiveYears;
	Money						stdDeviationCostOfBookingsLastFiveYears;
	Double						avgNumOfPassengersInBookings;
	Integer						minNumOfPassengersInBookings;
	Integer						maxNumOfPassengersInBookings;
	Double						stdDeviationNumOfPassengersInBookings;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
