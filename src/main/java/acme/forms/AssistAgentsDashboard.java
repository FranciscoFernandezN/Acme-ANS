
package acme.forms;

import java.util.List;
import java.time.Month;

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
	List<Month>					top3MonthsHigherNumClaims;
	Double						averageLogsOfClaims;
	Integer						minLogsOfClaims;
	Integer						maxLogsOfClaims;
	Double						stdDeviationLogsOfClaims;
	Double						averageClaimsAssistedLastMonth;
	Integer						minClaimsAssistedLastMonth;
	Integer						maxClaimsAssistedLastMonth;
	Double						stdDeviationClaimsAssistedLastMonth;
	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}
