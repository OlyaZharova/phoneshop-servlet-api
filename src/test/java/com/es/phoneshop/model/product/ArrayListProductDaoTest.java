package com.es.phoneshop.model.product;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private ProductDao productDao;
    private SortField sortField;
    private SortOrder sortOrder;
    private Currency usd;
    private List<PriceHistory> histories;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        sortField = SortField.description;
        sortOrder = SortOrder.desc;
        usd = Currency.getInstance("USD");
        histories = new ArrayList<>();
        histories.add(new PriceHistory(LocalDate.of(2018, 9, 01), new BigDecimal(100), usd));
        histories.add(new PriceHistory(LocalDate.of(2018, 10, 10), new BigDecimal(110), usd));
        histories.add(new PriceHistory(LocalDate.of(2019, 1, 10), new BigDecimal(150), usd));
    }

    @After
    public void cleanArray() {
        productDao.deleteAll();
    }

    @Test
    public void testFindProductsNoResults() {
        assertTrue(productDao.findProducts(null, sortField, sortOrder).isEmpty());
    }

    @Test
    public void testSaveNewProduct() {
        Product product = new Product(null, "test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(product);
        assertTrue(product.getId() != null);
        Optional<Product> result = productDao.getProduct(product.getId());
        Boolean present = result.isPresent();
        assertTrue(present);
        Product testProduct = result.get();
        assertEquals("test-product", testProduct.getCode());
    }

    @Test
    public void testSaveExistingProduct() {
        Product product = new Product(null, "test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(product);
        Product newProduct = new Product(product.getId(), "new-test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(newProduct);
        List<Product> result = productDao.findProducts(null, null, null);
        assertFalse(result.contains(product));
        assertTrue(result.contains(newProduct));
    }

    @Test
    public void testSaveNotExistingProductWithId() {
        Product product = new Product(null, "test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(product);
        Product newProduct = new Product(product.getId() + 1, "new-test-product", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(newProduct);
        List<Product> result = productDao.findProducts(null, null, null);
        assertFalse(result.contains(newProduct));
    }


    @Test
    public void testDeleteProduct() {
        Product product = new Product(null, "test-product", "Siemens SXG75", new BigDecimal(160), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(product);
        productDao.delete(Long.valueOf(product.getId()));
        List<Product> result = productDao.findProducts(null, null, null);
        assertFalse(result.contains(product));
    }

    @Test
    public void testDeleteNotExistingProduct() {
        Product product = new Product(1l, "test-product", "Siemens SXG75", new BigDecimal(160), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.delete(product.getId());
        List<Product> listAfterDelete = productDao.findProducts(null, sortField, sortOrder);
        assertEquals(0, listAfterDelete.size());
    }

    @Test
    public void testFindProductsWithZeroStock() {
        Product product = new Product(null, "test-product", "Siemens SXG75", new BigDecimal(150), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(product);
        List<Product> result = productDao.findProducts(null, null, null);
        assertFalse(result.contains(product));
    }

    @Test
    public void testFindProductsWithNullPrice() {
        Product product = new Product(null, "test-product", "Siemens SXG75", null, usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(product);
        List<Product> result = productDao.findProducts(null, null, null);
        assertFalse(result.contains(product));
    }

    @Test
    public void testFindProductsByQuery() {
        Product product1 = new Product(null, "test-product1", "Siemens SXG75", new BigDecimal(150), usd, 120, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        Product product2 = new Product(null, "test-product2", "Apple IPhone 6", new BigDecimal(150), usd, 130, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        productDao.save(product1);
        productDao.save(product2);
        List<Product> result = productDao.findProducts("IPhone", null, null);
        assertEquals(result.size(), 1);
        assertFalse(result.contains(product1));
        assertTrue(result.contains(product2));
    }

    @Test
    public void testSorting() {
        Product product1 = new Product(null, "test-product1", "Siemens SXG75", new BigDecimal(150), usd, 120, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);
        Product product2 = new Product(null, "test-product2", "Apple IPhone 6", new BigDecimal(1000), usd, 130, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", histories);

        productDao.save(product1);
        productDao.save(product2);

        List<Product> sortByPriceAsc = productDao.findProducts(null, SortField.price, SortOrder.asc);
        assertEquals(sortByPriceAsc.get(1), product2);

        List<Product> sortByDescriptionDesc = productDao.findProducts(null, sortField, sortOrder);
        assertEquals(sortByDescriptionDesc.get(0).getDescription(), product1.getDescription());
    }


}
