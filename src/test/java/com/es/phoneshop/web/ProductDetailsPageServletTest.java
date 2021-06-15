package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
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
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {

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

    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();
    private ProductHistory productHistory = new ProductHistory();
    private List<PriceHistory> histories;
    private ProductDao productDao;
    private Cart cart;
    private static final String PRODUCT_HISTORY_SESSION_ATTRIBUTE = ProductHistoryServiceImpl.class.getName() + ".history";
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    @Before
    public void setup() throws ServletException {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        histories = new ArrayList<>();
        cart = new Cart();
        productDao.save(new Product(null, "test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories));
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(PRODUCT_HISTORY_SESSION_ATTRIBUTE)).thenReturn(productHistory);
        when(session.getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(cart);
        when(request.getPathInfo()).thenReturn("/0");
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("product"), any());
        verify(request).setAttribute(eq("cart"), any());
        verify(request).setAttribute(eq("productHistory"), any());
    }

    @Test
    public void testDoPost() throws ServletException, IOException, ParseException {
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        servlet.doPost(request, response);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testIncorrectQuantity() throws ServletException, IOException {
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getParameter("quantity")).thenReturn("ffff");
        servlet.doPost(request, response);
        verify(request, times(4)).setAttribute(anyString(), any());
    }

    @Test
    public void testQuantityLessThanOne() throws ServletException, IOException {
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getParameter("quantity")).thenReturn("0");
        servlet.doPost(request, response);
        verify(request, times(4)).setAttribute(anyString(), any());
    }

    @Test
    public void testQuantityMoreStock() throws ServletException, IOException {
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getParameter("quantity")).thenReturn("1000");
        servlet.doPost(request, response);
        verify(request, times(4)).setAttribute(anyString(), any());
    }

    @Test
    public void testProductIsntPresent() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");
        servlet.doGet(request, response);
        verify(response).sendError(404);
    }

}