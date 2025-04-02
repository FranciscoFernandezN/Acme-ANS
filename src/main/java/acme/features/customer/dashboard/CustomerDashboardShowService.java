
package acme.features.customer.dashboard;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PropertyHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.forms.CustomerDashboard;
import acme.realms.Customer;

@GuiService
public class CustomerDashboardShowService extends AbstractGuiService<Customer, CustomerDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Customer.class));
	}

	@Override
	public void load() {

		Customer customer = (Customer) super.getRequest().getPrincipal().getRealmOfType(Customer.class);

		int customerId = customer.getId();

		CustomerDashboard dashboard = new CustomerDashboard();
		Collection<Booking> bookings = this.repository.findAllBookings(customerId);

		Comparator<Booking> cmp = Comparator.comparing(Booking::getPurchaseMoment);
		List<String> lastDestinies = bookings.stream().sorted(cmp.thenComparing(Booking::getId)).map(b -> b.getFlight().getDestiny()).toList();
		dashboard.setLastFiveDestinations(lastDestinies);

		Date lastYear = MomentHelper.getCurrentMoment();
		lastYear.setYear(lastYear.getYear() - 1);
		dashboard.setMoneySpentLastYear(this.repository.moneySpentLastYear(lastYear, customerId));

		Map<TravelClass, Integer> numOfBookingsByTravelClass = bookings.stream().collect(Collectors.groupingBy(Booking::getTravelClass, Collectors.summingInt(e -> 1)));
		dashboard.setNumOfBookingsByTravelClass(numOfBookingsByTravelClass);

		Date lastFiveYears = MomentHelper.getCurrentMoment();
		lastYear.setYear(lastYear.getYear() - 5);
		dashboard.setMoneySpentLastYear(this.repository.moneySpentLastYear(lastYear, customerId));
		DoubleSummaryStatistics moneyStats = bookings.stream().filter(e -> e.getPurchaseMoment().after(lastFiveYears)).mapToDouble(e -> e.getPrice().getAmount()).summaryStatistics();
		String defaultCurrency = PropertyHelper.getRequiredProperty("acme.currency.default", String.class);

		Money maxMon = new Money();
		Money minMon = new Money();
		Money avgMon = new Money();
		maxMon.setCurrency(defaultCurrency);
		minMon.setCurrency(defaultCurrency);
		avgMon.setCurrency(defaultCurrency);

		maxMon.setAmount(moneyStats.getMax());
		dashboard.setMaxCostOfBookingsLastFiveYears(maxMon);

		minMon.setAmount(moneyStats.getMin());
		dashboard.setMinCostOfBookingsLastFiveYears(minMon);

		avgMon.setAmount(moneyStats.getAverage());
		dashboard.setAvgCostOfBookingsLastFiveYears(avgMon);

		Double standardDeviation = 0.0;
		for (Booking b : bookings)
			standardDeviation += Math.pow(b.getPrice().getAmount() - avgMon.getAmount(), 2);
		standardDeviation = Math.sqrt(standardDeviation / bookings.size());
		dashboard.setStdDeviationCostOfBookingsLastFiveYears(avgMon);

		dashboard.setMinNumOfPassengersInBookings(1);
		dashboard.setMaxNumOfPassengersInBookings(1);
		dashboard.setAvgNumOfPassengersInBookings(1.);
		dashboard.setStdDeviationNumOfPassengersInBookings(0.);

		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final CustomerDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, "rankingByYearsOfExperience", "yearsToRetire", "ratioOfOnTimeLegs", "ratioOfDelayedLegs", "mostPopularAirportOfFlights", "lessPopularAirportOfFlights", "numberOfOnTimeLegs", "numberOfCancelledLegs",
			"numberOfDelayedLegs", "numberOfLandedLegs", "averageCostOfFlights", "minCostOfFlights", "maxCostOfFlights", "stdDeviationCostOfFlights");

		super.getResponse().addData(dataset);
	}
}
