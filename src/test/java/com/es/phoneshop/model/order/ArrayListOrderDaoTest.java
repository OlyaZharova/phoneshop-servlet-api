package com.es.phoneshop.model.order;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ArrayListOrderDaoTest {

    private OrderDao orderDao;
    private DefaultOrderService orderService;
    private Order order;
    private String firstName = "fff";
    private String lastName = "fff";
    private String phone = "fff";
    private String deliveryAddress = "fff";
    private PaymentMethod paymentMethod = PaymentMethod.CACHE;
    private String secureId;

    @Before
    public void setup() throws ServletException {
        orderDao = ArrayListOrderDao.getInstance();
        orderService = DefaultOrderService.getInstance();
        order = new Order(null, new BigDecimal(20), new BigDecimal(5), firstName, lastName, phone, LocalDate.of(2021, 12, 12), deliveryAddress, paymentMethod);
        orderService.placeOrder(order);
        secureId = order.getSecureId();
    }

    @Test
    public void testGetOrderBySecureId() throws OrderNotFoundException {
        orderDao.save(order);
        Order result = orderDao.getOrderBySecureId(secureId);
        assertFalse(result.getId() == null);
        assertEquals(result.getFirstName(), firstName);
        assertEquals(result.getDeliveryCost(), new BigDecimal(5));
    }

    @Test
    public void testGetOrderBySecureIdException() {
        String incorrectSecureId = UUID.randomUUID().toString();
        try {
            orderDao.getOrderBySecureId(incorrectSecureId);
            fail("Expected OrderNotFoundException");
        } catch (OrderNotFoundException orderNotFoundException) {
            assertNotEquals("", orderNotFoundException.getMessage());
        }
    }

    @Test
    public void testGetOrder() throws OrderNotFoundException {
        orderDao.save(order);
        Order result = orderDao.getOrder(order.getId());
        assertFalse(result.getId() == null);
        assertEquals(result.getFirstName(), firstName);
        assertEquals(result.getDeliveryCost(), new BigDecimal(5));
    }

    @Test
    public void testGetOrderException() {
        Long id = order.getId() + 1;
        try {
            orderDao.getOrder(id);
            fail("Expected OrderNotFoundException");
        } catch (OrderNotFoundException orderNotFoundException) {
            assertNotEquals("", orderNotFoundException.getMessage());
        }
    }

    @Test
    public void testSaveNewOrder() {
        orderDao.save(order);
        assertTrue(order.getId() != null);
        Order result = orderDao.getOrder(order.getId());
        assertEquals(firstName, result.getFirstName());
    }

    @Test
    public void testSaveExistingOrder() {
        orderDao.save(order);
        Order newOrder = new Order(order.getId(), new BigDecimal(20), new BigDecimal(5), "qqq", "qqq", "qqq", LocalDate.of(2021, 12, 12), "qqq", PaymentMethod.CACHE);
        orderDao.save(newOrder);
        Order result = orderDao.getOrder(order.getId());
        assertNotEquals(firstName, result.getFirstName());
        assertEquals("qqq", result.getFirstName());
    }
}
