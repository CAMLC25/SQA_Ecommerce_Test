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
import java.util.List;

public class CustomerAddToCartTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testAddToCart() throws InterruptedException {
        System.out.println("--- Test Case CU_03: Thêm vào giỏ hàng (Final Fix) ---");

        driver.get("http://localhost/ecommerce-website-php/index.php");
        Thread.sleep(1000);

        // 1. Click "View Details"
        try {
            List<WebElement> viewBtns = driver.findElements(By.xpath("//a[contains(text(), 'View Details')]"));
            WebElement btnToClick = viewBtns.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnToClick);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnToClick);
        } catch (Exception e) {
            Assert.fail("Lỗi click sản phẩm.");
        }

        // 2. Chọn Size/Qty
        Thread.sleep(2000);
        try {
            new Select(driver.findElement(By.name("product_qty"))).selectByIndex(1); // Index 1
            new Select(driver.findElement(By.name("product_size"))).selectByIndex(1); // Index 1
            System.out.println("Đã chọn Size và Số lượng.");

            // 3. Bấm Add to Cart (Dùng Javascript Click cho chắc ăn)
            WebElement addBtn = driver.findElement(By.cssSelector("button[type='submit']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
            System.out.println("Đã bấm Add to Cart.");

        } catch (Exception e) {
            Assert.fail("Lỗi thao tác form.");
        }

        // 4. Kiểm tra và Điều hướng thủ công (Nếu web bị đơ)
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL sau khi bấm: " + currentUrl);

        // Nếu web không tự chuyển, mình ép nó chuyển sang cart.php để kiểm tra
        if (!currentUrl.contains("cart.php")) {
            System.out.println("Web chưa chuyển trang, Selenium tự vào Cart để kiểm tra...");
            driver.get("http://localhost/ecommerce-website-php/cart.php");
            Thread.sleep(1000);
        }

        // 5. Kiểm tra tiền trong giỏ hàng
        try {
            WebElement totalEl = driver.findElement(By.xpath("//tr[@class='total']/th"));
            String totalText = totalEl.getText();
            System.out.println("Tổng tiền trong giỏ: " + totalText);

            boolean isNotZero = !totalText.contains("$0.00");
            Assert.assertTrue(isNotZero, "Lỗi: Giá tiền vẫn là $0.00 (Thêm thất bại)!");

        } catch (Exception e) {
            Assert.fail("Không tìm thấy giá tiền trong giỏ hàng.");
        }

        System.out.println(">>> Test Passed: Thêm vào giỏ thành công!");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}