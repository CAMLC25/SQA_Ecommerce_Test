package com.sqa.tests.admin;

import org.openqa.selenium.Alert; // Nhớ import thư viện này
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class AdminLoginTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testAdminLoginSuccess() throws InterruptedException {
        System.out.println("--- Bắt đầu Test Case 01 ---");

        // 1. Vào trang Login
        driver.get("http://localhost/ecommerce-website-php/admin_area/login.php");

        // 2. Nhập Email & Pass
        driver.findElement(By.name("admin_email")).sendKeys("admin@mail.com");
        driver.findElement(By.name("admin_pass")).sendKeys("Password@123");

        // 3. Bấm Login
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 4. [FIX LỖI] Xử lý Alert (Thông báo đăng nhập thành công)
        Thread.sleep(1000); // Chờ 1 xíu cho bảng hiện ra
        try {
            Alert alert = driver.switchTo().alert(); // Chuyển quyền điều khiển sang cái bảng
            String alertText = alert.getText();
            System.out.println("Thông báo từ Web: " + alertText); // In ra để báo cáo

            // Assert luôn cái chữ trong bảng thông báo (Test tính Correctness)
            Assert.assertTrue(alertText.contains("Logged in"), "Thông báo không đúng!");

            alert.accept(); // Bấm nút OK trên bảng
            System.out.println("-> Đã bấm OK đóng bảng thông báo.");
        } catch (Exception e) {
            System.out.println("Không thấy bảng thông báo nào cả!");
        }

        // 5. Chờ chuyển trang và Kiểm tra URL
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL sau khi đăng nhập: " + currentUrl);

        boolean isLoginSuccess = currentUrl.contains("index.php") || currentUrl.contains("dashboard");
        Assert.assertTrue(isLoginSuccess, "LỖI: Chưa vào được trang Dashboard!");

        System.out.println(">>> Test Passed: Đăng nhập thành công!");
    }

    @AfterClass
    public void tearDown() {
        // Mở dòng này ra nếu muốn tự tắt trình duyệt
        // if (driver != null) driver.quit();
    }
}