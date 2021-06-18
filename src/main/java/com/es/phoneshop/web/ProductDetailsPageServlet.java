package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.ProductNotFoundException;
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
import java.util.Optional;

public class ProductDetailsPageServlet extends HttpServlet {

    private ProductDao productDao;
    private CartService cartService;
    private ProductHistoryService productHistoryService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        productHistoryService = ProductHistoryServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = parseProductId(request);
        Optional<Product> productSearchResult = productDao.getProduct(id);
        if (productSearchResult.isPresent()) {
            Product product = productSearchResult.get();
            ProductHistory productHistory = productHistoryService.getProductHistory(request);
            request.setAttribute("productHistory", productHistory.getProductHistory());
            request.setAttribute("product", product);
            request.setAttribute("cart", cartService.getCart(request));
            request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
            productHistoryService.add(productHistory, product);
        } else {
            response.sendError(404);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String quantityString = request.getParameter("quantity");
        Long id = parseProductId(request);

        int quantity;
        try {
            quantity = QuantityUtility.getQuantity(quantityString, request);
            if (quantity < 1) {
                request.setAttribute("error", "Number must be more than zero");
                doGet(request, response);
                return;
            }
        } catch (ParseException e) {
            request.setAttribute("error", "Not a number");
            doGet(request, response);
            return;
        }

        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, id, quantity);
        } catch (OutOfStockException e) {
            request.setAttribute("error", "Out of stock, available " + e.getStockAvailable());
            doGet(request, response);
            return;
        } catch (ProductNotFoundException e) {
            request.setAttribute("error", "Product not found");
            doGet(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/products/" + id + "?message=Product added to cart");
    }

    private Long parseProductId(HttpServletRequest request) {
        String productInfo = request.getPathInfo().substring(1);
        return Long.valueOf(productInfo);
    }
}
