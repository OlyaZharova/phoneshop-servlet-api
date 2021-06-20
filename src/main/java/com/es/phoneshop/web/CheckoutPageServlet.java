package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.model.order.PaymentMethod;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CheckoutPageServlet extends HttpServlet {

    private CartService cartService;
    private OrderService orderService;


    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        request.setAttribute("order", orderService.getOrder(cart));
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);

        Map<String, String> errors = new HashMap<>();

        setRequiredParameter(request, "firstName", errors, order::setFirstName);
        setRequiredParameter(request, "lastName", errors, order::setLastName);
        setRequiredParameter(request, "phone", errors, order::setPhone);
        setDeliveryDate(request, errors, order);
        setRequiredParameter(request, "deliveryAddress", errors, order::setDeliveryAddress);
        setPaymentMethod(request, errors, order);

        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            cartService.clearCart(cart);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute("errors", errors);
            request.setAttribute("order", order);
            request.setAttribute("paymentMethods", orderService.getPaymentMethods());
            request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
        }
    }

    private void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors, Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, parameter + " is required");
        } else {
            consumer.accept(value);
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("paymentMethod");
        if (value == null || value.isEmpty()) {
            errors.put("paymentMethod", "Payment method is required");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("deliveryDate");
        if (value == null || value.isEmpty()) {
            errors.put("deliveryDate", "Delivery date is required");
        } else {
            String[] arrayValue = value.split("-");
            int year = Integer.valueOf(arrayValue[0]);
            int month = Integer.valueOf(arrayValue[1]);
            int day = Integer.valueOf(arrayValue[2]);
            LocalDate deliveryDate = LocalDate.of(year, month, day);
            LocalDate nowDate = LocalDate.now();
            if (deliveryDate.isEqual(nowDate) || deliveryDate.isAfter(nowDate)) {
                order.setDeliveryDate(LocalDate.of(year, month, day));
            } else {
                errors.put("deliveryDate", "Delivery date isn't correct");
            }

        }
    }

}
