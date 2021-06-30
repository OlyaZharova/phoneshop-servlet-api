package com.es.phoneshop.model.advancedSearch;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdvancedSearchServiceImpl implements AdvancedSearchService{

    private ProductDao productDao = ArrayListProductDao.getInstance();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private static class SingletonHelper {
        private static final AdvancedSearchServiceImpl INSTANCE = new AdvancedSearchServiceImpl();
    }

    public static AdvancedSearchServiceImpl getInstance() {
        return AdvancedSearchServiceImpl.SingletonHelper.INSTANCE;
    }

    @Override
    public List<Product> findProduct(SearchParams params) {
        rwl.readLock().lock();
        try {
            List<Product> products = productDao.findProducts(null, null, null);
            List<Product> result = searchProduct(params, products);
            return result;
        } finally {
            rwl.readLock().unlock();
        }
    }

    private List<Product> searchProduct(
            SearchParams params, List<Product> products) {
        Stream<Product> result = products.stream();
        if (params.getMinPrice() != null) {
            result = result.filter(product -> product.getPrice().intValue() >= params.getMinPrice());
        }
        if (params.getMaxPrice() != null) {
            result = result.filter(product -> product.getPrice().intValue() <= params.getMaxPrice());
        }
        if(params.getDescription() != null){
            if(params.isChoiceWord()){
                result = result.filter(product -> product.getDescription().equals(params.getDescription()));
            }
            else {
                result = result.filter(product -> getProductRelevancePoints(product, params.getDescription()) > 0);
            }
        }

        return result.collect(Collectors.toList());
    }

    private int getProductRelevancePoints(Product p, String query) {
        int points = 0;
        String[] queries = query.toLowerCase(Locale.ROOT).split(" ");
        String[] description = p.getDescription().toLowerCase(Locale.ROOT).split(" ");
        for (int i = 0; i < description.length; i++) {
            for (int y = 0; y < queries.length; y++) {
                if (description[i].equals(queries[y])) {
                    points++;
                }
            }
        }
        return points;
    }
}
