<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<!-- Utilizo en uniqueIdentifier un textbox ya que el framework tiene un bug visual con el integer -->
	<acme:input-textbox code="manager.leg.form.label.uniqueIdentifier" path="uniqueIdentifier" placeholder="manager.leg.form.placeholder.uniqueIdentifier" readonly="${ !isDraftMode }"/>
	<acme:input-moment code="manager.leg.form.label.scheduledDeparture" path="scheduledDeparture" readonly="${ !isDraftMode }"/>
	<acme:input-moment code="manager.leg.form.label.scheduledArrival" path="scheduledArrival" readonly="${ !isDraftMode }"/>
	<acme:input-select code="manager.leg.form.label.status" path="status" choices="${statuses}" readonly="${ !isDraftMode && status == 'LANDED' }"/>
	<acme:input-select code="manager.leg.form.label.departureAirport" path="departureAirport" choices="${departureIATACodes}" readonly="${ !isDraftMode }"/>
	<acme:input-select code="manager.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${arrivalIATACodes}" readonly="${ !isDraftMode }"/>
	<acme:input-select code="manager.leg.form.label.aircraft" path="aircraft" choices="${registrationNumbers}" readonly="${ !isDraftMode }"/>
	<acme:input-select code="manager.leg.form.label.flight" path="flight" choices="${flightChoices}" readonly="${ !isDraftMode }"/>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish|status')}">
			<jstl:if test="${ isDraftMode }"> 
				<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
				<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
				<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			</jstl:if>
			<jstl:if test="${ !isDraftMode && status != 'LANDED' }"> 
				<acme:submit code="manager.leg.form.button.status" action="/manager/leg/status"/>
			</jstl:if>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.form.button.create" action="/manager/leg/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>