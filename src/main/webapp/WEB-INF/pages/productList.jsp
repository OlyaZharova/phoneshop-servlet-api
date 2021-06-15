<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>
        Welcome to Expert-Soft training!
    </p>
    <c:if test="${not empty param.message}">
            <div class="success">
               <p>
                    ${param.message}
               </p>
            </div>
        </c:if>
        <c:if test="${not empty errors}">
            <div class="error">
                <p>
                    There was an error adding to cart
                </p>
            </div>
        </c:if>
    <form>
        <input name="query" value="${param.query}">
        <button>Search</button>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description
                <tags:sortLink sort="description" order="asc"/>
                <tags:sortLink sort="description" order="desc"/>
            </td>
            <td class="quantity">Quantity</td>
            <td class="price">Price
                <tags:sortLink sort="price" order="asc"/>
                <tags:sortLink sort="price" order="desc"/>
            </td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}" varStatus="status">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                            ${product.description}
                    </a>
                </td>
                <form method="post" action="${pageContext.servletContext.contextPath}/products">
                <td class="quantity">
                                        <fmt:formatNumber value="${quantity}" var="quantity"/>
                                        <c:set var="error" value="${errors[product.id]}"/>
                                        <input name="quantity"
                                               value="${product.id==param.productId ? param.quantity : 1}"
                                               class="quantity"/>
                                        <input type="hidden" name="productId" value="${product.id}">
                                        <c:if test="${not empty error}">
                                            <div class="error">
                                                    ${errors[product.id]}
                                            </div>
                                        </c:if>
                                    </td>
                <td class="price">
                    <a href="${pageContext.servletContext.contextPath}/priceHistory/${product.id}">
                            <fmt:formatNumber value="${product.price}" type="currency"
                                              currencySymbol="${product.currency.symbol}"/>
                </td>
                <td>
                  <button>Add to cart</button>
                </td>
                </form>
            </tr>
        </c:forEach>
    </table>
    <tags:productHistory productHistory="${productHistory}"/>
</tags:master>