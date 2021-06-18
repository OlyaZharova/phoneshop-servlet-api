package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.ProductNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


public class DefaultCartService implements CartService {

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    private ProductDao productDao;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    private static class SingletonHelper {
        private static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    public static DefaultCartService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
        if (cart == null) {
            rwl.writeLock().lock();
            try {
                cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
                if (cart == null) {
                    request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
                }
            } finally {
                rwl.writeLock().unlock();
            }
        }
        return cart;
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException, ProductNotFoundException {
        rwl.writeLock().lock();
        try {
            Optional<Product> searchProduct = productDao.getProduct(productId);
            if (searchProduct.isPresent()) {
                Product product = searchProduct.get();
                int quantityInHistory = cart.getItems().stream()
                        .filter(cartItem -> cartItem.getProduct().equals(product))
                        .mapToInt(CartItem::getQuantity).sum();
                int stockAvailable = product.getStock() - quantityInHistory;
                if (stockAvailable < quantity) {
                    throw new OutOfStockException(product, quantity, stockAvailable);
                }
                if (quantityInHistory > 0) {
                    cart.getItems().stream()
                            .filter(cartItem -> cartItem.getProduct().equals(product))
                            .forEach(cartItem -> cartItem.setQuantity(quantityInHistory + quantity));
                } else {
                    cart.getItems().add(new CartItem(product, quantity));
                }
            } else {
                throw new ProductNotFoundException();
            }
            recalculateCart(cart);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException, ProductNotFoundException {
        rwl.writeLock().lock();
        try {
            Optional<Product> searchProduct = productDao.getProduct(productId);
            if (searchProduct.isPresent()) {
                Product product = searchProduct.get();
                if (product.getStock() < quantity) {
                    throw new OutOfStockException(product, quantity, product.getStock());
                } else {
                    cart.getItems().stream()
                            .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                            .forEach(cartItem -> cartItem.setQuantity(quantity));
                }
            } else {
                throw new ProductNotFoundException();
            }
            recalculateCart(cart);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public void delete(Cart cart, Long productId) {
        rwl.writeLock().lock();
        try {
            cart.getItems().removeIf(cartItem ->
                    productId.equals(cartItem.getProduct().getId()));
            recalculateCart(cart);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity)
                .collect(Collectors.summingInt(q -> q.intValue())));
        cart.setTotalCost(cart.getItems().stream()
                .map(cartItem -> cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
