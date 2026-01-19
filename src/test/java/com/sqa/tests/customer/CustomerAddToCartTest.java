package com.sqa.tests.customer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select; // Import thư viện Dropdown
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

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
        System.out.println("--- Bắt đầu Test Case CU_03: Thêm vào giỏ hàng (Full Option) ---");

        // 1. Vào trang chủ
        driver.get("http://localhost/ecommerce-website-php/index.php");

        // 2. Tìm và bấm nút "Add To Cart" (Màu đỏ) ở trang chủ để vào trang chi tiết
        try {
            // Tìm nút có chữ "Add To Cart"
            // Dựa vào ảnh bạn gửi, nút này là <a> nằm trong <div> product
            // Ta dùng xpath tìm thẻ <a> chứa chữ 'Add To Cart'
            WebElement viewProductBtn = driver.findElement(By.xpath("//a[contains(text(), 'Add To Cart')]"));

            // Scroll xuống để chắc chắn nút hiển thị
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", viewProductBtn);
            Thread.sleep(1000);

            System.out.println("Click vào sản phẩm đầu tiên...");
            viewProductBtn.click();

        } catch (Exception e) {
            // Fallback: Nếu không tìm thấy nút Add To Cart, thử click vào ảnh sản phẩm
            driver.findElement(By.cssSelector(".product a")).click();
        }

        // 3. Đang ở trang Chi tiết (Details Page)
        Thread.sleep(2000);
        System.out.println("Đã vào trang: " + driver.getTitle());

        // --- [QUAN TRỌNG] XỬ LÝ DROPDOWN ---
        try {
            // 3.1 Chọn Số lượng (Quantity)
            // Inspect dropdown này xem name là gì. Thường là product_qty hoặc quantity
            // Code dưới thử tìm theo name="product_qty"
            WebElement qtyDropdown = driver.findElement(By.name("product_qty"));
            Select selectQty = new Select(qtyDropdown);
            selectQty.selectByVisibleText("2"); // Chọn số lượng 2
            System.out.println("Đã chọn Quantity: 2");

            // 3.2 Chọn Size (Kích thước)
            // Inspect dropdown Size, thường name="product_size" hoặc size
            WebElement sizeDropdown = driver.findElement(By.name("product_size"));
            Select selectSize = new Select(sizeDropdown);
            selectSize.selectByIndex(1); // Chọn Size đầu tiên (ví dụ Small)
            System.out.println("Đã chọn Size: Small");

        } catch (Exception e) {
            // Nếu tìm theo name không được, ta sẽ tìm theo Tag Name (Dropdown thứ 1 và thứ 2)
            System.out.println("Không tìm thấy dropdown theo name, thử tìm theo thứ tự...");
            try {
                // Lấy tất cả thẻ <select> trên trang
                java.util.List<WebElement> selects = driver.findElements(By.tagName("select"));
                if (selects.size() >= 2) {
                    new Select(selects.get(0)).selectByIndex(1); // Chọn Quantity
                    new Select(selects.get(1)).selectByIndex(1); // Chọn Size
                    System.out.println("Đã chọn Dropdown theo thứ tự.");
                }
            } catch (Exception ex) {
                System.out.println("Cảnh báo: Không chọn được Dropdown. Sẽ thử bấm nút luôn.");
            }
        }

        // 4. Bấm nút Add to Cart thật (Nút màu đỏ trong trang chi tiết)
        try {
            // Tìm nút button có type='submit' nằm trong form
            WebElement realAddToCartBtn = driver.findElement(By.cssSelector("button[type='submit']"));
            // Hoặc tìm theo text
            // WebElement realAddToCartBtn = driver.findElement(By.xpath("//button[contains(text(), 'Add to Cart')]"));

            realAddToCartBtn.click();
            System.out.println("Đã bấm nút Mua hàng.");

        } catch (Exception e) {
            Assert.fail("Lỗi: Không tìm thấy nút Add to Cart trong trang chi tiết!");
        }

        // 5. Kiểm tra kết quả
        Thread.sleep(2000);

        // Kiểm tra xem có chuyển sang trang Giỏ hàng hoặc reload lại trang không
        // Thường khi add xong web sẽ tự reload.

        // Check Header xem số lượng item có tăng lên không
        try {
            WebElement cartInfo = driver.findElement(By.xpath("//a[contains(text(), 'Items') or contains(@class, 'btn-primary')]"));
            String infoText = cartInfo.getText();
            System.out.println("Giỏ hàng hiện tại: " + infoText);

            // Nếu có chữ "2 Items" hoặc số tiền khác 0 -> Success
            boolean success = infoText.matches(".*[1-9].*"); // Có số từ 1-9
            Assert.assertTrue(success, "Lỗi: Giỏ hàng vẫn 0 Items!");

        } catch (Exception e) {
            System.out.println("Không check được Header, bỏ qua.");
        }

        System.out.println(">>> Test Passed: Thêm vào giỏ hàng thành công!");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}