
package acme.forms;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	String						lastFiveDestinations;
	String						incidentSeverityCount;
	String						lastLegCrewMembers;
	String						flightAssignmentByStatus;
	Double						flightAssignmentsLastMonth;
	Integer						minFlightAssignmentsLastMonth;
	Integer						maxFlightAssignmentsLastMonth;
	Double						stdDesviationFlightAssignmentsLastMonth;
}
