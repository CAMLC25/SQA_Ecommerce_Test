package com.sqa.tests.customer;

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
import java.util.List;

public class CustomerViewDetailsTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testViewProductDetails() throws InterruptedException {
        System.out.println("--- Test Case CU_01: Xem chi tiết sản phẩm (Click View Details) ---");

        // 1. Vào trang chủ
        driver.get("http://localhost/ecommerce-website-php/index.php");
        Thread.sleep(1000);

        // 2. Tìm và Click nút "View Details"
        try {
            // Cách tìm chuẩn nhất: Tìm thẻ <a> có chứa chữ "View Details"
            // Lấy danh sách tất cả các nút View Details
            List<WebElement> viewBtns = driver.findElements(By.xpath("//a[contains(text(), 'View Details')]"));

            if (viewBtns.isEmpty()) {
                Assert.fail("Lỗi: Không tìm thấy nút 'View Details' nào trên trang chủ!");
            }

            // Chọn nút đầu tiên tìm thấy
            WebElement firstBtn = viewBtns.get(0);

            // Scroll xuống để chắc chắn nút hiển thị trên màn hình (tránh bị che)
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstBtn);
            Thread.sleep(1000); // Chờ 1 xíu sau khi scroll

            System.out.println("Đã tìm thấy nút View Details. Đang click...");

            // Dùng Javascript click cho chắc ăn (tránh lỗi ElementClickIntercepted)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstBtn);
            // Hoặc: firstBtn.click();

        } catch (Exception e) {
            Assert.fail("Lỗi khi thao tác click nút View Details: " + e.getMessage());
        }

        // 3. Kiểm tra kết quả
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL hiện tại: " + currentUrl);

        // Kiểm tra URL có chuyển sang trang chi tiết không (có chứa 'details.php' hoặc tên sản phẩm)
        // Dựa vào code PHP của bạn, url thường là: details.php?pro_id=... hoặc tên-san-pham
        boolean isDetailsPage = currentUrl.contains("details.php") ||
                currentUrl.contains("product-url") || // URL thân thiện
                driver.getTitle().contains("Details") || // Tiêu đề trang
                driver.getPageSource().contains("Product Description"); // Nội dung trang

        Assert.assertTrue(isDetailsPage, "Lỗi: Chưa chuyển hướng vào trang chi tiết sản phẩm!");

        System.out.println(">>> Test Passed: Đã vào xem chi tiết thành công.");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}