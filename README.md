# 🛒 E-Commerce Microservices Project

Modern bir e-ticaret platformunun ihtiyaçlarını karşılamak amacıyla geliştirilmiş,  
**Spring Boot** ve **Spring Cloud** tabanlı çok modüllü bir **mikroservis mimarisi** projesidir.

---

## 🏗️ Proje Mimarisi

Proje, **altyapı (infrastructure)** ve **iş mantığı (business)** servisleri olmak üzere iki ana gruba ayrılmıştır.

---

## 🛠️ Altyapı Servisleri

### ⚙️ Config Server
- Tüm servislerin yapılandırmalarını merkezi olarak yönetir.
- `application.yml` / `application.properties` dosyaları buradan servis edilir.

### 🔍 Discovery Server (Netflix Eureka)
- Mikroservislerin birbirlerini dinamik olarak bulmasını sağlar.
- Servis kayıt ve keşif mekanizmasını yönetir.

### 🌐 API Gateway
- Tüm dış isteklerin giriş noktasıdır.
- İstekleri ilgili mikroservislere yönlendirir.
- Güvenlik, logging ve routing işlemlerini yönetir.

---

## 📦 Business Servisleri

### 👤 User Service
- Kullanıcı kayıt ve giriş işlemleri
- Profil yönetimi

### 📦 Product Service
- Ürün yönetimi
- Kategori ve özellik tanımları

### 📊 Stock Service
- Ürün stok takibi
- Stok güncelleme işlemleri

### 🔎 Search Service
- Ürünler arasında hızlı arama
- Filtreleme ve sorgulama

### 🛒 Shopping Cart Service
- Sepete ürün ekleme / çıkarma
- Sepet güncelleme işlemleri

### ❤️ Favorite List Service
- Kullanıcıların favori ürünlerini saklama

### 📑 Order Service
- Sipariş oluşturma
- Sipariş geçmişi
- Sipariş durumu takibi

### 💳 Payment Service
- Ödeme işlemleri
- Ödeme sağlayıcı entegrasyonları (örn: Iyzico)

---

## 🚀 Kurulum ve Çalıştırma

### 🔧 Ön Gereksinimler

Aşağıdaki araçların sisteminizde kurulu ve çalışır durumda olması gerekir:

- Java 17+
- PostgreSQL
- RabbitMQ veya Kafka (opsiyonel)
- Keycloak (authentication için)

---

## ▶️ Servisleri Başlatma Sırası

Mikroservislerin düzgün çalışması için aşağıdaki sıraya dikkat edilmelidir:

1. **Config Server**
   - Tüm servisler konfigürasyonlarını buradan alır.

2. **Discovery Server (Eureka)**
   - Servislerin birbirini bulabilmesi için gereklidir.

3. **Diğer Servisler**
   - `user-service`
   - `product-service`
   - `order-service`
   - `stock-service`
   - `search-service`
   - `favorite-list-service`
   - `shopping-cart-service`
   - `payment-service`

4. **API Gateway**
   - En son başlatılır.
   - Sistemin dış dünyaya açılan kapısıdır.

---

## 🧩 Kullanılan Teknolojiler

- Java 21
- Spring Boot
- Spring Cloud
- Netflix Eureka
- Spring Cloud Gateway
- PostgreSQL
- RabbitMQ / Kafka
- Keycloak
- Docker

---

## 📌 Notlar

- Mikroservisler birbirleriyle REST veya mesajlaşma (event-driven) üzerinden haberleşebilir.
- Her servis bağımsız olarak deploy edilebilir yapıdadır.
- Sistem ölçeklenebilir ve yüksek erişilebilirlik göz önünde bulundurularak tasarlanmıştır.

---

## 📄 Lisans

Bu proje eğitim ve geliştirme amaçlı hazırlanmıştır.
