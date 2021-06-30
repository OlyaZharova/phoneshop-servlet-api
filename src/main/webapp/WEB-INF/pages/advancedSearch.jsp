<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<tags:master pageTitle="Advanced Search">
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
                There were errors searching product
            </p>
        </div>
    </c:if>
    <br>
    <form method="post" action="${pageContext.servletContext.contextPath}/advancedSearch">
       <p>
        <label for="description">Description:</label>
                    <input id="description" name="description" value="${param.description}">
                    <c:if test="${not empty descriptionError}">
                        <span style="color: red">${descriptionError}</span>
                    </c:if>
                    <select name="searchWord">
                    <option value="true">All words</option>
                    <option value="false">Any word</option>
                    </select>
                </p>
                <p>
                <label for="minPrice">Min price:</label>
                      <fmt:formatNumber value="${minPrice}" var="minPrice"/>
                                                            <c:set var="error" value="${errors['minPrice']}"/>
                                                            <input name="minPrice"
                                                                   value="${not empty error ? minPrice : 1}"
                                                                   class="quantity"/>
                                                            <c:if test="${not empty error}">
                                                                <div class="error">
                                                                        ${errors["minPrice"]}
                                                                </div>
                                                            </c:if>
                </p>
                <p>
                    <label for="maxPrice">Max price:</label>
                     <fmt:formatNumber value="${maxPrice}" var="maxPrice"/>
                                                                                <c:set var="error" value="${errors['maxPrice']}"/>
                                                                                <input name="maxPrice"
                                                                                       value="${not empty error ? maxPrice : 1}"
                                                                                       class="quantity"/>
                                                                                <c:if test="${not empty error}">
                                                                                    <div class="error">
                                                                                            ${errors["maxPrice"]}
                                                                                    </div>
                                                                                </c:if>
                </p>
        <p>
            <button>Search</button>
        </p>
    </form>
    <c:if test="${empty errors}">
    <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>Description
                </td>
                <td class="price">Price
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
                    <td class="price">
                        <a href="${pageContext.servletContext.contextPath}/priceHistory/${product.id}">
                                <fmt:formatNumber value="${product.price}" type="currency"
                                                  currencySymbol="${product.currency.symbol}"/>
                    </td>
                    </form>
                </tr>
            </c:forEach>
        </table>
        </c:if>
</tags:master>