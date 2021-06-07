package com.es.phoneshop.model.product;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {

    private long maxId;
    private List<Product> products;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static ArrayListProductDao instance;

    public static synchronized ArrayListProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    public ArrayListProductDao() {
        products = new ArrayList<>();
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        rwl.readLock().lock();
        try {
            return products.stream()
                    .filter(product -> id.equals(product.getId()))
                    .findAny();
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        rwl.readLock().lock();
        try {
            List<Product> allProducts = products.stream()
                    .filter(product -> product.getPrice() != null)
                    .filter(product -> product.getStock() > 0)
                    .collect(Collectors.toList());
            if (query != null && !query.trim().isEmpty()) {
                allProducts = products.stream()
                        .filter(product -> getProductRelevancePoints(product, query) > 0)
                        .collect(Collectors.toList());
            }
            return allProducts.stream()
                    .sorted(getComparator(query, sortField, sortOrder))
                    .collect(Collectors.toList());
        } finally {
            rwl.readLock().unlock();
        }
    }


    @Override
    public void save(Product product) {
        rwl.writeLock().lock();
        try {
            if (product.getId() != null) {
                Optional<Product> oldProduct = getProduct(product.getId());
                if (oldProduct.isPresent()) {
                    products.set(products.indexOf(oldProduct.get()), product);
                }
            } else {
                product.setId(maxId++);
                products.add(product);
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public void delete(Long id) {
        rwl.writeLock().lock();
        try {
            Optional<Product> product = getProduct(id);
            if (product.isPresent()) {
                products.remove(product.get());
            }
        } finally {
            rwl.writeLock().unlock();
        }
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

    private Comparator<Product> getComparator(String query, SortField sortField, SortOrder sortOrder) {
        Comparator<Product> comparator = null;
        comparator = Comparator.comparing(product -> {
            if (sortField != null && SortField.description == sortField) {
                return (Comparable) product.getDescription();
            } else {
                return (Comparable) product.getPrice();
            }
        });
        if (sortOrder != null && SortOrder.desc == sortOrder) {
            comparator = comparator.reversed();
        }
        if (query != null && !query.trim().isEmpty() && sortOrder == null && sortField == null) {
            comparator = Comparator.comparingInt(product -> getProductRelevancePoints(product, query));
            comparator = comparator.reversed();
        }
        return comparator;
    }

    @Override
    public void deleteAll() {
        rwl.writeLock().lock();
        try {
            products.clear();
        } finally {
            rwl.writeLock().unlock();
        }
    }

}
