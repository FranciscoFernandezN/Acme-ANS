<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.supported-currency.list.label.currencyName" path="currencyName" width="50%"/>
	<acme:list-column code="administrator.supported-currency.list.label.isDefaultCurrency" path="isDefaultCurrency" width="50%"/>
</acme:list>

<acme:button code="administrator.supported-currency.list.button.create" action="/administrator/supported-currency/create"/>