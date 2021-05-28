package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

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
        Optional<Product> result = productDao.getProduct(product.getId());
        Boolean present = result.isPresent();
        assertTrue(present);
        Product testProduct = result.get();
        assertEquals("test-product", testProduct.getCode());
    }

    @Test
    public void testSaveExistingProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(product);
        Product newProduct = new Product(product.getId(), "new-test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(newProduct);
        List<Product> result = productDao.findProducts();
        assertFalse(result.contains(product));
        assertTrue(result.contains(newProduct));
    }

    @Test
    public void testSaveNotExistingProductWithId() {
        Currency usd = Currency.getInstance("USD");
        int lenght = productDao.findProducts().size();
        Product product = new Product((long) lenght + 1, "test-product3", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(product);
        List<Product> result = productDao.findProducts();
        assertFalse(result.contains(product));
    }


    @Test
    public void testDeleteProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Siemens SXG75", new BigDecimal(160), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(product);
        productDao.delete(Long.valueOf(product.getId()));
        List<Product> result = productDao.findProducts();
        assertFalse(result.contains(product));
    }

    @Test
    public void testDeleteNotExistingProduct() {
        Currency usd = Currency.getInstance("USD");
        int lenght = productDao.findProducts().size();
        Product product = new Product((long) lenght + 1, "test-product", "Siemens SXG75", new BigDecimal(160), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.delete(product.getId());
        List<Product> result = productDao.findProducts();
        assertEquals(lenght, result.size());
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
