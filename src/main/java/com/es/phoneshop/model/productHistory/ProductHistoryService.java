package com.es.phoneshop.model.productHistory;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;

public interface ProductHistoryService {

    ProductHistory getProductHistory(HttpServletRequest request);

    void add(ProductHistory productHistory, Product product);

}
