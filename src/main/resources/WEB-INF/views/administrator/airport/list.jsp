<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.airport.list.label.name" path="name" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.iATACode" path="iATACode" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.operationalScope" path="operationalScope" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.city" path="city" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.country" path="country" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.website" path="website" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.email" path="email" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.contactNumber" path="contactNumber" width="10%"/>
</acme:list>

<acme:button code="administrator.airport.list.button.create" action="/administrator/airport/create"/>