<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.service.list.label.name" path="name" width="10%"/>
	<acme:list-column code="administrator.service.list.label.avgDwellTime" path="avgDwellTime" width="10%"/>
	<acme:list-column code="administrator.service.list.label.promotionCode" path="promotionCode" width="10%"/>
	<acme:list-column code="administrator.service.list.label.money" path="money" width="10%"/>
	<acme:list-column code="administrator.service.list.label.iATACode" path="iATACode" width="10%"/>
	<acme:list-column code="administrator.service.list.label.defaultMoney" path="defaultMoney" width="20%"/>
</acme:list>

<acme:button code="administrator.service.list.button.create" action="/administrator/service/create"/>