package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
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
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {

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

    private CartPageServlet servlet = new CartPageServlet();
    private Cart cart;
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private String[] productIds = new String[1];
    private ProductDao productDao;

    @Before
    public void setup() throws ServletException {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        cart = new Cart();
        Product product = new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(1000), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", null);
        productDao.save(product);
        productIds[0] = product.getId().toString();
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(cart);
        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("cart"), any());
    }

    @Test
    public void testDoPost() throws ServletException, IOException, ParseException {
        String[] quantities = {"10"};
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        servlet.doPost(request, response);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testIncorrectQuantity() throws ServletException, IOException {
        String[] quantities = {"fff"};
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        servlet.doPost(request, response);
        verify(request, times(2)).setAttribute(anyString(), any());
        verify(response, times(0)).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");

    }

    @Test
    public void testQuantityLessThanOne() throws ServletException, IOException {
        String[] quantities = {"0"};
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        servlet.doPost(request, response);
        verify(request, times(2)).setAttribute(anyString(), any());
        verify(response, times(0)).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
    }

    @Test
    public void testQuantityMoreStock() throws ServletException, IOException {
        String[] quantities = {"1000"};
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        servlet.doPost(request, response);
        verify(request, times(2)).setAttribute(anyString(), any());
        verify(response, times(0)).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
    }

    @Test
    public void testProductNotFound() throws ServletException, IOException {
        String[] quantities = {"1000"};
        String[] productIds = {"-1"};
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        when(request.getParameterValues("productId")).thenReturn(productIds);
        servlet.doPost(request, response);
        verify(request, times(2)).setAttribute(anyString(), any());
        verify(response, times(0)).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
    }


}