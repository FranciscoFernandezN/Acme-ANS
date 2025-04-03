<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.leg.list.label.flightNumber" path="flightNumber" width="10%"/>
	<acme:list-column code="any.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="15%"/>
	<acme:list-column code="any.leg.list.label.scheduledArrival" path="scheduledArrival" width="15%"/>
	<acme:list-column code="any.leg.list.label.duration" path="duration" width="10%"/>
	<acme:list-column code="any.leg.list.label.status" path="status" width="10%"/>
</acme:list>