package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.ProductNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private HttpSession session;

    private CheckoutPageServlet servlet = new CheckoutPageServlet();
    private Cart cart;
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private Long productId;
    private ProductDao productDao;
    private CartService cartService;
    private String firstName = "fff";
    private String lastName = "fff";
    private String phone = "fff";

    private String deliveryDate = "2021-11-11";
    private String deliveryAddress = "fff";

    private String paymentMethod = PaymentMethod.CACHE.toString();
    private Map<String, String> errors = new HashMap<>();


    @Before
    public void setup() throws ServletException, ProductNotFoundException, OutOfStockException {
        cartService = DefaultCartService.getInstance();
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        cart = new Cart();
        Product product = new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(1000), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", null);
        productDao.save(product);
        productId = product.getId();
        cartService.add(cart, productId, 10);
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(cart);
        when(request.getParameter("firstName")).thenReturn(firstName);
        when(request.getParameter("lastName")).thenReturn(lastName);
        when(request.getParameter("phone")).thenReturn(phone);
        when(request.getParameter("deliveryDate")).thenReturn(deliveryDate);
        when(request.getParameter("deliveryAddress")).thenReturn(deliveryAddress);
        when(request.getParameter("paymentMethod")).thenReturn(paymentMethod);
    }

    @After
    public void deleteProduct() {
        productDao.deleteAll();
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        servlet.doPost(request, response);
        verify(response).sendRedirect(anyString());
    }


    @Test
    public void testSetRequiredParameter() throws ServletException, IOException {
        firstName = "";
        when(request.getParameter("firstName")).thenReturn(firstName);
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testSetPaymentMethod() throws ServletException, IOException {
        paymentMethod = "";
        when(request.getParameter("paymentMethod")).thenReturn(paymentMethod);
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testSetDeliveryDate() throws ServletException, IOException {
        deliveryDate = "";
        when(request.getParameter("deliveryDate")).thenReturn(deliveryDate);
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testSetDeliveryDateBeforeToday() throws ServletException, IOException {
        deliveryDate = "2020-10-10";
        when(request.getParameter("deliveryDate")).thenReturn(deliveryDate);
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testSetDeliveryDateIncorrect() throws ServletException, IOException {
        deliveryDate = "ffff";
        when(request.getParameter("deliveryDate")).thenReturn(deliveryDate);
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(requestDispatcher).forward(request, response);
    }
}