<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="productHistory" required="true" type="java.util.concurrent.ConcurrentLinkedDeque" %>

<html>
<body >
<c:if test="${not empty productHistory}">
<h3>Recently viewed</h3>
<table>
      <tr>
        <c:forEach var="product1" items="${productHistory}">
        <td>
        <p align="center">
          <img class="product-tile" src="${product1.imageUrl}">
          </p>
          <p align="center">
        <a href="${pageContext.servletContext.contextPath}/products/${product1.id}">
        ${product1.description}
        </a>
        </p>
        <p align="center">
        <fmt:formatNumber value="${product1.price}" type="currency"
                                          currencySymbol="${product1.currency.symbol}"/>
          </a>
          </p>
        </td>
        </c:forEach>
      </tr>
    </table>
    </c:if>
</body>
</html>