package com.es.phoneshop.model.productHistory;


import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.web.ProductDetailsPageServlet;
import org.junit.Before;
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
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductHistoryServiceImplTest {
    private ProductHistoryService productHistoryService;

    private List<PriceHistory> histories;
    private ProductHistory productHistory;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;

    @Mock
    private HttpServletRequest request;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private HttpSession session;

    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() throws ServletException {
        productHistory = new ProductHistory();
        productHistoryService = ProductHistoryServiceImpl.getInstance();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(productHistory);
        servlet.init(servletConfig);

        Currency usd = Currency.getInstance("USD");
        histories = new ArrayList<>();
        product1 = new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(1000), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", histories);
        product2 = new Product(null, "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", histories);
        product3 = new Product(null, "sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", histories);
        product4 = new Product(null, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg", histories);

    }


    @Test
    public void testAddProductInHistory() {
        productHistoryService.add(productHistory, product1);
        ProductHistory newProductHistory = productHistoryService.getProductHistory(request);
        ConcurrentLinkedDeque<Product> result = newProductHistory.getProductHistory();
        assertTrue(result.contains(product1));
    }

    @Test
    public void testDeleteFourthProduct() {
        assertTrue(productHistory.getProductHistory().isEmpty());
        productHistoryService.add(productHistory, product1);
        productHistoryService.add(productHistory, product2);
        productHistoryService.add(productHistory, product3);
        productHistoryService.add(productHistory, product4);
        ProductHistory newProductHistory = productHistoryService.getProductHistory(request);
        ConcurrentLinkedDeque<Product> result = newProductHistory.getProductHistory();
        assertFalse(result.contains(product1));
    }

    @Test
    public void testAddTwoSimilarProducts() {
        productHistoryService.add(productHistory, product1);
        productHistoryService.add(productHistory, product1);
        int size = productHistory.getProductHistory().size();
        assertEquals(size, 1);
    }

    @Test
    public void testGetProductHistory() {
        productHistoryService.getProductHistory(request);
        verify(session).getAttribute(anyString());
    }

    @Test
    public void testGetProductHistoryWithNullProductHistory() {
        when(session.getAttribute(anyString())).thenReturn(null);
        ProductHistory newProductHistory = productHistoryService.getProductHistory(request);
        verify(session).setAttribute(anyString(), any());
        assertTrue(newProductHistory.getProductHistory().isEmpty());
    }
}
