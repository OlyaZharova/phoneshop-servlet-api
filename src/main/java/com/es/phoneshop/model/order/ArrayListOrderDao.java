package com.es.phoneshop.model.order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ArrayListOrderDao implements OrderDao {

    private long orderId;
    private List<Order> orderList;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private static class SingletonHelper {
        private static final ArrayListOrderDao INSTANCE = new ArrayListOrderDao();
    }

    public static ArrayListOrderDao getInstance() {
        return SingletonHelper.INSTANCE;
    }


    public ArrayListOrderDao() {
        orderList = new ArrayList<>();
    }

    @Override
    public Order getOrder(Long id) throws OrderNotFoundException {
        rwl.readLock().lock();
        try {
            return orderList.stream()
                    .filter(order -> id.equals(order.getId()))
                    .findAny()
                    .orElseThrow(OrderNotFoundException::new);
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public void save(Order order) throws OrderNotFoundException {
        rwl.writeLock().lock();
        try {
            if (order.getId() != null) {
                Order oldOrder = getOrder(order.getId());
                orderList.set(orderList.indexOf(oldOrder), order);
            } else {
                order.setId(orderId++);
                orderList.add(order);
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public Order getOrderBySecureId(String secureId) throws OrderNotFoundException {
        rwl.readLock().lock();
        try {
            return orderList.stream()
                    .filter(order -> secureId.equals(order.getSecureId()))
                    .findAny()
                    .orElseThrow(OrderNotFoundException::new);
        } finally {
            rwl.readLock().unlock();
        }
    }

}
