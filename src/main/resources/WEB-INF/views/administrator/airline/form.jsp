<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="administrator.airline.form.label.name" path="name"/>
    <acme:input-textbox code="administrator.airline.form.label.iATACode" path="iATACode"/>
    <acme:input-url code="administrator.airline.form.label.website" path="website"/>
    
    <acme:input-select code="administrator.airline.form.label.type" path="type">
        <acme:option value="LUXURY" code="administrator.airline.type.luxury"/>
        <acme:option value="STANDARD" code="administrator.airline.type.standard"/>
        <acme:option value="LOW-COST" code="administrator.airline.type.lowcost"/>
    </acme:input-select>
    
    <acme:input-date code="administrator.airline.form.label.foundationMoment" path="foundationMoment"/>
    <acme:input-textbox code="administrator.airline.form.label.email" path="email"/>
    <acme:input-textbox code="administrator.airline.form.label.contactNumber" path="contactNumber"/>

    <acme:submit code="administrator.airline.form.button.create" action="/administrator/airline/create"/>
</acme:form>

