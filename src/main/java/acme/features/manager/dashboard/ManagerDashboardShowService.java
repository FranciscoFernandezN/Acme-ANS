
package acme.features.manager.dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PropertyHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.supportedcurrency.SupportedCurrency;
import acme.forms.ManagerDashboard;
import acme.realms.Manager;

@GuiService
public class ManagerDashboardShowService extends AbstractGuiService<Manager, ManagerDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerDashboardRepository md;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Manager.class));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void load() {

		Manager manager = (Manager) super.getRequest().getPrincipal().getRealmOfType(Manager.class);

		ManagerDashboard dashboard = new ManagerDashboard();

		dashboard.setRankingByYearsOfExperience(this.md.rankingByYearsOfExperience().indexOf(manager) + 1);

		Date retirementDate = new Date(manager.getBirth().getTime());
		retirementDate.setYear(retirementDate.getYear() + 65);

		Date currentMoment = MomentHelper.getCurrentMoment();
		double milisOfSubstract = Math.abs(retirementDate.getTime() - currentMoment.getTime());
		double division = 1000. * 60. * 60. * 24. * 30 * 12.;
		Double yearsToRetire = milisOfSubstract / division;

		dashboard.setYearsToRetire(yearsToRetire <= 0. ? 0. : yearsToRetire);
		dashboard.setRatioOfOnTimeLegs(this.md.ratioOfOnTimeLegs(manager.getId()));
		dashboard.setRatioOfDelayedLegs(this.md.ratioOfDelayedLegs(manager.getId()));
		
		dashboard.setRatioOfDelayedLegs(dashboard.getRatioOfDelayedLegs() == null ? 0: dashboard.getRatioOfDelayedLegs());
		dashboard.setRatioOfOnTimeLegs(dashboard.getRatioOfOnTimeLegs() == null ? 0: dashboard.getRatioOfOnTimeLegs());
		
		String defaultCurrency = SupportedCurrency.getDefaultCurrency();
		Airport mostPopularAirport;
		Airport lessPopularAirport;
		Money average = new Money();
		Money min = new Money();
		Money max = new Money();
		Double standardDeviation = 0.0;
		average.setCurrency(defaultCurrency);
		min.setCurrency(defaultCurrency);
		max.setCurrency(defaultCurrency);
		
		List<Flight> allFlightsOfManager = this.md.findAllFlightsByManagerId(manager.getId());
		
		if(!allFlightsOfManager.isEmpty()) {
			List<Airport> originAirports = allFlightsOfManager.stream().filter(f -> f.getOriginAirport() != null).map(Flight::getOriginAirport).collect(Collectors.toCollection(ArrayList::new));
			List<Airport> destinyAirports = allFlightsOfManager.stream().filter(f -> f.getDestinyAirport() != null).map(Flight::getDestinyAirport).toList();
			originAirports.addAll(destinyAirports);
			mostPopularAirport = originAirports.stream().collect(Collectors.collectingAndThen(Collectors.groupingBy(Function.identity(), Collectors.counting()), m -> m.entrySet().stream().max(Comparator.comparing(e -> e.getValue())).get())).getKey();
			lessPopularAirport = originAirports.stream().collect(Collectors.collectingAndThen(Collectors.groupingBy(Function.identity(), Collectors.counting()), m -> m.entrySet().stream().min(Comparator.comparing(e -> e.getValue())).get())).getKey();
			
			DoubleSummaryStatistics statistics = allFlightsOfManager.stream().mapToDouble(f -> {
				return f.getCost().getCurrency().equals(defaultCurrency) ? f.getCost().getAmount() : SupportedCurrency.convertToDefault(f.getCost()).getAmount();
			}).summaryStatistics();
			

			average.setAmount(statistics.getAverage());
			min.setAmount(statistics.getMin());
			max.setAmount(statistics.getMax());

			
			for (Flight f : allFlightsOfManager)
				standardDeviation += Math.pow(f.getCost().getAmount() - average.getAmount(), 2);

			standardDeviation = Math.sqrt(standardDeviation / allFlightsOfManager.size());
			
		} else {
			Airport dummyAirport = new Airport();
			dummyAirport.setIATACode("N/A");
			mostPopularAirport = dummyAirport;
			lessPopularAirport = dummyAirport;
			average.setAmount(0.);
			max.setAmount(0.);
			min.setAmount(0.);
		}
		
		dashboard.setMostPopularAirportOfFlights(mostPopularAirport.getIATACode());
		dashboard.setLessPopularAirportOfFlights(lessPopularAirport.getIATACode());
		dashboard.setNumberOfOnTimeLegs(this.md.numberOfOnTimeLegs(manager.getId()));
		dashboard.setNumberOfCancelledLegs(this.md.numberOfCancelledLegs(manager.getId()));
		dashboard.setNumberOfDelayedLegs(this.md.numberOfDelayedLegs(manager.getId()));
		dashboard.setNumberOfLandedLegs(this.md.numberOfLandedLegs(manager.getId()));
		dashboard.setAverageCostOfFlights(average);
		dashboard.setMinCostOfFlights(min);
		dashboard.setMaxCostOfFlights(max);
		dashboard.setStdDeviationCostOfFlights(standardDeviation);

		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final ManagerDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, "rankingByYearsOfExperience", "yearsToRetire", "ratioOfOnTimeLegs", "ratioOfDelayedLegs", "mostPopularAirportOfFlights", "lessPopularAirportOfFlights", "numberOfOnTimeLegs", "numberOfCancelledLegs",
			"numberOfDelayedLegs", "numberOfLandedLegs", "averageCostOfFlights", "minCostOfFlights", "maxCostOfFlights", "stdDeviationCostOfFlights");

		super.getResponse().addData(dataset);
	}
}
