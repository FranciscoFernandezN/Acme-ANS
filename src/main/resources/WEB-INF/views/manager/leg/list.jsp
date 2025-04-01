<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.leg.list.label.flightNumber" path="flightNumber" width="10%"/>
	<acme:list-column code="manager.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="15%"/>
	<acme:list-column code="manager.leg.list.label.scheduledArrival" path="scheduledArrival" width="15%"/>
	<acme:list-column code="manager.leg.list.label.duration" path="duration" width="10%"/>
	<acme:list-column code="manager.leg.list.label.status" path="status" width="10%"/>
	<acme:list-column code="manager.leg.list.label.flight" path="flight" width="10%"/>
	<acme:list-column code="manager.leg.list.label.isDraftMode" path="isDraftMode" width="10%"/>
</acme:list>

<acme:button code="manager.leg.list.button.create" action="/manager/leg/create"/>