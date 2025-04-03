
package acme.features.assistanceAgent.dashboard;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.forms.AssistanceAgentDashboard;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentDashboardShowService extends AbstractGuiService<AssistanceAgent, AssistanceAgentDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentDashboardRepository aad;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class));
	}

	@Override
	public void load() {

		AssistanceAgent AssistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class);

		AssistanceAgentDashboard dashboard = new AssistanceAgentDashboard();

		dashboard.setRatioOfClaimsResolved(this.aad.ratioOfClaimsResolved(AssistanceAgent.getId()));
		dashboard.setRatioOfClaimsRejected(this.aad.ratioOfClaimsRejected(AssistanceAgent.getId()));

		List<Claim> allClaimsOfAssistanceAgent = this.aad.findAllClaimsByAgentId(AssistanceAgent.getId());

		List<Date> allRegistrationMoments = this.aad.findAllRegistrationMomentsByAgentId(AssistanceAgent.getId());
		List<Integer> monthsAppeareance = new ArrayList<>();

		for (int i = 1; i < 13; i++)
			monthsAppeareance.add(0);

		for (Date d : allRegistrationMoments)
			monthsAppeareance.set(d.getMonth(), monthsAppeareance.get(d.getMonth()) + 1);

		Integer maxClaimsMonth = Collections.max(monthsAppeareance);

		DoubleSummaryStatistics logsStatistics = allClaimsOfAssistanceAgent.stream().mapToDouble(c -> this.aad.logsOfClaim(c.getId()).size()).summaryStatistics();
		Double logsAverage;
		Double logsMin;
		Double logsMax;

		logsAverage = logsStatistics.getAverage();
		logsMin = logsStatistics.getMin();
		logsMax = logsStatistics.getMax();

		Double logsStandardDeviation = 0.0;
		for (Claim c : allClaimsOfAssistanceAgent)
			logsStandardDeviation += Math.pow(this.aad.logsOfClaim(c.getId()).size() - logsAverage, 2);

		logsStandardDeviation = Math.sqrt(logsStandardDeviation / allClaimsOfAssistanceAgent.size());

		Long numberOfClaimsLastMonth = allClaimsOfAssistanceAgent.stream().filter(c -> c.getRegistrationMoment().getMonth() == MomentHelper.getBaseMoment().getMonth() - 1).count();

		dashboard.setMonthHigherNumClaims(Month.of(monthsAppeareance.indexOf(maxClaimsMonth) + 1));
		dashboard.setAverageLogsOfClaims(logsAverage);
		dashboard.setMinLogsOfClaims(logsMin);
		dashboard.setMaxLogsOfClaims(logsMax);
		dashboard.setStdDeviationLogsOfClaims(logsStandardDeviation);
		dashboard.setNumberOfClaimsLastMonth(numberOfClaimsLastMonth);

		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final AssistanceAgentDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, "ratioOfClaimsResolved", "ratioOfClaimsRejected", "monthHigherNumClaims", "averageLogsOfClaims", "minLogsOfClaims", "maxLogsOfClaims", "stdDeviationLogsOfClaims", "numberOfClaimsLastMonth");

		super.getResponse().addData(dataset);
	}
}
