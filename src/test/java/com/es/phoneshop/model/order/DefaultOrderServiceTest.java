package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest {

    private DefaultOrderService orderService;

    private static List<PriceHistory> histories;
    private Cart cart;
    private static ProductDao productDao;
    private static Product product;
    private Order order;
    private String firstName = "fff";
    private String lastName = "fff";
    private String phone = "fff";
    private String deliveryAddress = "fff";
    private PaymentMethod paymentMethod = PaymentMethod.CACHE;


    @BeforeClass
    public static void saveProduct() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        histories = new ArrayList<>();
        product = new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(1000), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", histories);
        productDao.save(product);
    }

    @AfterClass
    public static void deleteProduct() {
        productDao.deleteAll();
    }

    @Before
    public void setup() throws ServletException {
        cart = new Cart();
        CartItem cartItem = new CartItem(product, 10);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);
        cart.setItems(items);
        cart.setTotalCost(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cart.setTotalQuantity(cartItem.getQuantity());
        orderService = DefaultOrderService.getInstance();
        order = new Order(null, new BigDecimal(20), new BigDecimal(5), firstName, lastName, phone, LocalDate.of(2021, 12, 12), deliveryAddress, paymentMethod);
    }

    @Test
    public void testGetOrder() {
        Order order = orderService.getOrder(cart);
        assertFalse(order.getItems().isEmpty());
        assertEquals(order.getItems().get(0).getQuantity(), 10);
        assertEquals(order.getDeliveryCost(), new BigDecimal(5));
    }

    @Test
    public void testGetPaymentMethods() {
        List<PaymentMethod> paymentMethods = orderService.getPaymentMethods();
        assertFalse(paymentMethods.isEmpty());
    }

    @Test
    public void testPlaceOrder() {
        orderService.placeOrder(order);
        assertFalse(order.getSecureId().isEmpty());
    }
}
