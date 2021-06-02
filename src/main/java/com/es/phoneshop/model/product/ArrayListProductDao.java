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
                String[] quiries = query.split(" ");
                List<Product> productsContainsQuery = new ArrayList<>();
                for (Product product :
                        allProducts) {
                    String[] description = product.getDescription().split(" ");
                    boolean contains = Arrays.asList(description).containsAll(Arrays.asList(quiries));
                    if (contains) {
                        productsContainsQuery.add(product);
                    }
                }
                allProducts = productsContainsQuery;
            }
            Comparator<Product> comparatorField = Comparator.comparing(product -> {
                if (sortField != null && SortField.description == sortField) {
                    return (Comparable) product.getDescription();
                } else {
                    return (Comparable) product.getPrice();
                }
            });
            Comparator<Product> comparatorOrder = comparatorField;
            if (sortOrder != null && SortOrder.desc == sortOrder) {
                comparatorOrder = comparatorField.reversed();
            }
            return allProducts.stream()
                    .sorted(comparatorField)
                    .sorted(comparatorOrder)
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
}
