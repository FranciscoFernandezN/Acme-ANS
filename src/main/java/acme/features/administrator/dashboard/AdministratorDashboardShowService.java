
package acme.features.administrator.dashboard;

import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.AirlineType;
import acme.entities.airports.OperationalScope;
import acme.forms.AdministratorDashboard;

@GuiService
public class AdministratorDashboardShowService extends AbstractGuiService<Administrator, AdministratorDashboard> {

	@Autowired
	protected AdministratorDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		AdministratorDashboard dashboard = new AdministratorDashboard();

		// --- Count Airports by OperationalScope (initialize with all enum values)
		List<Object[]> airportsCountList = this.repository.countAirportsByOperationalScope();
		Map<OperationalScope, Integer> airportsCountMap = new HashMap<>();
		for (OperationalScope scope : OperationalScope.values())
			airportsCountMap.put(scope, 0);
		for (Object[] row : airportsCountList) {
			OperationalScope scope = (OperationalScope) row[0];
			Long count = (Long) row[1];
			airportsCountMap.put(scope, count != null ? count.intValue() : 0);
		}
		String airportsCountAsString = airportsCountMap.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", "));
		dashboard.setTotalNumAirportsByOperationalScope(airportsCountAsString);

		// --- Count Airlines by AirlineType (initialize with all enum values)
		List<Object[]> airlinesCountList = this.repository.countAirlinesByType();
		Map<AirlineType, Integer> airlinesCountMap = new HashMap<>();
		for (AirlineType type : AirlineType.values())
			airlinesCountMap.put(type, 0);
		for (Object[] row : airlinesCountList) {
			AirlineType type = (AirlineType) row[0];
			Long count = (Long) row[1];
			airlinesCountMap.put(type, count != null ? count.intValue() : 0);
		}
		String airlinesCountAsString = airlinesCountMap.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", "));
		dashboard.setNumAirlinesByType(airlinesCountAsString);

		// --- Ratio airlines with email & phone
		Integer countAirlinesWithContact = this.repository.countAirlinesWithEmailAndPhone();
		Integer totalAirlines = this.repository.countAllAirlines();
		Double ratioAirlinesWithContact = totalAirlines == null || totalAirlines == 0 ? 0.0 : (countAirlinesWithContact != null ? countAirlinesWithContact.doubleValue() : 0.0) / totalAirlines.doubleValue();
		dashboard.setRatioAirlinesWithEmailAndPhone(ratioAirlinesWithContact);

		// --- Ratios active / inactive aircrafts
		Integer enabledAircrafts = this.repository.countEnabledAircrafts();
		Integer totalAircrafts = this.repository.countAllAircrafts();
		Double ratioActiveAircrafts = totalAircrafts == null || totalAircrafts == 0 ? 0.0 : (enabledAircrafts != null ? enabledAircrafts.doubleValue() : 0.0) / totalAircrafts.doubleValue();
		Double ratioInactiveAircrafts = totalAircrafts == null || totalAircrafts == 0 ? 0.0 : (totalAircrafts.doubleValue() - (enabledAircrafts != null ? enabledAircrafts.doubleValue() : 0.0)) / totalAircrafts.doubleValue();
		dashboard.setRatioActiveAircrafts(ratioActiveAircrafts);
		dashboard.setRatioInactiveAircrafts(ratioInactiveAircrafts);

		// --- Ratio reviews with score > 5
		Integer countReviewsAboveFive = this.repository.countReviewsWithScoreAboveFive();
		Integer totalReviews = this.repository.countAllReviews();
		double ratioReviewsAboveFive = totalReviews == null || totalReviews == 0 ? 0.0 : (countReviewsAboveFive != null ? countReviewsAboveFive.doubleValue() : 0.0) / totalReviews.doubleValue();
		dashboard.setRatioReviewsWithScoreHigher5(ratioReviewsAboveFive);

		// --- Reviews posted last 10 weeks stats
		Date tenWeeksAgo = MomentHelper.getCurrentMoment();
		tenWeeksAgo.setTime(tenWeeksAgo.getTime() - 70L * 24L * 60L * 60L * 1000L);

		Integer countReviewsLast10Weeks = this.repository.countReviewsPostedSince(tenWeeksAgo);
		dashboard.setCountReviewsPostedLast10Weeks(countReviewsLast10Weeks != null ? countReviewsLast10Weeks : 0);

		List<Object[]> reviewsPerWeek = this.repository.countReviewsPerWeekSince(tenWeeksAgo);
		List<Integer> countsPerWeek = reviewsPerWeek.stream().map(row -> ((Long) row[1]).intValue()).collect(Collectors.toList());

		if (countsPerWeek.isEmpty()) {
			dashboard.setAverageReviewsPostedLast10Weeks(0.0);
			dashboard.setMinReviewsPostedLast10Weeks(0);
			dashboard.setMaxReviewsPostedLast10Weeks(0);
			dashboard.setStdDeviationReviewsPostedLast10Weeks(0.0);
		} else {
			DoubleSummaryStatistics stats = countsPerWeek.stream().mapToDouble(Integer::doubleValue).summaryStatistics();
			dashboard.setAverageReviewsPostedLast10Weeks(stats.getAverage());
			dashboard.setMinReviewsPostedLast10Weeks((int) stats.getMin());
			dashboard.setMaxReviewsPostedLast10Weeks((int) stats.getMax());

			double avg = stats.getAverage();
			double variance = countsPerWeek.stream().mapToDouble(c -> Math.pow(c - avg, 2)).average().orElse(0.0);
			double stddev = Math.sqrt(variance);
			dashboard.setStdDeviationReviewsPostedLast10Weeks(stddev);
		}

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final AdministratorDashboard dashboard) {
		super.getResponse().addData(super.unbindObject(dashboard, "totalNumAirportsByOperationalScope", "numAirlinesByType", "ratioAirlinesWithEmailAndPhone", "ratioActiveAircrafts", "ratioInactiveAircrafts", "ratioReviewsWithScoreHigher5",
			"countReviewsPostedLast10Weeks", "averageReviewsPostedLast10Weeks", "minReviewsPostedLast10Weeks", "maxReviewsPostedLast10Weeks", "stdDeviationReviewsPostedLast10Weeks"));
	}
}
