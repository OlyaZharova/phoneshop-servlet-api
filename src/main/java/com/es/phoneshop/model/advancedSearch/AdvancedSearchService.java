package com.es.phoneshop.model.advancedSearch;

import com.es.phoneshop.model.product.Product;

import java.util.List;

public interface AdvancedSearchService {
    List<Product> findProduct(SearchParams params);
}
