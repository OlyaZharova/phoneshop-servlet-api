package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.productHistory.ProductHistory;
import com.es.phoneshop.model.productHistory.ProductHistoryServiceImpl;

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
public class ProductListPageServletTest {
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

    private ProductListPageServlet servlet = new ProductListPageServlet();
    private ProductHistory productHistory = new ProductHistory();
    private static final String PRODUCT_HISTORY_SESSION_ATTRIBUTE = ProductHistoryServiceImpl.class.getName() + ".history";
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private Cart cart;
    private ProductDao productDao;

    @Before
    public void setup() throws ServletException {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(1000), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", null);
        productDao.save(product);
        cart = new Cart();
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(productHistory);
        when(request.getParameter("productId")).thenReturn(product.getId().toString());
        when(session.getAttribute(PRODUCT_HISTORY_SESSION_ATTRIBUTE)).thenReturn(productHistory);
        when(session.getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(cart);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("products"), any());
        verify(request).setAttribute(eq("productHistory"), any());
    }

    @Test
    public void testDoPost() throws ServletException, IOException, ParseException {
        when(request.getParameter("quantity")).thenReturn("10");
        servlet.doPost(request, response);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testIncorrectQuantity() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("ffff");
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
    }

    @Test
    public void testQuantityLessThanOne() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("0");
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
    }

    @Test
    public void testQuantityMoreStock() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("1000");
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
    }

    @Test
    public void testProductNotFound() throws ServletException, IOException {
        when(request.getParameter("productId")).thenReturn("-1");
        when(request.getParameter("quantity")).thenReturn("10");
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
        verify(response, times(0)).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
    }
}