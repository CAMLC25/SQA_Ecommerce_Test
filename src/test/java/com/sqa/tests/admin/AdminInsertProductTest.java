package com.sqa.tests.admin;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor; // Thêm thư viện này
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class AdminInsertProductTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        // --- 1. ĐĂNG NHẬP TRƯỚC ---
        driver.get("http://localhost/ecommerce-website-php/admin_area/login.php");
        driver.findElement(By.name("admin_email")).sendKeys("admin@mail.com");
        driver.findElement(By.name("admin_pass")).sendKeys("Password@123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Xử lý Alert đăng nhập
        try {
            Thread.sleep(1000);
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
    }

    @Test
    public void testInsertProduct() throws InterruptedException {
        System.out.println("--- Bắt đầu Test Case 02: Thêm sản phẩm ---");

        // 1. Vào trang Thêm sản phẩm
        driver.get("http://localhost/ecommerce-website-php/admin_area/index.php?insert_product");
        Thread.sleep(1000);

        // 2. Điền thông tin cơ bản
        driver.findElement(By.name("product_title")).sendKeys("Ao Thun SQA Automation");
        driver.findElement(By.name("product_url")).sendKeys("ao-thun-sqa-auto");

        // 3. Dropdown
        try {
            Select manufacturer = new Select(driver.findElement(By.name("manufacturer")));
            manufacturer.selectByIndex(1);
        } catch (Exception e) {}

        Select productCat = new Select(driver.findElement(By.name("product_cat")));
        productCat.selectByIndex(1);
        Select cat = new Select(driver.findElement(By.name("cat")));
        cat.selectByIndex(1);

        // 4. Upload ảnh (D:\product_test.jpg phải tồn tại nhé)
        String imagePath = "D:\\Hoc\\HK6\\Software quality assurance and testing\\product_test.png";
        driver.findElement(By.name("product_img1")).sendKeys(imagePath);
        driver.findElement(By.name("product_img2")).sendKeys(imagePath);
        driver.findElement(By.name("product_img3")).sendKeys(imagePath);

        // 5. Giá tiền
        driver.findElement(By.name("product_price")).sendKeys("500000");
        driver.findElement(By.name("psp_price")).sendKeys("450000");
        driver.findElement(By.name("product_keywords")).sendKeys("test");
        driver.findElement(By.name("product_label")).sendKeys("New");

        // --- [FIX LỖI] BỎ QUA 2 TRƯỜNG DESCRIPTION & FEATURES ---
        // Vì Editor TinyMCE che mất ô input nên Selenium không điền được.
        // 2 ô này không bắt buộc nên ta bỏ qua để Test Case chạy thành công.
        // driver.findElement(By.name("product_desc")).sendKeys("..."); // Đã comment lại
        // driver.findElement(By.name("product_features")).sendKeys("..."); // Đã comment lại

        // 6. Scroll xuống cuối trang để thấy nút Submit (Tránh bị che)
        WebElement submitBtn = driver.findElement(By.name("submit"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        Thread.sleep(500);

        // 7. Bấm nút
        submitBtn.click();

        // 8. Kiểm tra kết quả
        Thread.sleep(3000); // Chờ upload xong
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("Kết quả thông báo: " + alertText);

            Assert.assertTrue(alertText.contains("successfully"), "Lỗi: Không thêm được sản phẩm!");
            alert.accept();
        } catch (Exception e) {
            Assert.fail("Test Failed: Không hiện thông báo thành công (Có thể do chưa chọn đủ ảnh hoặc giá trị bắt buộc).");
        }

        System.out.println(">>> Test Passed: Thêm sản phẩm thành công!");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}