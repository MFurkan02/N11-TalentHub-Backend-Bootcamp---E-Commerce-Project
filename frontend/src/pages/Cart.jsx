import React, { useState, useEffect } from 'react';
import ShoppingCartService from '../services/ShoppingCartService';
import '../Cart.css';
import { Trash2, Plus, Minus } from "lucide-react";
import { useNavigate } from "react-router-dom";



const Cart = () => {

    const [cart, setCart] = useState(null);
    const [totalPrice, setTotalPrice] = useState("0");
    const [loading, setLoading] = useState(true);

    const user = JSON.parse(localStorage.getItem("user"));

    const navigate = useNavigate();

    useEffect(() => {
        if (user) fetchCart();
        else setLoading(false);
    }, []);

    const fetchCart = async () => {
        try {
            const res = await ShoppingCartService.getCartByUsername(user.username);

            // --- SIRALAMA İŞLEMİ BURADA YAPILIYOR ---
            // Ürünleri ID'lerine göre küçükten büyüğe sıralıyoruz
            const sortedItems = res.data.items.sort((a, b) => a.productId - b.productId);

            // Sıralanmış yeni listeyi sepet içine yerleştiriyoruz
            setCart({ ...res.data, items: sortedItems });

            const priceRes = await ShoppingCartService.getTotalPrice(res.data.id);
            setTotalPrice(priceRes.data.total_price || "0");

        } catch (err) {
            console.error("Sepet yüklenemedi", err);
            setCart(null);
        } finally {
            setLoading(false);
        }
    };

    const increaseAmount = async (productId) => {
        try {
            const item = cart.items.find(i => i.productId === productId);
            if (!item) return;

            const newAmount = item.amount + 1;

            await ShoppingCartService.updateAmount(
                cart.id,
                productId,
                newAmount
            );

            fetchCart();
        } catch (err) {
            console.error(err);
        }
    };

    const decreaseAmount = async (productId) => {
         try {
             const item = cart.items.find(i => i.productId === productId);
             if (!item) return;

             const newAmount = item.amount - 1;

             if (newAmount <= 0) {
                 await ShoppingCartService.removeProductFromCart(cart.id, productId);
             } else {
                 await ShoppingCartService.updateAmount(
                     cart.id,
                     productId,
                     newAmount
                 );
             }

             fetchCart();
         } catch (err) {
             console.error(err);
         }
     };

    const handleRemove = async (productId) => {
        try {
            await ShoppingCartService.removeProductFromCart(cart.id, productId);
            fetchCart();
        } catch (err) {
            alert("Ürün silinirken hata oluştu");
        }
    };

    const handleCheckoutNavigation = () => {
        if (!cart || items.length === 0) {
            return toast.warning("Sepetiniz boş!");
        }

        // Verileri Checkout sayfasına state olarak gönderiyoruz
        navigate("/checkout", {
            state: {
                cartItems: items,
                totalPrice: totalPrice
            }
        });
    };

    const items = cart?.items || [];
    const isEmpty = !cart || items.length === 0;

    if (!user) return <div className="cart-empty">Lütfen önce giriş yapın.</div>;
    if (loading) return <div className="loader">Yükleniyor...</div>;

    return (
        <div className="cart-container">

            <h1>Sepetim</h1>

            {isEmpty ? (
                <div className="cart-empty">
                    <div className="empty-icon">🛒</div>
                    <p>Sepetiniz boş</p>
                </div>
            ) : (
                <div className="cart-content">

                    <div className="cart-items">

                        {items.map((item) => (
                            <div key={item.id} className="cart-item">

                                <img
                                    src={`http://localhost:8771${item.image}`}
                                    alt={item.name}
                                    onError={(e) => {
                                        e.target.onerror = null;
                                        e.target.src = "https://via.placeholder.com/150";
                                    }}
                                />

                                <div className="item-details">
                                    <h3>{item.name}</h3>

                                    <p className="price">{item.price} TL</p>

                                    {/* 🔥 AMOUNT CONTROL */}
                                    <div className="amount-box">

                                        <button onClick={() => decreaseAmount(item.productId)}>
                                            <Minus size={16} />
                                        </button>

                                        <span>{item.amount}</span>

                                        <button onClick={() => increaseAmount(item.productId)}>
                                            <Plus size={16} />
                                        </button>

                                    </div>
                                </div>

                                <button
                                    className="remove-btn"
                                    onClick={() => handleRemove(item.productId)}
                                >
                                    <Trash2 size={18} />
                                </button>

                            </div>
                        ))}
                    </div>

                    <div className="cart-summary">
                        <h2>Sipariş Özeti</h2>

                        <div className="summary-row">
                            <span>Toplam</span>
                            <span>{totalPrice} TL</span>
                        </div>

                       <button
                           className="checkout-btn"
                           onClick={handleCheckoutNavigation}
                       >
                           Alışverişi Tamamla
                       </button>

                    </div>

                </div>
            )}
        </div>
    );
};

export default Cart;