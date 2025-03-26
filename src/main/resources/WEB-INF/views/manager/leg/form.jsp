<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.leg.form.label.uniqueIdentifier" path="uniqueIdentifier" readonly="${acme:anyOf(_command, 'show|update|delete')}"/>
	<acme:input-moment code="manager.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-moment code="manager.leg.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-select code="manager.leg.form.label.status" path="status" choices="${statuses}"/>
	<acme:input-select code="manager.leg.form.label.departureAirport" path="departureAirport" choices="${arrivalIATACodes}"/>
	<acme:input-select code="manager.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${departureIATACodes}"/>
	<acme:input-select code="manager.leg.form.label.aircraft" path="aircraft" choices="${registrationNumbers}"/>
	<acme:input-checkbox code="manager.leg.form.label.isDraftMode" path="isDraftMode"/>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && isDraftMode == true}">
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.form.button.create" action="/manager/leg/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>