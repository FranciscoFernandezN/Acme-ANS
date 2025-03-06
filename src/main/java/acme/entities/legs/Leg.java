
package acme.entities.legs;

import java.util.Date;

import acme.client.components.basis.AbstractEntity;
import acme.entities.airports.Airport;
import lombok.Getter;

@Getter
public class Leg extends AbstractEntity {

	private Date	scheduledDeparture;
	private Date	scheduledArrival;
	private Airport	departureAirport;
	private Airport	arrivalAirport;
}
