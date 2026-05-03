import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import OrderService from '../services/OrderService';
import ShoppingCartService from '../services/ShoppingCartService';
import { toast } from 'react-toastify';
import { CreditCard, MapPin, Truck, ShieldCheck, ChevronRight } from 'lucide-react';
import '../Checkout.css';

const Checkout = () => {
    const { state } = useLocation();
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem("user"));

    // Formu bölümlere ayırdık (Adres ve Kart)
    const [formData, setFormData] = useState({
        firstName: "", lastName: "", streetAddress: "",
        city: "", country: "", phone: "", email: user?.email || "",
        paymentMethod: "IYZICO"
    });

    const [cardData, setCardData] = useState({
        cardHolderName: "", cardNumber: "",
        expireMonth: "", expireYear: "", cvc: ""
    });

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleCardChange = (e) => {
        setCardData({ ...cardData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Basit Kart Kontrolü
        if (cardData.cardNumber.length !== 16) {
            return toast.error("Geçerli bir kart numarası giriniz (16 hane).");
        }

        const finalRequest = {
            // 1. Kullanıcı ve Adres Bilgileri
            username: user.username,
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            phone: formData.phone,
            streetAddress: formData.streetAddress,
            city: formData.city,
            country: formData.country,

            // 2. Ödeme Bilgileri
            paymentMethod: "IYZICO",
            card: {
                cardHolderName: cardData.cardHolderName,
                cardNumber: cardData.cardNumber,
                expireMonth: cardData.expireMonth,
                expireYear: cardData.expireYear,
                cvc: cardData.cvc
            },

            // 3. Ürün Bilgileri (Backend Beklentisine Uygun)
            items: state.cartItems.map(item => ({
                productId: item.productId,
                productName: item.name,
                price: item.price,
                quantity: item.amount
            }))
        };

        try {
            toast.info("Ödemeniz işleniyor, lütfen bekleyin...", { autoClose: 2000 });

            // ADIM 1: Siparişi Oluştur
            const response = await OrderService.createOrder(finalRequest);

            if (response.data) {
                // ADIM 2: Sipariş Başarılıysa Sepeti Temizle
                try {
                    // Backend'de yazdığın yeni 'clear' endpoint'ini çağırıyoruz
                    await ShoppingCartService.clearCartByShoppingCartName(user.username);
                    console.log("Sepet başarıyla temizlendi.");
                } catch (cartErr) {
                    // Sepet temizlenemese bile sipariş oluştuğu için akışı bozmayalım
                    console.error("Sipariş oluştu ancak sepet temizlenirken hata alındı:", cartErr);
                }

                // ADIM 3: Kullanıcıyı Bilgilendir ve Yönlendir
                toast.success("✨ Ödeme Başarılı! Siparişiniz oluşturuldu ve sepetiniz boşaltıldı.");

                // Kısa bir gecikmeyle yönlendirme (Toast görünmesi için)
                setTimeout(() => {
                    navigate("/orders");
                }, 1500);
            }
        } catch (err) {
            // Backend'den veya Iyzico'dan gelen spesifik hata mesajını göster
            const errorMsg = err.response?.data?.message || "Ödeme reddedildi. Lütfen kart bilgilerinizi kontrol edin.";
            toast.error(errorMsg);
            console.error("Checkout Hatası:", err);
        }
    };

    return (
        <div className="checkout-wrapper">
            <div className="checkout-steps-nav">
                <span>Sepet</span> <ChevronRight size={16} />
                <span className="active">Ödeme ve Teslimat</span> <ChevronRight size={16} />
                <span>Onay</span>
            </div>

            <div className="checkout-main-content">
                <form onSubmit={handleSubmit} className="checkout-sections">

                    {/* TESLİMAT BÖLÜMÜ */}
                    <div className="checkout-card">
                        <div className="card-header">
                            <MapPin className="header-icon" />
                            <h3>Teslimat Adresi</h3>
                        </div>
                        <div className="card-body">
                            <div className="input-row">
                                <div className="input-group">
                                    <label>Ad</label>
                                    <input name="firstName" placeholder="Örn: Ahmet" onChange={handleInputChange} required />
                                </div>
                                <div className="input-group">
                                    <label>Soyad</label>
                                    <input name="lastName" placeholder="Örn: Yılmaz" onChange={handleInputChange} required />
                                </div>
                            </div>
                            <div className="input-group">
                                <label>Adres Detayı</label>
                                <textarea name="streetAddress" rows="3" placeholder="Sokak, Mahalle, Kapı No..." onChange={handleInputChange} required />
                            </div>
                            <div className="input-row">
                                <input name="city" placeholder="Şehir" onChange={handleInputChange} required />
                                <input name="country" placeholder="Ülke" onChange={handleInputChange} required />
                                <input name="phone" placeholder="Telefon" onChange={handleInputChange} required />
                            </div>
                        </div>
                    </div>

                    {/* ÖDEME BÖLÜMÜ */}
                    <div className="checkout-card">
                        <div className="card-header">
                            <CreditCard className="header-icon" />
                            <h3>Ödeme Bilgileri</h3>
                        </div>
                        <div className="card-body payment-grid">
                            <div className="input-group full">
                                <label>Kart Üzerindeki İsim</label>
                                <input name="cardHolderName" placeholder="AD SOYAD" onChange={handleCardChange} required />
                            </div>
                            <div className="input-group full">
                                <label>Kart Numarası</label>
                                <input name="cardNumber" placeholder="**** **** **** ****" maxLength="16" onChange={handleCardChange} required />
                            </div>
                            <div className="input-row">
                                <input name="expireMonth" placeholder="AY (05)" maxLength="2" onChange={handleCardChange} required />
                                <input name="expireYear" placeholder="YIL (26)" maxLength="2" onChange={handleCardChange} required />
                                <input name="cvc" placeholder="CVC" maxLength="3" onChange={handleCardChange} required />
                            </div>
                            <div className="secure-badge">
                                <ShieldCheck size={18} />
                                <span>256-bit SSL Güvenli Ödeme Alt Yapısı</span>
                            </div>
                        </div>
                    </div>

                    <button type="submit" className="payment-submit-btn">
                        {state.totalPrice} TL Öde ve Bitir
                    </button>
                </form>

                {/* SAĞ TARAF - SİPARİŞ ÖZETİ */}
                <aside className="checkout-sidebar">
                    <div className="summary-box">
                        <h3>Sipariş Özeti</h3>
                        <div className="summary-list">
                            {state.cartItems.map(item => (
                                <div key={item.productId} className="summary-product">
                                    <div className="prod-info">
                                        <span className="prod-name">{item.name}</span>
                                        <span className="prod-qty">Adet: {item.amount}</span>
                                    </div>
                                    <span className="prod-price">{item.price * item.amount} TL</span>
                                </div>
                            ))}
                        </div>
                        <div className="price-details">
                            <div className="detail-row">
                                <span>Ara Toplam</span>
                                <span>{state.totalPrice} TL</span>
                            </div>
                            <div className="detail-row">
                                <span>Kargo</span>
                                <span className="free-shipping">Ücretsiz</span>
                            </div>
                            <div className="total-row">
                                <span>Toplam</span>
                                <span>{state.totalPrice} TL</span>
                            </div>
                        </div>
                        <div className="trust-badges">
                            <Truck size={16} /> <span>Hızlı Teslimat</span>
                        </div>
                    </div>
                </aside>
            </div>
        </div>
    );
};

export default Checkout;