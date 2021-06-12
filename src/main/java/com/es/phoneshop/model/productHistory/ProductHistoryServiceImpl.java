package com.es.phoneshop.model.productHistory;


import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProductHistoryServiceImpl implements ProductHistoryService {

    private static final String PRODUCT_HISTORY_SESSION_ATTRIBUTE = ProductHistoryServiceImpl.class.getName() + ".history";
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final int PRODUCT_HISTORY_SIZE = 3;

    private static class SingletonHelper {
        private static final ProductHistoryServiceImpl INSTANCE = new ProductHistoryServiceImpl();

    }

    public static ProductHistoryServiceImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public ProductHistory getProductHistory(HttpServletRequest request) {
        rwl.readLock().lock();
        try {
            ProductHistory productHistory = (ProductHistory) request.getSession().getAttribute(PRODUCT_HISTORY_SESSION_ATTRIBUTE);
            if (productHistory == null) {
                request.getSession().setAttribute(PRODUCT_HISTORY_SESSION_ATTRIBUTE, productHistory = new ProductHistory());
            }
            return productHistory;
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public void add(ProductHistory productHistory, Product product) {
        rwl.writeLock().lock();
        try {
            ConcurrentLinkedDeque<Product> products = productHistory.getProductHistory();
            if (products.contains(product)) {
                products.removeLastOccurrence(product);
            }
            products.addFirst(product);
            if (products.size() > PRODUCT_HISTORY_SIZE) {
                products.removeLast();
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }
}

