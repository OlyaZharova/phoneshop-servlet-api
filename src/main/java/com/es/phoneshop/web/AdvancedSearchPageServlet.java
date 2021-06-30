package com.es.phoneshop.web;

import com.es.phoneshop.model.advancedSearch.AdvancedSearchService;
import com.es.phoneshop.model.advancedSearch.AdvancedSearchServiceImpl;
import com.es.phoneshop.model.advancedSearch.SearchParams;
import com.es.phoneshop.model.product.Product;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AdvancedSearchPageServlet extends HttpServlet {

    private AdvancedSearchService searchService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        searchService = AdvancedSearchServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        SearchParams params = new SearchParams();

        String description = request.getParameter("description");
        if (description != null || !description.isEmpty()) {
            params.setDescription(description);
        }
        try {
            parsePrice(request, "minPrice", errors, params::setMinPrice);
        } catch (ParseException e) {
            errors.put("minPrice", "Not a number");
        }
        try {
            parsePrice(request, "maxPrice", errors, params::setMaxPrice);
        } catch (ParseException e) {
            errors.put("maxPrice", "Not a number");
        }
        String choiceWordString = request.getParameter("choiceWord");
            boolean choiceWord = Boolean.parseBoolean(choiceWordString);
            params.setChoiceWord(choiceWord);
        if (errors.isEmpty()) {
            List<Product> products = searchService.findProduct(params);
            request.setAttribute("products", products);
            doGet(request, response);
        } else {
            request.setAttribute("errors", errors);
            response.setHeader("Cache-control", "no-cache, no-store");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "-1");
            request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);
        }
    }

    private void parsePrice(HttpServletRequest request, String parameter, Map<String, String> errors, Consumer<Integer> consumer) throws ParseException {
        String value = request.getParameter(parameter);
        boolean flag = value.matches("\\d+");
        if (flag) {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            int price = format.parse(value).intValue();
            if (price > 0) {
                consumer.accept(price);
            } else {
                errors.put(parameter, parameter + "must be more than zero");
            }
        }
    }
}
