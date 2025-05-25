<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.administrator-dashboard.form.label.totalNumAirportsByOperationalScope" path="totalNumAirportsByOperationalScope" readonly="true"/>
	<acme:input-textbox code="administrator.administrator-dashboard.form.label.numAirlinesByType" path="numAirlinesByType" readonly="true"/>
	<acme:input-money code="administrator.administrator-dashboard.form.label.ratioAirlinesWithEmailAndPhone" path="ratioAirlinesWithEmailAndPhone" readonly="true"/>
	<acme:input-money code="administrator.administrator-dashboard.form.label.ratioActiveAircrafts" path="ratioActiveAircrafts" readonly="true"/>
	<acme:input-money code="administrator.administrator-dashboard.form.label.ratioInactiveAircrafts" path="ratioInactiveAircrafts" readonly="true"/>
	<acme:input-money code="administrator.administrator-dashboard.form.label.ratioReviewsWithScoreHigher5" path="ratioReviewsWithScoreHigher5" readonly="true"/>
	<acme:input-textbox code="administrator.administrator-dashboard.form.label.countReviewsPostedLast10Weeks" path="countReviewsPostedLast10Weeks" readonly="true"/>
	<acme:input-money code="administrator.administrator-dashboard.form.label.averageReviewsPostedLast10Weeks" path="averageReviewsPostedLast10Weeks" readonly="true"/>
	<acme:input-textbox code="administrator.administrator-dashboard.form.label.minReviewsPostedLast10Weeks" path="minReviewsPostedLast10Weeks" readonly="true"/>
	<acme:input-textbox code="administrator.administrator-dashboard.form.label.maxReviewsPostedLast10Weeks" path="maxReviewsPostedLast10Weeks" readonly="true"/>
	<acme:input-money code="administrator.administrator-dashboard.form.label.stdDeviationReviewsPostedLast10Weeks" path="stdDeviationReviewsPostedLast10Weeks" readonly="true"/>

</acme:form>
