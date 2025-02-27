<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
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
                There were errors updating cart
            </p>
        </div>
    </c:if>
    <br>
    <form method="post" action="${pageContext.servletContext.contextPath}/cart">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>
                    Description
                </td>
                <td class="quantity">Quantity</td>
                <td class="price">Price
                </td>
            </tr>
            </thead>
            <c:forEach var="item" items="${cart.items}" varStatus="status">
                <tr>
                    <td>
                        <img class="product-tile" src="${item.product.imageUrl}">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                                ${item.product.description}
                        </a>
                    </td>
                    <td class="quantity">
                        <fmt:formatNumber value="${item.quantity}" var="quantity"/>
                        <c:set var="error" value="${errors[item.product.id]}"/>
                        <input name="quantity"
                               value="${not empty error ? paramValues['quantity'][status.index] : item.quantity}"
                               class="quantity"/>
                        <input type="hidden" name="productId" value="${item.product.id}">
                        <c:if test="${not empty error}">
                            <div class="error">
                                    ${errors[item.product.id]}
                            </div>
                        </c:if>
                    </td>
                    <td class="price">
                        <a href="${pageContext.servletContext.contextPath}/priceHistory/${item.product.id}">
                                <fmt:formatNumber value="${item.product.price}" type="currency"
                                                  currencySymbol="${item.product.currency.symbol}"/>
                    </td>
                    <td>
                        <button form="deleteCartItem"
                                formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td>Total cost</td>
                <td>${cart.totalCost}</td>
            </tr>
            <tr>
                <td>Total quantity</td>
                <td>${cart.totalQuantity}</td>
            </tr>
        </table>
        <p>
            <button>Update</button>
        </p>
    </form>
    <form action="${pageContext.servletContext.contextPath}/checkout">
        <button>Checkout</button>
    </form>
    <form id="deleteCartItem" method="post">
    </form>
</tags:master>