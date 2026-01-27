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

public class CartNegativeQuantityTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    // --- CÁC HÀM HỖ TRỢ (Copy lại để chạy độc lập) ---
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
    public void testNegativeQuantity() throws InterruptedException {
        System.out.println("--- Test Case CU_05: Nhập số âm (-5) ---");

        login();
        addProductToCart();

        driver.get("http://localhost/ecommerce-website-php/cart.php");

        // 1. Nhập số âm: -5
        WebElement qtyInput = driver.findElement(By.cssSelector("input.quantity"));
        qtyInput.clear();
        qtyInput.sendKeys("-5");

        // 2. Bấm Update
        driver.findElement(By.name("update")).click();
        Thread.sleep(2000);

        // 3. Lấy Tổng tiền
        WebElement totalElement = driver.findElement(By.xpath("//tr[@class='total']/th"));
        double currentTotal = parsePrice(totalElement.getText());
        System.out.println("Tổng tiền sau khi nhập -5: $" + currentTotal);

        // 4. Đánh giá Bug
        if (currentTotal < 0) {
            String msg = "PHÁT HIỆN LỖI (BUG): Hệ thống cho phép nhập số lượng âm! Tiền bị âm.";
            System.err.println(msg);
            Assert.fail(msg); // Báo đỏ để biết là Bug
        } else {
            System.out.println(">>> Test Passed: Hệ thống chặn số âm tốt.");
        }
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}