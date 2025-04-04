<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.review.form.label.name" path="name" placeholder="any.review.form.placeholder.name" />
	<acme:input-textbox code="any.review.form.label.subject" path="subject" placeholder="any.review.form.placeholder.subject"/>
	<acme:input-textarea code="any.review.form.label.body" path="body"/>
	<acme:input-double code="any.review.form.label.score" path="score" placeholder="any.review.form.placeholder.score"/>
	<acme:input-checkbox code="any.review.form.label.isRecommended" path="isRecommended"/>
	<acme:input-checkbox code="any.review.form.label.confirmation" path="confirmation"/>
	
	<acme:submit code="any.review.form.button.create" action="/any/review/create"/>
</acme:form>