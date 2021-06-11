package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public class DefaultCartService implements CartService {

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    private ProductDao productDao;

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
            request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
        }
        return cart;
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        Optional<Product> searchProduct = productDao.getProduct(productId);
        if (searchProduct.isPresent()) {
            Product product = searchProduct.get();
            int stockAvailable = calculateStockAvailable(product, cart);
            if (stockAvailable < quantity) {
                throw new OutOfStockException(product, quantity, stockAvailable);
            }
            cart.getItems().add(new CartItem(product, quantity));
        }
    }

    public int calculateStockAvailable(Product product, Cart cart) {
        int lastStock = product.getStock();
        List<CartItem> cartItems = cart.getItems();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId() == product.getId()) {
                lastStock -= cartItem.getQuantity();
            }
        }
        return lastStock;
    }

}
