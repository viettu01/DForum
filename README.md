# DForum - Diễn đàn trao đổi kiến thức lập trình trên thiết bị di động

DForum là một ứng dụng Android phục vụ như một diễn đàn trao đổi kiến thức lập trình. Nó cho phép người dùng tham gia vào các cuộc thảo luận, đặt câu hỏi và chia sẻ chuyên môn của họ về các chủ đề lập trình khác nhau. Ứng dụng sử dụng Firebase làm cơ sở dữ liệu phụ trợ và được xây dựng bằng Android Studio.

![Ảnh chụp màn hình DForum](ảnh chụp màn hình.png)

## Đặc trưng

- Đăng ký và xác thực người dùng
- Tạo bài viết mới và trả lời bài viết hiện có
- Upvote và downvote bài viết và trả lời
- Tìm kiếm bài viết theo từ khóa hoặc danh mục
- Duyệt bài viết theo các chủ đề lập trình khác nhau
- Quản lý hồ sơ người dùng

## Cài đặt

1. Sao chép kho lưu trữ vào máy cục bộ của bạn:

2. Mở dự án trong Android Studio.

3. Định cấu hình Firebase:

    - Tạo dự án Firebase mới trên [Bảng điều khiển Firebase](https://console.firebase.google.com/).
    - Làm theo hướng dẫn để thêm ứng dụng Android của bạn vào dự án và tải xuống tệp `google-services.json`.
    - Đặt file `google-services.json` vào thư mục `app/` của project.

4. Xây dựng và chạy ứng dụng trên trình giả lập Android hoặc thiết bị vật lý.

## Phụ thuộc

Các phụ thuộc sau đây được sử dụng trong dự án này:

- Xác thực căn cứ hỏa lực
- Cơ sở dữ liệu thời gian thực Firebase
- Giao diện người dùng căn cứ hỏa lực
- Lướt
- CircleImageView

Để biết danh sách đầy đủ các phụ thuộc và phiên bản của chúng, vui lòng tham khảo tệp `build.gradle`.

## Môi trường phát triển

- Android Studio: IDE chính để phát triển ứng dụng di động Android.
- Java Development Kit (JDK): Cung cấp các công cụ phát triển Java cần thiết.
- Android SDK: Bộ công cụ phát triển Android.
- Firebase Account: Đăng ký tài khoản Firebase để cấu hình dịch vụ Firebase cho ứng dụng.

## Tài liệu tham khảo

- [Tài liệu dành cho nhà phát triển Android](https://developer.android.com/docs)
- [Tài liệu Firebase](https://firebase.google.com/docs)
- [Stackoverflow.com](https://stackoverflow.com/)

## Nhà phát triển DForum

DForum được phát triển bởi:
- [Nguyễn Văn Mạnh](https://github.com/manhnv01/)
- [Phạm Lê Việt Tú](https://github.com/viettu01/)
- [Nguyễn Thị Lê](https://github.com/NguyenLe0508/)
