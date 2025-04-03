<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="administrator.claim.form.label.registrationMoment" path="registrationMoment"/>
    <acme:input-textbox code="administrator.claim.form.label.passengerEmail" path="passengerEmail"/>
    <acme:input-textarea code="administrator.claim.form.label.description" path="description"/>
    <acme:input-textbox code="administrator.claim.form.label.claimType" path="claimType"/>
    <acme:input-textbox code="administrator.claim.form.label.indicator" path="indicator"/>
    <acme:input-textbox code="administrator.claim.form.label.leg" path="leg"/>
    <acme:input-checkbox code="administrator.claim.form.label.isPublished" path="isPublished"/>
    
    <jstl:if test="${hasLog == true}">
    	<acme:button code="administrator.claim.form.button.log" action="/administrator/tracking-log/show?masterId=${id}"/>
    </jstl:if>
</acme:form>
