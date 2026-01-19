package com.sqa.tests.customer;

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

public class CustomerCheckoutFullFlowTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    // Hàm chuyển đổi tiền tệ: "$150.00" -> 150.0
    public double parsePrice(String priceText) {
        if (priceText == null) return 0.0;
        String cleanPrice = priceText.replaceAll("[$,\\s]", "");
        try { return Double.parseDouble(cleanPrice); } catch (Exception e) { return 0.0; }
    }

    @Test
    public void testMasterFlow() throws InterruptedException {
        System.out.println("=== MASTER TEST: LOGIN -> ADD CART -> TÍNH TIỀN -> CHECKOUT ===");

        // --- BƯỚC 1: ĐĂNG NHẬP ---
        System.out.println("1. Đăng nhập...");
        driver.get("http://localhost/ecommerce-website-php/checkout.php");

        if (driver.getPageSource().contains("Login")) {
            try {
                driver.findElement(By.name("c_email")).sendKeys("user@ave.com");
                driver.findElement(By.name("c_pass")).sendKeys("123");
                driver.findElement(By.name("login")).click();
                Thread.sleep(1000);
                try { driver.switchTo().alert().accept(); } catch (Exception e) {}
                System.out.println("-> Đăng nhập thành công!");
            } catch (Exception e) {
                System.out.println("-> Có thể đã đăng nhập rồi.");
            }
        }

        // --- BƯỚC 2: THÊM SẢN PHẨM ---
        System.out.println("2. Thêm sản phẩm vào giỏ...");
        driver.get("http://localhost/ecommerce-website-php/index.php");

        try {
            WebElement productLink = driver.findElement(By.cssSelector(".product a"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", productLink);
            productLink.click();
            Thread.sleep(1000);

            // Chọn Size & Qty (Mặc định chọn 1 trước)
            new Select(driver.findElement(By.name("product_qty"))).selectByIndex(0);
            new Select(driver.findElement(By.name("product_size"))).selectByIndex(1);

            driver.findElement(By.cssSelector("button[type='submit']")).click();

            // Xử lý Alert
            Thread.sleep(1000);
            try {
                driver.switchTo().alert().accept();
                System.out.println("-> Sản phẩm đã có, tiếp tục vào giỏ hàng.");
            } catch (Exception e) {
                System.out.println("-> Thêm mới thành công.");
            }

        } catch (Exception e) {
            System.out.println("Lỗi thao tác thêm hàng: " + e.getMessage());
        }

        // --- [MỚI] BƯỚC 3: VÀO CART SỬA SỐ LƯỢNG & CHECK GIÁ ---
        System.out.println("3. Vào giỏ hàng kiểm tra tính tiền...");
        driver.get("http://localhost/ecommerce-website-php/cart.php");
        Thread.sleep(1000);

        // Lấy đơn giá (Unit Price)
        WebElement priceEl = driver.findElement(By.xpath("//tbody/tr[1]/td[4]"));
        double unitPrice = parsePrice(priceEl.getText());
        System.out.println("   + Đơn giá: $" + unitPrice);

        // Sửa số lượng thành 2
        WebElement qtyInput = driver.findElement(By.cssSelector("input.quantity"));
        qtyInput.clear();
        qtyInput.sendKeys("2");
        System.out.println("   + Đổi số lượng thành: 2");

        // Bấm Update Cart
        driver.findElement(By.name("update")).click();
        Thread.sleep(2000);

        // Kiểm tra Tổng tiền (Total)
        WebElement totalEl = driver.findElement(By.xpath("//tr[@class='total']/th"));
        double actualTotal = parsePrice(totalEl.getText());
        double expectedTotal = unitPrice * 2; // Phải bằng giá * 2

        System.out.println("   + Tổng tiền Web tính: $" + actualTotal);

        // Assert: Nếu sai tiền thì báo lỗi ngay
        Assert.assertEquals(actualTotal, expectedTotal, "LỖI RELIABILITY: Tính tiền sai!");
        System.out.println("-> Tính toán chính xác! (Pass)");

        // --- BƯỚC 4: TIẾN HÀNH CHECKOUT ---
        System.out.println("4. Tiến hành Checkout...");
        // Bấm nút "Proceed to checkout" ngay trong giỏ hàng
        driver.findElement(By.xpath("//a[contains(@href, 'checkout.php')]")).click();
        Thread.sleep(2000);

        // --- BƯỚC 5: THANH TOÁN OFFLINE ---
        try {
            WebElement payOfflineBtn = driver.findElement(By.xpath("//a[contains(@href, 'order.php') or contains(text(), 'Offline')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payOfflineBtn);
            Thread.sleep(1000);

            System.out.println("-> Chọn thanh toán Offline...");
            payOfflineBtn.click();

        } catch (Exception e) {
            System.out.println("Cảnh báo: Không tìm thấy nút 'Pay Offline'.");
        }

        // --- BƯỚC 6: XỬ LÝ ALERT THÀNH CÔNG ---
        Thread.sleep(2000);
        try {
            Alert alert = driver.switchTo().alert();
            String text = alert.getText();
            System.out.println(" THÔNG BÁO CUỐI: " + text);
            Assert.assertTrue(text.contains("submitted") || text.contains("Thanks"), "Thông báo không đúng!");
            alert.accept();
        } catch (Exception e) {
            System.out.println("Không thấy bảng thông báo.");
        }

        // --- BƯỚC 7: XÁC NHẬN ---
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        boolean isSuccess = currentUrl.contains("my_orders");
        Assert.assertTrue(isSuccess, "Lỗi Integrity: Không chuyển về trang đơn hàng!");

        System.out.println(">>> MASTER TEST PASSED: TOÀN BỘ QUY TRÌNH OK! <<<");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}