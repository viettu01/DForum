# DForum - Diễn đàn trao đổi kiến thức lập trình trên thiết bị di động

DForum là một ứng dụng Android phục vụ như một diễn đàn trao đổi kiến thức lập trình. Nó cho phép người dùng tham gia vào các cuộc thảo luận, đặt câu hỏi và chia sẻ chuyên môn của họ về các chủ đề lập trình khác nhau. Ứng dụng sử dụng Firebase Realtime Database làm cơ sở dữ liệu chính và được xây dựng bằng Android Studio.

![Ảnh chụp màn hình DForum](ảnh chụp màn hình.png)
![image](https://github.com/viettu01/DForum/assets/88828150/8a894d93-1ede-4dd9-89a9-c50e24fb5d1b)
![image](https://github.com/viettu01/DForum/assets/88828150/728a1788-7b79-4d80-8df2-f2577b99d32c)

## Tính năng

- Đăng ký và xác thực người dùng
- Tạo bài viết mới và trả lời bài viết hiện có
- Quản lý thông báo khi có người dùng khác đăng bài, bình luận, trả lời bình luận
- Tắt thông báo bài viết chính chủ
- Bình luận và trả lời bình luận bài viết hiện có
- Tìm kiếm bài viết theo từ khóa hoặc danh mục diễn đàn nhỏ
- Lọc và sắp xếp bài viết
- Quản lý kiểm duyệt bài viết của admin
- Quản lý hồ sơ người dùng
- Quản lý thông tin cá nhân
- Chế độ Dark Mode

## Phụ thuộc

Yêu cầu phần mềm

- Hệ điều hành tối thiểu Android 8
- minSdk 26
- targetSdk 33

Yêu cầu quyền của ứng dụng

- android.permission.INTERNET
- android.permission.ACCESS_NETWORK_STATE
- android.permission.ACCESS_WIFI_STATE
- android.permission.READ_EXTERNAL_STORAGE

Các phụ thuộc sau đây được sử dụng trong dự án này:

- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage
- Glide
- PhotoView
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
