<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.booking.form.label.locatorCode" path="locatorCode"/>
	<acme:input-textbox code="administrator.booking.form.label.flight" path="flight"/>
	<acme:input-textbox code="administrator.booking.form.label.travelClass" path="travelClass"/>
	<acme:input-textbox code="administrator.booking.form.label.lastNibble" path="lastNibble"/>
	
	<acme:button code="customer.booking.form.button.passengers" action="/administrator/passenger/list?bookingId=${id}"/>
</acme:form>