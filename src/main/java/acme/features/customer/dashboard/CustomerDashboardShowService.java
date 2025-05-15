
package acme.features.customer.dashboard;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
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
import acme.entities.supportedcurrency.SupportedCurrency;
import acme.forms.CustomerDashboard;
import acme.realms.Customer;

@SuppressWarnings("deprecation")
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
		Integer numBookings = bookings.size();
		String defaultCurrency = SupportedCurrency.getDefaultCurrency();
		Money noMoney = new Money();
		noMoney.setCurrency(defaultCurrency);
		noMoney.setAmount(0.0);

		if (numBookings == 0) {
			dashboard.setLastFiveDestinations("N/A");
			dashboard.setMoneySpentLastYear(noMoney);
			dashboard.setNumOfBookingsByTravelClass("N/A");
			dashboard.setMaxCostOfBookingsLastFiveYears(noMoney);
			dashboard.setMinCostOfBookingsLastFiveYears(noMoney);
			dashboard.setAvgCostOfBookingsLastFiveYears(noMoney);
			dashboard.setStdDeviationCostOfBookingsLastFiveYears(0.0);
			dashboard.setMinNumOfPassengersInBookings(0);
			dashboard.setMaxNumOfPassengersInBookings(0);
			dashboard.setAvgNumOfPassengersInBookings(0.0);
			dashboard.setStdDeviationNumOfPassengersInBookings(0.0);
		} else {
			Comparator<Booking> cmp = Comparator.comparing(Booking::getPurchaseMoment);
			List<String> lastDestiniesList = bookings.stream().sorted(cmp.thenComparing(Booking::getId)).map(b -> b.getFlight().getDestiny()).distinct().limit(5L).toList();
			String lastDestinies = "";
			for (String d : lastDestiniesList) {
				int pos = lastDestiniesList.indexOf(d) + 1;
				lastDestinies += pos + ": " + d + ", ";
			}
			lastDestinies = lastDestinies.substring(0, lastDestinies.length() - 2);

			dashboard.setLastFiveDestinations(lastDestinies);

			Date lastYear = MomentHelper.getCurrentMoment();
			lastYear.setYear(lastYear.getYear() - 1);
			Money spentLastYear = new Money();
			spentLastYear.setCurrency(defaultCurrency);
			Double moneySpent = this.repository.moneySpentLastYear(lastYear, customerId);
			spentLastYear.setAmount(moneySpent != null ? moneySpent : 0.0);
			dashboard.setMoneySpentLastYear(spentLastYear);

			Map<TravelClass, Integer> numOfBookingsByTravelClassMap = bookings.stream().collect(Collectors.groupingBy(Booking::getTravelClass, Collectors.summingInt(e -> 1)));
			String numOfBookingsByTravelClass = "";
			for (TravelClass k : numOfBookingsByTravelClassMap.keySet())
				numOfBookingsByTravelClass += k + ": " + numOfBookingsByTravelClassMap.get(k) + ", ";
			numOfBookingsByTravelClass = numOfBookingsByTravelClass.substring(0, numOfBookingsByTravelClass.length() - 2);

			dashboard.setNumOfBookingsByTravelClass(numOfBookingsByTravelClass);

			Date lastFiveYears = MomentHelper.getCurrentMoment();
			lastFiveYears.setYear(lastYear.getYear() - 5);

			List<Booking> bookingsAfterYear = bookings.stream().filter(e -> e.getPurchaseMoment().after(lastFiveYears)).toList();
			Integer numBookingsAfterYear = bookingsAfterYear.size();

			Money maxMon = new Money();
			Money minMon = new Money();
			Money avgMon = new Money();
			maxMon.setCurrency(defaultCurrency);
			minMon.setCurrency(defaultCurrency);
			avgMon.setCurrency(defaultCurrency);

			Double standardDeviationBookings = 0.0;
			if (numBookingsAfterYear > 0) {
				DoubleSummaryStatistics moneyStats = bookingsAfterYear.stream().mapToDouble(e -> {
					return e.getPrice().getCurrency().equals(defaultCurrency) ? e.getPrice().getAmount() : SupportedCurrency.convertToDefault(e.getPrice()).getAmount();
				}).summaryStatistics();
				maxMon.setAmount(moneyStats.getMax());
				minMon.setAmount(moneyStats.getMin());
				Double avgMonVal = moneyStats.getAverage();
				avgMon.setAmount(avgMonVal);
				for (Booking b : bookingsAfterYear)
					standardDeviationBookings += Math.pow(b.getPrice().getAmount() - avgMonVal, 2);
				standardDeviationBookings = Math.sqrt(standardDeviationBookings / numBookingsAfterYear);
			} else {
				maxMon.setAmount(0.0);
				minMon.setAmount(0.0);
				avgMon.setAmount(0.0);
			}

			dashboard.setMaxCostOfBookingsLastFiveYears(maxMon);
			dashboard.setMinCostOfBookingsLastFiveYears(minMon);
			dashboard.setAvgCostOfBookingsLastFiveYears(avgMon);
			dashboard.setStdDeviationCostOfBookingsLastFiveYears(standardDeviationBookings);

			IntSummaryStatistics passengerStats = bookings.stream().mapToInt(e -> this.repository.findPassengersByBookingId(e.getId()).size()).summaryStatistics();

			dashboard.setMinNumOfPassengersInBookings(passengerStats.getMin());
			dashboard.setMaxNumOfPassengersInBookings(passengerStats.getMax());
			Double avgPas = passengerStats.getAverage();
			dashboard.setAvgNumOfPassengersInBookings(avgPas);

			Double standardDeviationPassengers = 0.0;
			for (Booking b : bookings)
				standardDeviationPassengers += Math.pow(this.repository.findPassengersByBookingId(b.getId()).size() - avgPas, 2);

			standardDeviationPassengers = Math.sqrt(standardDeviationPassengers / numBookings);
			dashboard.setStdDeviationNumOfPassengersInBookings(standardDeviationPassengers);
		}

		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final CustomerDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, "lastFiveDestinations", "moneySpentLastYear", "numOfBookingsByTravelClass", "avgCostOfBookingsLastFiveYears", "minCostOfBookingsLastFiveYears", "maxCostOfBookingsLastFiveYears",
			"stdDeviationCostOfBookingsLastFiveYears", "avgNumOfPassengersInBookings", "minNumOfPassengersInBookings", "maxNumOfPassengersInBookings", "stdDeviationNumOfPassengersInBookings");

		super.getResponse().addData(dataset);
	}
}
