package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ProductNotFoundException;

import javax.servlet.http.HttpServletRequest;

public interface CartService {
    Cart getCart(HttpServletRequest request);

    void add(Cart cart, Long productId, int quantity) throws OutOfStockException, ProductNotFoundException;

    void update(Cart cart, Long productId, int quantity) throws OutOfStockException, ProductNotFoundException;

    void delete(Cart cart, Long productId);
}
