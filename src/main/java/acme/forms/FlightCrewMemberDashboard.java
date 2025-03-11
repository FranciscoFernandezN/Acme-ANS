
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.flights.CurrentStatus;
import acme.entities.flights.FlightAssignment;
import acme.realms.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long				serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	List<String>							lastFiveDestinations;
	Map<String, Integer>					incidentSeverityCount;
	List<FlightCrewMember>					lastLegCrewMembers;
	Map<CurrentStatus, FlightAssignment>	flightAssignmentByStatus;
	Double									flightAssignmentsLastMonth;
	Double									minFlightAssignmentsLastMonth;
	Double									maxFlightAssignmentsLastMonth;
	Double									stdDesviationFlightAssignmentsLastMonth;
}
