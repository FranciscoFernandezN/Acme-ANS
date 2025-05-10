<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list >

	<acme:list-column code="customer.recommendation.list.label.city" path="city" width="25%"/>
	<acme:list-column code="customer.recommendation.list.label.name" path="name" width="25%"/>
	<acme:list-column code="customer.recommendation.list.label.openNow" path="openNow" width="25%"/>
	<acme:list-column code="customer.recommendation.list.label.rating" path="rating" width="25%"/>
</acme:list>