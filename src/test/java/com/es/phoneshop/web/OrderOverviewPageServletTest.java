package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.model.product.ProductNotFoundException;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;

    private OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();
    private OrderService orderService;
    private String firstName = "fff";
    private String lastName = "fff";
    private String phone = "fff";
    private String deliveryAddress = "fff";
    private PaymentMethod paymentMethod = PaymentMethod.CACHE;
    private Order order;
    private String securityId;


    @Before
    public void setup() throws ServletException, ProductNotFoundException, OutOfStockException {
        orderService = DefaultOrderService.getInstance();
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        order = new Order(null, new BigDecimal(20), new BigDecimal(5), firstName, lastName, phone, LocalDate.of(2021, 12, 12), deliveryAddress, paymentMethod);
        orderService.placeOrder(order);
        securityId = order.getSecureId();
        when(request.getPathInfo()).thenReturn("/" + securityId);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("order"), any());
    }
}