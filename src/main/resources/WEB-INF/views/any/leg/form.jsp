<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<!-- Utilizo en uniqueIdentifier un textbox ya que el framework tiene un bug visual con el integer -->
	<acme:input-textbox code="any.leg.form.label.uniqueIdentifier" path="uniqueIdentifier" />
	<acme:input-moment code="any.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-moment code="any.leg.form.label.scheduledArrival" path="scheduledArrival" />
	<acme:input-textbox code="any.leg.form.label.status" path="status"/>
	<acme:input-textbox code="any.leg.form.label.departureAirport" path="departureAirport" />
	<acme:input-textbox code="any.leg.form.label.arrivalAirport" path="arrivalAirport" />
	<acme:input-textbox code="any.leg.form.label.aircraft" path="aircraft" />
	<acme:input-textbox code="any.leg.form.label.airline" path="airline" />
	
</acme:form>