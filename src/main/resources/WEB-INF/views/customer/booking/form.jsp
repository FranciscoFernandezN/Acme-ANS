<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.booking.form.label.locatorCode" path="locatorCode"/>
	<acme:input-textbox code="customer.booking.form.label.purchaseMoment" path="purchaseMoment" readonly="true"/>
	<acme:input-select code="customer.booking.form.label.flight" path="flight" choices="${flightChoices}"/>
	<acme:input-select code="customer.booking.form.label.travelClass" path="travelClass" choices="${travelClasses}"/>
	<acme:input-textbox code="customer.booking.form.label.lastNibble" path="lastNibble" placeholder="1234"/>
	<acme:input-select code="customer.booking.form.label.passenger" path="passenger" choices="${passengerChoices}"/>
	
	<jstl:choose>
		<jstl:when test="${_command == 'publish' && updatedBooking || acme:anyOf(_command, 'show|update') && isDraftMode == true}">
			<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'publish' && !updatedBooking || _command == 'create'}">
			<acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
		</jstl:when>		
	</jstl:choose>
	<jstl:if test="${acme:anyOf(_command, 'show|update') || _command == 'publish' && updatedBooking }">
    	<acme:button code="customer.booking.form.button.recommendations" action="/customer/recommendation/list?city=${city}"/>
    </jstl:if>
	<jstl:if test="${isDraftMode == true}">
		<acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
	</jstl:if>
	<jstl:if test="${updatedBooking}">
		<acme:button code="customer.booking.form.button.passengers" action="/customer/passenger/list?bookingId=${id}"/>
	</jstl:if>
</acme:form>