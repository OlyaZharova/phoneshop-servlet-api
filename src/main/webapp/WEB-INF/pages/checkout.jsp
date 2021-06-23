<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Checkout">
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
                There were errors placing order
            </p>
        </div>
    </c:if>
    <br>
    <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
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
            <c:forEach var="item" items="${order.items}" varStatus="status">
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
                            ${item.quantity}
                    </td>
                    <td class="price">
                        <a href="${pageContext.servletContext.contextPath}/priceHistory/${item.product.id}">
                                <fmt:formatNumber value="${item.product.price}" type="currency"
                                                  currencySymbol="${item.product.currency.symbol}"/>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td>Subtotal:</td>
                <td>${order.subtotal}</td>
            </tr>
            <tr>
                <td>Delivery cost:</td>
                <td>${order.deliveryCost}</td>
            </tr>
            <tr>
                <td>Total cost:</td>
                <td>${order.totalCost}</td>
            </tr>
        </table>
        <h2>Your details</h2>
        <table>
            <tags:orderFormRow name="firstName" label="First Name" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tags:orderFormRow name="lastName" label="Last Name" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tags:orderFormRow name="phone" label="Phone" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tr>
                <td>Delivery date<span style="color:red">*</span></td>
                <td>
                    <c:set var="error" value="${errors['deliveryDate']}"/>
                    <input name="deliveryDate" type="date" value="${not empty error ? param['deliveryDate'] : order.deliveryDate}"/>
                    <c:if test="${not empty error}">
                        <div class="error">
                                ${errors['deliveryDate']}
                        </div>
                    </c:if>
                </td>
            </tr>
            <tags:orderFormRow name="deliveryAddress" label="Delivery address" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tr>
                <td>Payment method<span style="color:red">*</span></td>
                <td>
                    <c:set var="error" value="${errors['paymentMethod']}"/>
                    <select name="paymentMethod">
                      <c:if test="${not empty order.paymentMethod}">
                         <option>${order.paymentMethod}</option>
                      </c:if>
                      <c:if test="${empty order.paymentMethod}">
                      <option></option>
                      </c:if>
                      <c:forEach var="paymentMethod" items="${paymentMethods}">
                      <option>${paymentMethod}</option>
                      </c:forEach>
                    </select>
                    <c:if test="${not empty error}">
                        <div class="error">
                                ${errors['paymentMethod']}
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>
        <p>
            <button>Place order</button>
        </p>
</tags:master>