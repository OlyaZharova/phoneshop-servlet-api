package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.*;
import com.es.phoneshop.web.ProductDetailsPageServlet;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {

    private DefaultCartService cartService;

    private static List<PriceHistory> histories;
    private Cart cart;
    private static ProductDao productDao;
    private static long incorrectProductId;
    private static long productId;
    @Mock
    private HttpServletRequest request;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private HttpSession session;

    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();


    @BeforeClass
    public static void saveProduct() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        histories = new ArrayList<>();
        Product product = new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(1000), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", histories);
        productDao.save(product);
        productId = product.getId();
        incorrectProductId = productId + 1;
    }

    @AfterClass
    public static void deleteProduct() {
        productDao.deleteAll();
    }


    @Before
    public void setup() throws ServletException {
        cart = new Cart();
        cartService = DefaultCartService.getInstance();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(cart);
        servlet.init(servletConfig);
    }


    @Test
    public void testAdd() throws OutOfStockException, ProductNotFoundException {
        cartService.add(cart, productId, 50);
        assertFalse(cart.getItems().isEmpty());
        assertEquals(cart.getItems().get(0).getQuantity(), 50);
    }

    @Test
    public void testAddNonExistentProduct() throws OutOfStockException {
        try {
            cartService.add(cart, incorrectProductId, 50);
            fail("Expected ProductNotFoundException");
        } catch (ProductNotFoundException productNotFoundException) {
            assertNotEquals("", productNotFoundException.getMessage());
        }
    }

    @Test
    public void testAddQuantityMoreStock() throws ProductNotFoundException {
        try {
            cartService.add(cart, productId, 110);
            fail("Expected OutOfStockException");
        } catch (OutOfStockException outOfStockException) {
            assertEquals(110, outOfStockException.getStockRequested());
        }
    }

    @Test
    public void testAddTwoSimilarProduct() throws OutOfStockException, ProductNotFoundException {
        cartService.add(cart, productId, 10);
        cartService.add(cart, productId, 10);
        Optional<CartItem> cartItem = cartService.getCart(request).getItems().stream()
                .filter(cartItem1 -> cartItem1.getProduct().getId().equals(productId))
                .findAny();
        int quantity = cartItem.get().getQuantity();
        int result = 20;
        assertEquals(result, quantity);
    }

    @Test
    public void testGetCart() {
        cartService.getCart(request);
        verify(session).getAttribute(anyString());
    }

    @Test
    public void testGetProductHistoryWithNullProductHistory() {
        when(session.getAttribute(anyString())).thenReturn(null);
        Cart newCart = cartService.getCart(request);
        verify(session).setAttribute(anyString(), any());
        assertTrue(newCart.getItems().isEmpty());
    }

    @Test
    public void testUpdate() throws OutOfStockException, ProductNotFoundException {
        cartService.add(cart, productId, 50);
        cartService.update(cart, productId, 30);
        assertFalse(cart.getItems().isEmpty());
        assertEquals(cart.getItems().get(0).getQuantity(), 30);
    }

    @Test
    public void testUpdateNonExistentProduct() throws OutOfStockException {
        try {
            cartService.update(cart, incorrectProductId, 50);
            fail("Expected ProductNotFoundException");
        } catch (ProductNotFoundException productNotFoundException) {
            assertNotEquals("", productNotFoundException.getMessage());
        }
    }

    @Test
    public void testUpdateQuantityMoreStock() throws ProductNotFoundException {
        try {
            cartService.add(cart, productId, 50);
            cartService.update(cart, productId, 110);
            fail("Expected OutOfStockException");
        } catch (OutOfStockException outOfStockException) {
            assertEquals(110, outOfStockException.getStockRequested());
        }
    }

    @Test
    public void testDelete() throws OutOfStockException, ProductNotFoundException {
        cartService.add(cart, productId, 50);
        cartService.delete(cart, productId);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    public void testRecalculateCart() throws OutOfStockException, ProductNotFoundException {
        cartService.add(cart, productId, 5);
        assertEquals(cart.getTotalCost(), new BigDecimal(5000));
        assertEquals(cart.getTotalQuantity(), 5);
    }

    @Test
    public void testClearCart() {
        cartService.clearCart(cart);
        assertEquals(cart.getTotalCost(), BigDecimal.ZERO);
        assertEquals(cart.getTotalQuantity(), 0);
        assertTrue(cart.getItems().isEmpty());
    }
}
