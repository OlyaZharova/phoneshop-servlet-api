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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private HttpSession session;

    private DeleteCartItemServlet servlet = new DeleteCartItemServlet();
    private Cart cart;
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private String productId;
    private ProductDao productDao;

    @Before
    public void setup() throws ServletException {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        cart = new Cart();
        Product product = new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(1000), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", null);
        productDao.save(product);
        productId = product.getId().toString();
        servlet.init(servletConfig);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(cart);
        when(request.getPathInfo()).thenReturn("/0");
    }

    @Test
    public void testDoPost() throws ServletException, IOException, ParseException {
        when(request.getPathInfo()).thenReturn("/" + productId);
        servlet.doPost(request, response);
        verify(request).getPathInfo();
        verify(response).sendRedirect(anyString());
    }


}