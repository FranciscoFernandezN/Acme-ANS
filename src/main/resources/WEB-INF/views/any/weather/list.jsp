<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list >

	<acme:list-column code="any.weather.list.label.city" path="name" width="25%"/>
	<acme:list-column code="any.weather.list.label.forecastDate" path="postedAt" width="25%"/>
	<acme:list-column code="any.weather.list.label.status" path="subject" width="25%"/>
	<acme:list-column code="any.weather.list.label.temperature" path="body" width="25%"/>
		
</acme:list>
