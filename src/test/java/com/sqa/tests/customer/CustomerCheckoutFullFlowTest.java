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

        // --- BƯỚC 2: THÊM SẢN PHẨM (SỬA LỖI CLICK) ---
        System.out.println("2. Thêm sản phẩm vào giỏ...");
        driver.get("http://localhost/ecommerce-website-php/index.php");
        Thread.sleep(1000);

        try {
            // Tìm ảnh sản phẩm
            WebElement productLink = driver.findElement(By.cssSelector(".product a"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", productLink);
            Thread.sleep(1000);

            // [FIX] Dùng Javascript Click để không bị nhãn 'New/Sale' che khuất
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", productLink);
            System.out.println("-> Đã click vào sản phẩm.");
            Thread.sleep(1000);

            // Chọn Size & Qty (Chọn index 1 để chắc chắn có số lượng)
            new Select(driver.findElement(By.name("product_qty"))).selectByIndex(1); // Chọn số lượng 1
            new Select(driver.findElement(By.name("product_size"))).selectByIndex(1); // Chọn size Small

            // Bấm nút Mua
            WebElement addBtn = driver.findElement(By.cssSelector("button[type='submit']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);

            // Xử lý Alert "Already added"
            Thread.sleep(1000);
            try {
                driver.switchTo().alert().accept();
                System.out.println("-> Sản phẩm đã có trong giỏ.");
            } catch (Exception e) {
                System.out.println("-> Thêm mới thành công.");
            }

        } catch (Exception e) {
            System.out.println("Lỗi thao tác thêm hàng: " + e.getMessage());
            Assert.fail("Không thể thêm hàng vào giỏ! Dừng test.");
        }

        // --- BƯỚC 3: VÀO CART SỬA SỐ LƯỢNG & CHECK GIÁ ---
        System.out.println("3. Vào giỏ hàng kiểm tra tính tiền...");
        driver.get("http://localhost/ecommerce-website-php/cart.php");
        Thread.sleep(1000);

        // Kiểm tra giỏ hàng có rỗng không
        if(driver.getPageSource().contains("Your cart is empty")) {
            Assert.fail("Lỗi: Giỏ hàng rỗng! Bước thêm hàng thất bại.");
        }

        // Lấy đơn giá (Unit Price) - Cột thứ 4
        // [FIX] XPath tổng quát hơn để tìm trong bảng
        WebElement priceEl = driver.findElement(By.xpath("//form[@method='post']//table/tbody/tr[1]/td[4]"));
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

        // Kiểm tra Tổng tiền (Total) ở bảng Order Summary
        WebElement totalEl = driver.findElement(By.xpath("//tr[@class='total']/th"));
        double actualTotal = parsePrice(totalEl.getText());
        double expectedTotal = unitPrice * 2;

        System.out.println("   + Tổng tiền Web tính: $" + actualTotal);

        Assert.assertEquals(actualTotal, expectedTotal, "LỖI RELIABILITY: Tính tiền sai!");
        System.out.println("-> Tính toán chính xác! (Pass)");

        // --- BƯỚC 4: TIẾN HÀNH CHECKOUT ---
        System.out.println("4. Tiến hành Checkout...");
        driver.findElement(By.xpath("//a[contains(@href, 'checkout.php')]")).click();
        Thread.sleep(2000);

        // --- BƯỚC 5: THANH TOÁN OFFLINE ---
        try {
            WebElement payOfflineBtn = driver.findElement(By.xpath("//a[contains(@href, 'order.php') or contains(text(), 'Offline')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payOfflineBtn);
            Thread.sleep(1000);

            System.out.println("-> Chọn thanh toán Offline...");
            // Dùng JS Click cho chắc ăn
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", payOfflineBtn);

        } catch (Exception e) {
            System.out.println("Cảnh báo: Không tìm thấy nút 'Pay Offline'.");
        }

        // --- BƯỚC 6: XỬ LÝ ALERT THÀNH CÔNG ---
        Thread.sleep(2000);
        try {
            Alert alert = driver.switchTo().alert();
            String text = alert.getText();
            System.out.println(" THÔNG BÁO CUỐI: " + text);
            alert.accept();
        } catch (Exception e) {
            System.out.println("Không thấy bảng thông báo.");
        }

        // --- BƯỚC 7: XÁC NHẬN ---
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        boolean isSuccess = currentUrl.contains("my_orders") || driver.getPageSource().contains("paid");
        Assert.assertTrue(isSuccess, "Lỗi Integrity: Không chuyển về trang đơn hàng!");

        System.out.println(">>> MASTER TEST PASSED: TOÀN BỘ QUY TRÌNH OK! <<<");
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}