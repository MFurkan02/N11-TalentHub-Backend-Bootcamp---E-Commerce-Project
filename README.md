🛒 E-Commerce Microservices Project
Bu proje, modern bir e-ticaret platformunun gereksinimlerini karşılamak üzere Spring Boot ve Spring Cloud ekosistemi kullanılarak geliştirilmiş, çok modüllü bir mikroservis mimarisidir.

🏗️ Proje Mimarisi ve Servisler
Proje içerisinde yer alan servisler ve görevleri aşağıda açıklanmıştır:

🛠️ Altyapı Servisleri
Config Server: Uygulamanın tüm çevresel yapılandırmalarını (properties/yml) merkezi bir yerden yönetir.

Discovery Server (Netflix Eureka): Servislerin dinamik olarak birbirlerini bulmasını ve kayıt olmasını sağlar.

API Gateway: Tüm dış isteklerin tek bir noktadan girmesini sağlar ve ilgili mikroservise yönlendirir.

📦 İş Mantığı (Business) Servisleri
User Service: Kullanıcı kayıt, giriş ve profil yönetimi işlemlerini yürütür.

Product Service: Ürünlerin tanımı, kategori ve özellik yönetimini yapar.

Stock Service: Ürünlerin stok miktarlarını takip eder ve günceller.

Search Service: Ürünler arasında hızlı ve etkili arama yapılmasını sağlar.

Shopping Cart Service: Kullanıcıların sepet işlemlerini (ekleme, çıkarma, güncelleme) yönetir.

Favorite List Service: Kullanıcıların beğendikleri ürünleri saklamasına olanak tanır.

Order Service: Sipariş oluşturma, geçmiş siparişleri listeleme ve durum takibi yapar.

Payment Service: Ödeme süreçlerini ve ödeme sağlayıcı entegrasyonlarını yönetir.

🚀 Yerel Kurulum ve Çalıştırma
Projeyi yerel makinenizde çalıştırmak için şu adımları izleyin:

1. Ön Hazırlık
Java 17 veya üzeri bir sürümün yüklü olduğundan emin olun.

Kullandığınız veritabanlarının (PostgreSQL) ve varsa Message Broker (RabbitMQ/Kafka) servislerinin ve KeyCloak ayakta olduğundan emin olun.

2. Servisleri Başlatma Sırası
Mikroservislerin doğru çalışabilmesi için aşağıdaki sırayla başlatılması kritiktir:

Config Server: Diğer servisler ayarlarını buradan çekeceği için ilk bu servis çalışmalıdır.

Discovery Server: Servislerin birbirini tanıyabilmesi için ikinci sırada çalışmalıdır.

Diğer Servisler: Artık product, order, user gibi servisleri dilediğiniz sırayla başlatabilirsiniz.

API Gateway: En son gateway servisini başlatarak dış dünyaya kapıları açabilirsiniz.
