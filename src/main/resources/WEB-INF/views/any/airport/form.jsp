<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.airport.form.label.name" path="name" />
	<acme:input-textbox code="any.airport.form.label.iATACode" path="iATACode" />
	<acme:input-select code="any.airport.form.label.operationalScope" path="operationalScope" choices="${operationalScope}"/>
	<acme:input-textbox code="any.airport.form.label.city" path="city" />
	<acme:input-textbox code="any.airport.form.label.country" path="country" />
	<acme:input-url code="any.airport.form.label.website" path="website" />
	<acme:input-email code="any.airport.form.label.email" path="email" />
	<acme:input-textbox code="any.airport.form.label.contactNumber" path="contactNumber" />
</acme:form>