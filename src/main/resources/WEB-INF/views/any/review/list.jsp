<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.review.list.label.name" path="name" width="20%"/>
	<acme:list-column code="any.review.list.label.postedAt" path="postedAt" width="10%"/>
	<acme:list-column code="any.review.list.label.subject" path="subject" width="10%"/>
	<acme:list-column code="any.review.list.label.body" path="body" width="50%"/>
	<acme:list-column code="any.review.list.label.score" path="score" width="10%"/>
	<acme:list-column code="any.review.list.label.isRecommended" path="isRecommended" width="10%"/>
		
</acme:list>

<acme:button code="any.review.list.button.create" action="/any/review/create"/>
