import React, { useEffect, useState } from "react";
import OrderService from "../services/OrderService";
import "../Order.css";
import { toast } from "react-toastify";

const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    const user = JSON.parse(localStorage.getItem("user"));

    useEffect(() => {
        if (user) fetchOrders();
        else setLoading(false);
    }, []);

    const fetchOrders = async () => {
        try {
            const res = await OrderService.getOrdersByUser(user.username);
            setOrders(res.data);
        } catch (err) {
            toast.error("Siparişler yüklenemedi");
        } finally {
            setLoading(false);
        }
    };

    // Statüye göre CSS sınıfı belirleyen fonksiyon
    const getStatusClass = (status) => {
        if (!status) return "status-pending";
        switch (status.toUpperCase()) {
            case 'COMPLETED': return 'status-completed';
            case 'CANCELLED': return 'status-cancelled';
            case 'SHIPPED': return 'status-shipped';
            default: return 'status-pending';
        }
    };

    if (!user) {
        return <div className="orders-empty">Lütfen giriş yapın</div>;
    }

    if (loading) {
        return <div className="loader">Yükleniyor...</div>;
    }

    return (
        <div className="orders-container">
            <h1>📦 Siparişlerim</h1>

            {orders.length === 0 ? (
                <div className="orders-empty">Henüz siparişiniz yok</div>
            ) : (
                <div className="orders-grid">
                    {orders.map(order => (
                        <div key={order.id || Math.random()} className="order-card">
                            <div className="order-header">
                                {/* ID kısmını sildik, sadece statü kaldı */}
                                <span className={`status ${getStatusClass(order.status)}`}>
                                    {order.status || "Hazırlanıyor"}
                                </span>
                            </div>

                            <div className="order-info">
                                <p><b>Kullanıcı:</b> {order.username}</p>
                                <p><b>Toplam Tutar:</b> <span className="price-text">{order.totalPrice} TL</span></p>
                            </div>

                            <div className="order-items">
                                <p style={{fontSize: '12px', color: '#6b7280', marginBottom: '5px'}}>Ürünler:</p>
                                {order.items?.map((item, i) => (
                                    <div key={i} className="order-item">
                                        <span>{item.productName}</span>
                                        <span>{item.amount}</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Orders;