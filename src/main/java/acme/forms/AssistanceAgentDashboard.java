
package acme.forms;

import java.time.Month;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistanceAgentDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Double						ratioOfClaimsResolved;
	Double						ratioOfClaimsRejected;
	Month						monthHigherNumClaims;
	Double						averageLogsOfClaims;
	Double						minLogsOfClaims;
	Double						maxLogsOfClaims;
	Double						stdDeviationLogsOfClaims;
	Long						numberOfClaimsLastMonth;
	/*
	 * Double averageClaimsAssistedLastMonth;
	 * Integer minClaimsAssistedLastMonth;
	 * Integer maxClaimsAssistedLastMonth;
	 * Double stdDeviationClaimsAssistedLastMonth;
	 */

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}
