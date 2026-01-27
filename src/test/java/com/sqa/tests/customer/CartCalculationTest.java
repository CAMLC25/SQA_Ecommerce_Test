package com.sqa.tests.customer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class CartCalculationTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    // --- CÁC HÀM HỖ TRỢ ---
    public void login() {
        driver.get("http://localhost/ecommerce-website-php/checkout.php");
        try {
            driver.findElement(By.name("c_email")).sendKeys("user@ave.com");
            driver.findElement(By.name("c_pass")).sendKeys("123");
            driver.findElement(By.name("login")).click();
            Thread.sleep(1000);
            try { driver.switchTo().alert().accept(); } catch (Exception e) {}
        } catch (Exception e) {}
    }

    public void addProductToCart() throws InterruptedException {
        driver.get("http://localhost/ecommerce-website-php/index.php");
        try {
            WebElement productLink = driver.findElement(By.cssSelector(".product a"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", productLink);
            productLink.click();
            Thread.sleep(1000);

            new Select(driver.findElement(By.name("product_qty"))).selectByIndex(0);
            new Select(driver.findElement(By.name("product_size"))).selectByIndex(1);
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            Thread.sleep(1000);
        } catch (Exception e) {}
    }

    public double parsePrice(String text) {
        return Double.parseDouble(text.replaceAll("[$,\\s]", ""));
    }

    // --- TEST CASE CHÍNH ---
    @Test
    public void testCartCalculation() throws InterruptedException {
        System.out.println("--- Test Case CU_04: Kiểm tra tính tiền (Số lượng 3) ---");

        login();
        addProductToCart();

        // Vào trang giỏ hàng
        driver.get("http://localhost/ecommerce-website-php/cart.php");

        // 1. Lấy đơn giá (Unit Price)
        WebElement priceElement = driver.findElement(By.xpath("//tbody/tr[1]/td[4]"));
        double unitPrice = parsePrice(priceElement.getText());
        System.out.println("Đơn giá gốc: $" + unitPrice);

        // 2. Nhập số lượng: 3
        WebElement qtyInput = driver.findElement(By.cssSelector("input.quantity"));
        qtyInput.clear();
        qtyInput.sendKeys("3");

        // 3. Bấm Update Cart
        driver.findElement(By.name("update")).click();
        Thread.sleep(2000);

        // 4. Kiểm tra Tổng tiền (Total)
        WebElement totalElement = driver.findElement(By.xpath("//tr[@class='total']/th"));
        double actualTotal = parsePrice(totalElement.getText());

        double expectedTotal = unitPrice * 3;
        System.out.println("Tổng tiền Web tính: $" + actualTotal);

        Assert.assertEquals(actualTotal, expectedTotal, "Lỗi: Web tính sai tiền!");
        System.out.println(">>> Test Passed: Tính toán đúng!");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}