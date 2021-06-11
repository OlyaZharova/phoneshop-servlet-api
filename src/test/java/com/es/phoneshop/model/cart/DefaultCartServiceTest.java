package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.web.ProductDetailsPageServlet;
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
    public void testAdd() throws OutOfStockException {
        cartService.add(cart, productId, 50);
        assertFalse(cart.getItems().isEmpty());
        assertEquals(cart.getItems().get(0).getQuantity(), 50);
    }

    @Test
    public void testAddQuantityMoreStock() {
        try {
            cartService.add(cart, productId, 110);
            Cart newCart = cartService.getCart(request);
            List<CartItem> cartItems = newCart.getItems();
            for (CartItem c :
                    cartItems) {
                System.out.println(c.getProduct().getId() + "  " + c.getQuantity());
            }
            fail("Expected OutOfStockException");
        } catch (OutOfStockException outOfStockException) {
            assertEquals(110, outOfStockException.getStockRequested());
        }
    }

    @Test
    public void testCalculateStockAvailable() throws OutOfStockException {
        cartService.add(cart, productId, 10);
        cartService.add(cart, productId, 10);
        Product product = productDao.getProduct(productId).get();
        int result = cartService.calculateStockAvailable(product, cart);
        int expectedResult = product.getStock() - 10 - 10;
        assertEquals(result, expectedResult);
    }

    @Test
    public void testGetProductHistory() {
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

}
