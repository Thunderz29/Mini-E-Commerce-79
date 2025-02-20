# E-Commerce Microservices System

## Deskripsi Aplikasi
Aplikasi ini adalah sebuah sistem e-commerce yang terdiri dari beberapa microservice yang dibangun menggunakan **Java 17** dan **Maven 3**. Setiap service memiliki fungsi khusus untuk mendukung operasi e-commerce, seperti manajemen pengguna, produk, pesanan, keranjang belanja, stok produk, pembayaran, dan notifikasi. Database yang digunakan adalah **PostgreSQL**. Aplikasi ini dirancang untuk memudahkan pengelolaan toko online, mulai dari registrasi pengguna, penambahan produk, hingga proses pembayaran.

---

## Daftar Service

### 1. User Service
#### Fitur:
- Registrasi pengguna baru.
- Login dan manajemen profil pengguna.
- CRUD (Create, Read, Update, Delete) operasi untuk data pengguna.

#### Tech Stack:
- **Java 17**, **Maven 3**, **PostgreSQL**.

#### Endpoints:
- `POST /users` - Mendaftarkan pengguna baru.
- `GET /users/{id}` - Mendapatkan informasi pengguna berdasarkan ID.
- `PUT /users/{id}` - Mengupdate data pengguna.
- `DELETE /users/{id}` - Menghapus pengguna.

#### Tabel Database:
- **users** (id, username, email, password, created_at).

---

### 2. Product Service
#### Fitur:
- Menambah, mengedit, melihat, dan menghapus produk.
- Menampilkan daftar produk dan detail produk.

#### Tech Stack:
- **Java 17**, **Maven 3**, **PostgreSQL**.

#### Endpoints:
- `POST /products` - Menambahkan produk baru.
- `GET /products/{id}` - Mendapatkan detail produk berdasarkan ID.
- `GET /products` - Menampilkan daftar produk.
- `PUT /products/{id}` - Mengupdate data produk.
- `DELETE /products/{id}` - Menghapus produk.

#### Tabel Database:
- **products** (id, name, description, price, category).

---

### 3. Order Service
#### Fitur:
- Membuat dan mengelola pesanan.
- Melacak status pesanan.

#### Tech Stack:
- **Java 17**, **Maven 3**, **PostgreSQL**.

#### Endpoints:
- `POST /orders` - Membuat pesanan baru.
- `GET /orders/{id}` - Mendapatkan detail pesanan berdasarkan ID.
- `GET /orders` - Menampilkan semua pesanan.
- `PUT /orders/{id}` - Mengupdate status pesanan.
- `DELETE /orders/{id}` - Membatalkan pesanan.

#### Tabel Database:
- **orders** (id, user_id, product_id, quantity, status, order_date).

---

### 4. Cart Service (Keranjang Belanja)
#### Fitur:
- Menambahkan produk ke keranjang belanja.
- Mengupdate atau menghapus produk dari keranjang.

#### Tech Stack:
- **Java 17**, **Maven 3**, **PostgreSQL**.

#### Endpoints:
- `POST /cart` - Menambahkan produk ke keranjang.
- `GET /cart/{userId}` - Mendapatkan semua barang di keranjang untuk pengguna tertentu.
- `PUT /cart/{userId}` - Mengupdate barang dalam keranjang (misalnya, mengubah jumlah).
- `DELETE /cart/{userId}/{productId}` - Menghapus produk dari keranjang.

#### Tabel Database:
- **carts** (user_id, product_id, quantity, created_at).

---

### 5. Payment Service
#### Fitur:
- Memproses pembayaran untuk pesanan.
- Melacak status pembayaran.

#### Tech Stack:
- **Java 17**, **Maven 3**, **PostgreSQL**.

#### Endpoints:
- `POST /payment` - Memproses pembayaran untuk pesanan.
- `GET /payment/{paymentId}` - Mendapatkan status pembayaran berdasarkan ID.

#### Tabel Database:
- **payments** (id, order_id, amount, payment_status, payment_date).

---

### 6. Notification Service
#### Fitur:
- Mengirimkan notifikasi kepada pengguna.
- Mendukung notifikasi untuk berbagai kejadian, seperti pesanan baru, pembayaran berhasil, atau pembaruan status pesanan.

#### Tech Stack:
- **Java 17**, **Maven 3**, **PostgreSQL**.

#### Endpoints:
- `POST /notifications` - Mengirimkan notifikasi baru.
- `GET /notifications/{userId}` - Mendapatkan daftar notifikasi untuk pengguna tertentu.
- `PUT /notifications/{id}` - Menandai notifikasi sebagai sudah dibaca.

#### Tabel Database:
- **notifications** (id, user_id, message, status, created_at).

---

## Cara Clone dan Menjalankan Repo

### Clone Repository:
```bash
git clone https://github.com/username/nama-repo.git
cd nama-repo
```

### Setup Database:
1. Pastikan PostgreSQL sudah terinstall.
2. Buat database untuk setiap service (sesuaikan dengan konfigurasi di `application.properties` atau `application.yml`).

### Build dan Run:
1. Masuk ke direktori masing-masing service.
2. Jalankan perintah berikut untuk build dan menjalankan aplikasi:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Akses Aplikasi:
Setiap service akan berjalan pada port yang berbeda. Akses melalui `http://localhost:{port}`.

---

## Catatan Tambahan
- Pastikan konfigurasi database dan port service tidak konflik.
- Gunakan Postman atau alat serupa untuk menguji endpoint masing-masing service.
