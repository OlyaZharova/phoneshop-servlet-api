package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
    }

    @Test
    public void testFindProductsNoResults() {
        assertFalse(productDao.findProducts().isEmpty());
    }

    @Test
    public void testSaveNewProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(product);
        assertTrue(product.getId() > 0);
        Product result = productDao.getProduct(Long.valueOf(product.getId()));
        assertNotNull(result);
        assertEquals("test-product", result.getCode());
    }

    @Test
    public void testDeleteProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(product);
        productDao.delete(Long.valueOf(product.getId()));
        List<Product> result = productDao.findProducts();
        assertFalse(result.contains(product));
    }

    @Test
    public void testFindProductsWithZeroStock() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Siemens SXG75", new BigDecimal(150), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(product);
        List<Product> result = productDao.findProducts();
        assertFalse(result.contains(product));
    }

    @Test
    public void testFindProductsWithNullPrice() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Siemens SXG75", null, usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(product);
        List<Product> result = productDao.findProducts();
        assertFalse(result.contains(product));
    }
}
