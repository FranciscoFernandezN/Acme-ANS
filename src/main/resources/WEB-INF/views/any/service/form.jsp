<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.service.form.label.name" path="name"/>
	<acme:input-textbox code="any.service.form.label.picture" path="picture"/>
	<acme:input-textarea code="any.service.form.label.avgDwellTime" path="avgDwellTime"/>
	<acme:input-double code="any.service.form.label.promotionCode" path="promotionCode"/>
	<acme:input-money code="any.service.form.label.money" path="money"/>
	<acme:input-textbox code="any.service.form.label.airport" path="airport"/>

</acme:form>