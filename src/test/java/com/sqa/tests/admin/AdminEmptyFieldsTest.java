package com.sqa.tests.admin;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class AdminEmptyFieldsTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        // 1. Đăng nhập Admin trước
        driver.get("http://localhost/ecommerce-website-php/admin_area/login.php");
        driver.findElement(By.name("admin_email")).sendKeys("admin@mail.com");
        driver.findElement(By.name("admin_pass")).sendKeys("Password@123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        try {
            Thread.sleep(1000);
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
    }

    @Test
    public void testSubmitEmptyForm() throws InterruptedException {
        System.out.println("--- Bắt đầu Test Case AD_05: Xử lý dữ liệu rỗng (Robustness) ---");

        // 2. Vào trang Thêm sản phẩm
        driver.get("http://localhost/ecommerce-website-php/admin_area/index.php?insert_product");
        Thread.sleep(1000);

        // 3. CHECK 1: Kiểm tra thuộc tính 'required' trong HTML (Validation phía Client)
        // Chúng ta kiểm tra 3 trường quan trọng nhất: URL, Price, Image

        WebElement urlInput = driver.findElement(By.name("product_url"));
        String isUrlRequired = urlInput.getAttribute("required");
        System.out.println("Trường URL có bắt buộc không? -> " + isUrlRequired);
        Assert.assertEquals(isUrlRequired, "true", "LỖI: Trường Product URL thiếu thuộc tính required!");

        WebElement priceInput = driver.findElement(By.name("product_price"));
        String isPriceRequired = priceInput.getAttribute("required");
        System.out.println("Trường Price có bắt buộc không? -> " + isPriceRequired);
        Assert.assertEquals(isPriceRequired, "true", "LỖI: Trường Product Price thiếu thuộc tính required!");

        WebElement imgInput = driver.findElement(By.name("product_img1"));
        String isImgRequired = imgInput.getAttribute("required");
        System.out.println("Trường Image 1 có bắt buộc không? -> " + isImgRequired);
        Assert.assertEquals(isImgRequired, "true", "LỖI: Trường Image 1 thiếu thuộc tính required!");

        // 4. CHECK 2: Cố tình bấm Submit khi chưa điền gì cả
        WebElement submitBtn = driver.findElement(By.name("submit"));

        // Scroll xuống để bấm
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        Thread.sleep(500);
        submitBtn.click();

        // 5. Kiểm tra kết quả
        // Mong đợi: KHÔNG có Alert "Success" hiện ra.
        // Trình duyệt sẽ chặn lại và hiện tooltip "Please fill out this field" (Selenium khó bắt tooltip này, nên ta check việc KHÔNG CÓ Alert success).

        boolean isSuccessAlertPresent = false;
        try {
            // Chờ xíu xem có alert không (set wait ngắn thôi)
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            Alert alert = driver.switchTo().alert();
            String text = alert.getText();

            if (text.contains("successfully")) {
                isSuccessAlertPresent = true;
                alert.accept();
            }
        } catch (Exception e) {
            // Không có alert -> Tốt! Nghĩa là form chưa được gửi đi.
            isSuccessAlertPresent = false;
        } finally {
            // Trả lại timeout mặc định
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }

        Assert.assertFalse(isSuccessAlertPresent,
                "LỖI ROBUSTNESS: Không điền gì cả mà vẫn Submit thành công!");

        System.out.println(">>> Test Passed: Hệ thống đã chặn việc gửi form rỗng!");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}