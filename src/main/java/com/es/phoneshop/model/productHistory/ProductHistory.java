package com.es.phoneshop.model.productHistory;


import com.es.phoneshop.model.product.Product;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ProductHistory implements Serializable {
    private ConcurrentLinkedDeque<Product> productHistory;

    public ProductHistory() {
        this.productHistory = new ConcurrentLinkedDeque<>();
    }

    public ConcurrentLinkedDeque<Product> getProductHistory() {
        return productHistory;
    }
}
