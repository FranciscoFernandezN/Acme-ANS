
package acme.entities.trackinglogs;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidScore;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidNonSpamString;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimState;
import acme.realms.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "id,claim_id")
})
public class TrackingLog extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidNonSpamString
	@ValidString(max = 50)
	@Automapped
	private String				step;

	@Mandatory
	@ValidScore
	@Automapped
	private Double				resolutionPercentage;

	@Mandatory
	@ValidNonSpamString
	@ValidString
	@Automapped
	private String				resolution;

	@Mandatory
	@Automapped
	private Boolean				isPublished;

	// Derived attributes -----------------------------------------------------


	@Transient
	private ClaimState getAcceptance() {
		return this.claim.getIndicator();
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Claim			claim;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgent	agent;

}
