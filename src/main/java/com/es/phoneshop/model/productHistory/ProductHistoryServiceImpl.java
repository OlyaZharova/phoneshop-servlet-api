package com.es.phoneshop.model.productHistory;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ProductHistoryServiceImpl implements ProductHistoryService {

    private ProductDao productDao;
    private static final String PRODUCT_HISTORY_SESSION_ATTRIBUTE = ProductHistoryServiceImpl.class.getName() + ".history";

    private ProductHistoryServiceImpl() {
        productDao = ArrayListProductDao.getInstance();
    }

    private static class SingletonHelper {
        private static final ProductHistoryServiceImpl INSTANCE = new ProductHistoryServiceImpl();

    }

    public static ProductHistoryServiceImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public ProductHistory getProductHistory(HttpServletRequest request) {
        ProductHistory productHistory = (ProductHistory) request.getSession().getAttribute(PRODUCT_HISTORY_SESSION_ATTRIBUTE);
        if (productHistory == null) {
            request.getSession().setAttribute(PRODUCT_HISTORY_SESSION_ATTRIBUTE, productHistory = new ProductHistory());
        }
        return productHistory;
    }

    @Override
    public void add(ProductHistory productHistory, Product product) {
        ConcurrentLinkedDeque<Product> products = productHistory.getProductHistory();
        if (products.contains(product)) {
            products.removeLastOccurrence(product);
        }
        products.addFirst(product);
        if (products.size() > 3) {
            products.removeLast();
        }
    }

}

