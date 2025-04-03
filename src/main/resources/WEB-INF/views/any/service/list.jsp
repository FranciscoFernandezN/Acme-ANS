<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list >
	<acme:list-column code="any.service.list.label.name" path="name" width="20%"/>
	<acme:list-column code="any.service.list.label.avgDwellTime" path="avgDwellTime" width="10%"/>
	<acme:list-column code="any.service.list.label.promotionCode" path="promotionCode" width="40%"/>
	<acme:list-column code="any.service.list.label.money" path="money" width="30%"/>
		
</acme:list>
