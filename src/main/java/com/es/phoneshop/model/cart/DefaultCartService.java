package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
        rwl.readLock().lock();
        try {
            Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
            }
            return cart;
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
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
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }

}
