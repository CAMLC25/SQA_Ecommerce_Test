package com.sqa.tests.admin;

import org.openqa.selenium.Alert;
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

public class AdminFileUploadTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        // 1. Đăng nhập Admin
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
    public void testUploadInvalidFileType() throws InterruptedException {
        System.out.println("--- Bắt đầu Test Case AD_06: Upload file sai định dạng (.txt) ---");

        // 2. Vào trang Thêm sản phẩm
        driver.get("http://localhost/ecommerce-website-php/admin_area/index.php?insert_product");
        Thread.sleep(1000);

        // 3. Điền các trường bắt buộc (để không bị lỗi do thiếu thông tin)
        driver.findElement(By.name("product_title")).sendKeys("SP Test Upload Sai");
        driver.findElement(By.name("product_url")).sendKeys("sp-upload-sai");
        driver.findElement(By.name("product_price")).sendKeys("1000");
        driver.findElement(By.name("psp_price")).sendKeys("1000");
        driver.findElement(By.name("product_label")).sendKeys("Bug");

        // Chọn category để đỡ lỗi
        try { new Select(driver.findElement(By.name("product_cat"))).selectByIndex(1); } catch (Exception e) {}
        try { new Select(driver.findElement(By.name("cat"))).selectByIndex(1); } catch (Exception e) {}

        // 4. [QUAN TRỌNG] Upload file .txt vào chỗ yêu cầu ảnh
        // Đảm bảo file D:\fake_image.txt đã tồn tại
        String invalidFilePath = "D:\\Hoc\\HK6\\Software quality assurance and testing\\fake_image.txt";

        System.out.println("Đang thử upload file: " + invalidFilePath);
        driver.findElement(By.name("product_img1")).sendKeys(invalidFilePath);
        driver.findElement(By.name("product_img2")).sendKeys(invalidFilePath);
        driver.findElement(By.name("product_img3")).sendKeys(invalidFilePath);

        driver.findElement(By.name("product_price")).sendKeys("500000");
        driver.findElement(By.name("psp_price")).sendKeys("450000");
        driver.findElement(By.name("product_keywords")).sendKeys("test");
        driver.findElement(By.name("product_label")).sendKeys("New");

        // 5. Bấm Submit
        WebElement submitBtn = driver.findElement(By.name("submit"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        Thread.sleep(500);
        submitBtn.click();

        // 6. Kiểm tra kết quả (Validation)
        Thread.sleep(2000);
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("Thông báo từ Web: " + alertText);
            alert.accept();

            // LOGIC KIỂM THỬ SQA:
            // Nếu web báo "Success" -> Nghĩa là Web chấp nhận file rác -> Test Failed (Phát hiện Bug).
            // Nếu web báo lỗi hoặc không cho submit -> Test Passed (Web xịn).

            if (alertText.contains("successfully")) {
                // Đây là chỗ bạn ghi điểm với thầy giáo: Automation tìm ra lỗi bảo mật!
                String bugMessage = "PHÁT HIỆN LỖI (BUG): Hệ thống chấp nhận file .txt làm hình ảnh! (Thiếu Validate đuôi file)";
                System.err.println(bugMessage);

                // Dòng này sẽ làm Test Case màu đỏ (Failed), nhưng là Failed có chủ đích
                Assert.fail(bugMessage);
            } else {
                System.out.println(">>> Hệ thống đã chặn file rác thành công.");
            }

        } catch (Exception e) {
            // Nếu không có alert success hiện ra, có thể là browser chặn hoặc web reload
            System.out.println("Không thấy thông báo thành công -> Có thể hệ thống đã chặn âm thầm.");
        }
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}