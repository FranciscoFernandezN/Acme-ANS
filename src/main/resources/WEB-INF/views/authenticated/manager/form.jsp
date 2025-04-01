<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <!-- Uso textbox debido a un bug visual en integer -->
	<acme:input-textbox code="authenticated.manager.form.label.identifierNumber" path="identifierNumber" placeholder="authenticated.manager.form.placeholder.identifierNumber" />
	<acme:input-textbox code="authenticated.manager.form.label.yearsOfExperience" path="yearsOfExperience" placeholder="authenticated.manager.form.placeholder.yearsOfExperience"/>
	<acme:input-moment code="authenticated.manager.form.label.birth" path="birth"/>
	<acme:input-url code="authenticated.manager.form.label.linkPicture" path="linkPicture"/>
	<acme:input-select code="authenticated.manager.form.label.airlineManaging" path="airlineManaging" choices="${ airlineIATACodes }" readonly="${_command == 'update|show'}"/>
	
	<jstl:if test="${_command == 'create'}">
		<acme:submit code="authenticated.manager.form.button.create" action="/authenticated/manager/create"/>
	</jstl:if>
	
	<jstl:if test="${_command == 'update'}">
		<acme:submit code="authenticated.manager.form.button.update" action="/authenticated/manager/update"/>
	</jstl:if>
	
</acme:form>
