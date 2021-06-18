package com.es.phoneshop.util;

import javax.servlet.http.HttpServletRequest;
import java.text.NumberFormat;
import java.text.ParseException;

public class QuantityUtility {

    public static int getQuantity(String quantityString, HttpServletRequest request) throws ParseException {
        boolean flag = quantityString.matches("\\d+");
        if (flag) {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            return format.parse(quantityString).intValue();
        } else {
            throw new ParseException(quantityString, -1);
        }
    }
}
