package com.sqa.tests.admin;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class AdminSecurityTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        // Khi khởi tạo ChromeDriver mới, nó là một trình duyệt sạch (không lưu Cookie/Session cũ)
        // Nghĩa là tương đương với người dùng CHƯA ĐĂNG NHẬP (Anonymous)
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testAccessDashboardWithoutLogin() {
        System.out.println("--- Bắt đầu Test Case AD_03: Kiểm soát truy cập trái phép ---");

        // 1. Cố tình truy cập thẳng vào trang Dashboard (Nơi chứa dữ liệu nhạy cảm)
        String dashboardUrl = "http://localhost/ecommerce-website-php/admin_area/index.php?dashboard";
        System.out.println("Cố gắng truy cập vào: " + dashboardUrl);
        driver.get(dashboardUrl);

        // 2. Kiểm tra URL hiện tại sau khi truy cập
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Hệ thống chuyển hướng đến: " + currentUrl);

        // 3. Validation (Kiểm tra)
        // Mong đợi: Hệ thống phải tự động chuyển hướng về trang 'login.php'
        boolean isRedirectedToLogin = currentUrl.contains("login.php");

        Assert.assertTrue(isRedirectedToLogin,
                "LỖI BẢO MẬT NGHIÊM TRỌNG: Vào được Dashboard mà không cần đăng nhập!");

        System.out.println(">>> Test Passed: Hệ thống đã chặn truy cập trái phép thành công!");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}