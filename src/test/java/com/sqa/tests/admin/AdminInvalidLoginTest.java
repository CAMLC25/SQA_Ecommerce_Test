package com.sqa.tests.admin;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class AdminInvalidLoginTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testLoginWithWrongPassword() throws InterruptedException {
        System.out.println("--- Bắt đầu Test Case AD_02: Đăng nhập sai mật khẩu ---");

        // 1. Truy cập URL Admin
        driver.get("http://localhost/ecommerce-website-php/admin_area/login.php");

        // 2. Nhập Email đúng (admin@mail.com)
        driver.findElement(By.name("admin_email")).sendKeys("admin@mail.com");

        // 3. Nhập Password SAI (ví dụ: 12345678)
        driver.findElement(By.name("admin_pass")).sendKeys("wrongpassword123");

        // 4. Click nút Login
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 5. Kiểm tra kết quả (Validation)
        // Kịch bản mong đợi: Hệ thống hiện Alert thông báo lỗi và KHÔNG chuyển trang
        Thread.sleep(1000); // Chờ Alert hiện ra
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("Thông báo lỗi từ Web: " + alertText);

            // Kiểm tra nội dung thông báo lỗi
            // Web này thường báo: "Email or Password is Wrong !"
            Assert.assertTrue(alertText.contains("Wrong") || alertText.contains("sai"),
                    "Lỗi: Không hiện thông báo sai mật khẩu!");

            alert.accept(); // Đóng bảng thông báo
        } catch (Exception e) {
            Assert.fail("Test Failed: Hệ thống không hiện cảnh báo khi nhập sai pass!");
        }

        // Kiểm tra chắc chắn là vẫn đang ở trang Login, chưa vào Dashboard
        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("dashboard"),
                "LỖI BẢO MẬT: Nhập sai pass mà vẫn vào được Dashboard!");

        System.out.println(">>> Test Passed: Hệ thống chặn đăng nhập sai thành công!");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}