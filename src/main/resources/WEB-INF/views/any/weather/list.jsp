<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list >

	<acme:list-column code="any.weather.list.label.city" path="city" width="25%"/>
	<acme:list-column code="any.weather.list.label.forecastDate" path="forecastDate" width="25%"/>
	<acme:list-column code="any.weather.list.label.status" path="status" width="25%"/>
	<acme:list-column code="any.weather.list.label.temperature" path="temperature" width="25%"/>
		
</acme:list>
