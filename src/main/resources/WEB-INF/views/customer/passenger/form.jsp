<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.passenger.form.label.fullName" path="fullName"/>
	<acme:input-email code="customer.passenger.form.label.email" path="email"/>
	<acme:input-textbox code="customer.passenger.form.label.passportNumber" path="passportNumber"/>
	<acme:input-moment code="customer.passenger.form.label.dateOfBirth" path="dateOfBirth"/>
	<acme:input-textbox code="customer.passenger.form.label.specialNeeds" path="specialNeeds"/>
	<acme:input-select code="customer.passenger.form.label.booking" path="booking" choices="${bookingChoices}" readonly="${createdInBooking}"/>
	
	<jstl:choose>
		<jstl:when test="${_command == 'publish' && updatedPassenger || (acme:anyOf(_command, 'show|update') && isDraftMode == true)}">
			<acme:submit code="customer.passenger.form.button.update" action="/customer/passenger/update"/>
		</jstl:when>	
		<jstl:when test="${_command == 'publish' && !updatedPassenger || (_command == 'create' && createdInBooking)}">
			<acme:submit code="customer.passenger.form.button.create" action="/customer/passenger/create?bookingId=${booking}"/>
		</jstl:when>	
		<jstl:when test="${_command == 'create' && !createdInBooking}">
			<acme:submit code="customer.passenger.form.button.create" action="/customer/passenger/create"/>
		</jstl:when>	
	</jstl:choose>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|create') && isDraftMode == true && createdInBooking}">
			<acme:submit code="customer.passenger.form.button.publish" action="/customer/passenger/publish?bookingId=${booking}"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|create') && isDraftMode == true}">
			<acme:submit code="customer.passenger.form.button.publish" action="/customer/passenger/publish"/>
		</jstl:when>
	</jstl:choose>
</acme:form>