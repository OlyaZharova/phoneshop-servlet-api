<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Price history">
    <h1>Price history</h1>
    <h3>
            ${product.description}
    </h3>
    <table>
        <tr>
            <td>Start date</td>
            <td>Price</td>
        </tr>
        <c:forEach var="priceHistory" items="${product.histories}">
        <tr>
            <td>${priceHistory.date}</td>
            <td><fmt:formatNumber value="${priceHistory.price}" type="currency"
            currencySymbol="${priceHistory.currency.symbol}"/></td>
        </tr>
        </c:forEach>
    </table>
</tags:master>