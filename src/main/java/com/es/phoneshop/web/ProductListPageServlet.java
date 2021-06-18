package com.es.phoneshop.web;


import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.*;
import com.es.phoneshop.model.productHistory.ProductHistory;
import com.es.phoneshop.model.productHistory.ProductHistoryService;
import com.es.phoneshop.model.productHistory.ProductHistoryServiceImpl;
import com.es.phoneshop.util.QuantityUtility;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {

    private ProductDao productDao;
    private ProductHistoryService productHistoryService;
    private CartService cartService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        productHistoryService = ProductHistoryServiceImpl.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");
        ProductHistory productHistory = productHistoryService.getProductHistory(request);
        request.setAttribute("productHistory", productHistory.getProductHistory());
        request.setAttribute("products", productDao.findProducts(query,
                Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null)
        ));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<Long, String> errors = new HashMap<>();

        Long productId = Long.valueOf(request.getParameter("productId"));
        String quantityString = request.getParameter("quantity");
        int quantity;
        try {
            quantity = QuantityUtility.getQuantity(quantityString, request);
            Cart cart = cartService.getCart(request);
            cartService.add(cart, productId, quantity);
            if (quantity < 1) {
                errors.put(productId, "Number must be more than zero");
            }
        } catch (ParseException e) {
            errors.put(productId, "Not a number");
        } catch (OutOfStockException outOfStockException) {
            errors.put(productId, "Out of stock, available " + outOfStockException.getStockAvailable());
        } catch (ProductNotFoundException productNotFoundException) {
            errors.put(productId, "Product not found ");
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products?message=Product added to cart");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }
}
