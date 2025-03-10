
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistAgentsDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Double						ratioOfClaimsResolved;
	Double						ratioOfClaimsRejected;
	List<Integer>				top3MonthsHigherNumClaims;
	Double						averageLogsOfClaims;
	Double						minLogsOfClaims;
	Double						maxLogsOfClaims;
	Double						stdDeviationLogsOfClaims;
	Double						averageClaimsAssistedLastMonth;
	Double						minClaimsAssistedLastMonth;
	Double						maxClaimsAssistedLastMonth;
	Double						stdDeviationClaimsAssistedLastMonth;
	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}
