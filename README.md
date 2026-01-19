🛒 AUTOMATION TESTING PROJECT – E-COMMERCE WEBSITE

Môn học: Đảm bảo Chất lượng Phần mềm
(Software Quality Assurance & Testing)

Công cụ & Công nghệ: Selenium WebDriver, Java, TestNG, Maven

Mục tiêu dự án:
Thực hiện kiểm thử tự động (Automation Testing) cho các chức năng cốt lõi của website E-Commerce viết bằng PHP thuần,
đồng thời đánh giá mức độ tuân thủ các thuộc tính chất lượng phần mềm (SQA) như: Security, Reliability, Robustness, Integrity, Efficiency

📂 Project Structure

Mã nguồn kiểm thử được tổ chức theo Page Object Model (POM) và chia tách rõ ràng giữa:

Admin Module

Customer Module

Thiết kế này giúp đảm bảo:

✅ Maintainability (Khả năng bảo trì)

✅ Modularity (Tính mô-đun)

✅ Dễ mở rộng & tái sử dụng test case

🧪 Test Scenarios & SQA Attributes

Tài liệu này mô tả các kịch bản kiểm thử (Test Scenarios) cho toàn bộ hệ thống.
Mỗi test case được ánh xạ trực tiếp với các thuộc tính SQA nhằm đảm bảo chất lượng phần mềm toàn diện.

1️⃣ Admin Module (Phân hệ Quản trị)

| Test File                  | Test Scenario                                                                                               | SQA Attributes                                                |
| -------------------------- | ----------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------- |
| **AdminLoginTest**         | Kiểm tra đăng nhập với tài khoản hợp lệ. Xác minh hệ thống chuyển hướng đúng đến Dashboard.                 | Correctness (Tính đúng đắn)                                   |
| **AdminInvalidLoginTest**  | Nhập sai mật khẩu. Hệ thống phải hiển thị thông báo lỗi và chặn truy cập.                                   | Security (Tính bảo mật)                                       |
| **AdminSecurityTest**      | Cố tình truy cập trực tiếp URL Dashboard khi chưa đăng nhập. Hệ thống phải tự động redirect về trang Login. | Security (Access Control)                                     |
| **AdminInsertProductTest** | Thêm mới sản phẩm với đầy đủ thông tin (Text, Dropdown, Upload hình ảnh). Kiểm tra thông báo thành công.    | Reliability (Độ tin cậy)<br>Data Integrity (Toàn vẹn dữ liệu) |
| **AdminEmptyFieldsTest**   | Bỏ trống các trường bắt buộc khi thêm sản phẩm. Hệ thống phải chặn thao tác Submit.                         | Robustness (Tính bền vững)                                    |
| **AdminFileUploadTest**    | Upload file không hợp lệ (.txt) vào trường hình ảnh. Kiểm tra cơ chế validate định dạng file.               | Robustness (Input Validation)                                 |

2️⃣ Customer Module (Phân hệ Khách hàng)

| Test File                        | Test Scenario                                                                                                                                                                                                                | SQA Attributes                                                                           |
| -------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| **CustomerAddToCartTest**        | Chọn sản phẩm từ trang chủ, vào trang chi tiết, chọn Size & Số lượng, sau đó thêm vào giỏ hàng.                                                                                                                              | Functionality (Tính chức năng)                                                           |
| **CustomerCheckoutFullFlowTest** | **Master Test – Kiểm thử toàn trình:**<br>1. Đăng nhập hệ thống.<br>2. Chọn sản phẩm và thêm vào giỏ.<br>3. Chỉnh sửa số lượng & kiểm tra tổng tiền.<br>4. Thực hiện thanh toán Offline.<br>5. Xác nhận đặt hàng thành công. | Integrity (Tính toàn vẹn)<br>Reliability (Tính toán chính xác)<br>Efficiency (Hiệu năng) |


🐛 Bug Report – Các lỗi được phát hiện

Trong quá trình chạy Automation Test, hệ thống phát hiện một số Defects nghiêm trọng, vi phạm các nguyên tắc SQA và cần được ưu tiên xử lý.

1️⃣ Lỗ hổng Upload File (Robustness / Security)

Mô tả:
Hệ thống cho phép upload các file không hợp lệ (.txt, định dạng lạ) vào trường Hình ảnh sản phẩm, thay vì chỉ cho phép file ảnh (.jpg, .png, …).

File test phát hiện:
AdminFileUploadTest.java

Thuộc tính SQA bị vi phạm:

Robustness

Security

Mức độ:
🔴 High (Nghiêm trọng)

Rủi ro:

Nguy cơ upload mã độc

Ảnh hưởng đến an toàn và toàn vẹn hệ thống

2️⃣ Lỗi Logic Tính Tiền trong Giỏ Hàng (Robustness)

Mô tả:
Người dùng nhập số lượng âm (ví dụ: -5), hệ thống vẫn chấp nhận và tính ra tổng tiền âm.

File test phát hiện:
CartFunctionalityTest
(tích hợp trong CustomerCheckoutFullFlowTest)

Thuộc tính SQA bị vi phạm:

Robustness (Input Handling)

Mức độ:
🔴 High (Nghiêm trọng)

Rủi ro:

Sai lệch dữ liệu thanh toán

Ảnh hưởng trực tiếp đến nghiệp vụ bán hàng

🚀 Setup & Run Automation Tests
🔧 Yêu cầu môi trường

Java JDK: 17 hoặc 21

Maven: 3.8+

Web Server: XAMPP (Apache + MySQL)

Source Web: PHP E-Commerce

Database: ecom_store (đã import)

▶️ Cách chạy Test

Khởi động XAMPP

Apache ✅

MySQL ✅

Mở project bằng IntelliJ IDEA

Mở file test bất kỳ
(ví dụ: CustomerCheckoutFullFlowTest)

Nhấn Run (▶) để thực thi test