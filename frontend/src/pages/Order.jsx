import React, { useEffect, useState } from "react";
import OrderService from "../services/OrderService";
import "../Order.css";
import { toast } from "react-toastify";
import { useSearchParams } from "react-router-dom";

const Orders = () => {

    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    const user = JSON.parse(localStorage.getItem("user"));

    const [searchParams] = useSearchParams();
    const cartId = searchParams.get("cartId");

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
                <div className="orders-empty">
                    Henüz siparişiniz yok
                </div>
            ) : (
                <div className="orders-grid">

                    {orders.map(order => (
                        <div key={order.id} className="order-card">

                            <div className="order-header">
                                <h3>Order #{order.id}</h3>
                                <span className="status">{order.status || "PENDING"}</span>
                            </div>

                            <div className="order-info">
                                <p><b>Kullanıcı:</b> {order.username}</p>
                                <p><b>Toplam:</b> {order.totalPrice} TL</p>
                            </div>

                            <div className="order-items">
                                {order.items?.map((item, i) => (
                                    <div key={i} className="order-item">
                                        <span>{item.productName}</span>
                                        <span>x{item.amount}</span>
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