import React, { useEffect, useState } from "react";
import FavoriteListService from "../services/FavoriteListService";
import ShoppingCartService from "../services/ShoppingCartService"; // Sepet servisi eklendi
import { toast } from "react-toastify";
import { Trash2, Heart, PlusCircle, LayoutGrid, ShoppingCart, Minus, Plus } from "lucide-react";
import "../FavoriteList.css";

const FavoriteList = () => {
    const [lists, setLists] = useState([]);
    const [selectedList, setSelectedList] = useState(null);
    const [cart, setCart] = useState(null); // Sepet state'i eklendi
    const user = JSON.parse(localStorage.getItem("user"));

    useEffect(() => {
        if (user) {
            loadUserLists();
            fetchCart(); // Sayfa açılınca sepeti çek
        }
    }, []);

    // --- SEPET MANTIĞI (Products.jsx ile aynı) ---
    const fetchCart = async () => {
        try {
            const res = await ShoppingCartService.getCartByUsername(user.username);
            setCart(res.data);
        } catch (err) {
            console.log("Sepet yüklenemedi", err);
        }
    };

    const getAmount = (productId) => {
        if (!cart?.items) return 0;
        const item = cart.items.find(i => i.productId === productId);
        return item ? item.amount : 0;
    };

    const increase = async (productId) => {
        try {
            let currentCart = cart;
            if (!currentCart || !currentCart.id) {
                const res = await ShoppingCartService.createCart(user.username);
                currentCart = res.data;
                setCart(currentCart);
            }
            const currentAmount = getAmount(productId);
            if (currentAmount === 0) {
                await ShoppingCartService.addProductsToCart(currentCart.id, [{ productId, amount: 1 }]);
                toast.success("Ürün sepete eklendi");
            } else {
                await ShoppingCartService.updateAmount(currentCart.id, productId, currentAmount + 1);
            }
            fetchCart();
        } catch (err) {
            toast.error("Sepet işlemi başarısız");
        }
    };

    const decrease = async (productId) => {
        const currentAmount = getAmount(productId);
        if (currentAmount <= 0) return;
        try {
            if (currentAmount === 1) {
                await ShoppingCartService.removeProductFromCart(cart.id, productId);
                toast.info("Ürün sepetten çıkarıldı");
            } else {
                await ShoppingCartService.updateAmount(cart.id, productId, currentAmount - 1);
            }
            fetchCart();
        } catch (err) {
            toast.error("İşlem başarısız");
        }
    };

    // --- FAVORİ LİSTESİ MANTIĞI ---
    const loadUserLists = async () => {
        try {
            const res = await FavoriteListService.getAllByUsername(user.username);
            setLists(res.data);
            if (selectedList) {
                const updatedSelected = res.data.find(l => l.id === selectedList.id);
                setSelectedList(updatedSelected);
            }
        } catch (err) {
            toast.error("Favori listeleri yüklenemedi");
        }
    };

    const removeItem = async (listName, productId) => {
        try {
            await FavoriteListService.removeItem(user.username, listName, productId);
            toast.info("Ürün favorilerden çıkarıldı");
            loadUserLists();
        } catch (err) {
            toast.error("Ürün çıkarılamadı");
        }
    };

    return (
        <div className="fav-page-container">
            {/* Header ve Sidebar kısımları aynı kalabilir... */}
            <header className="fav-header">
                <div className="fav-title">
                    <Heart color="#ff4d4f" fill="#ff4d4f" size={32} />
                    <h1>Favori Listelerim</h1>
                </div>
                <button className="create-list-btn" onClick={() => {
                    const name = prompt("Yeni liste adı:");
                    if(name) FavoriteListService.getOrCreate(user.username, name).then(loadUserLists);
                }}>
                    <PlusCircle size={20} /> Yeni Liste Oluştur
                </button>
            </header>

            <div className="fav-content">
                <aside className="fav-sidebar">
                    {/* Listeleri listelediğin alan... */}
                    {lists.map(list => (
                        <div key={list.id} className={`list-card ${selectedList?.id === list.id ? "active" : ""}`} onClick={() => setSelectedList(list)}>
                            <span>{list.favoriteListName}</span>
                            <small>{list.products?.length || 0} Ürün</small>
                        </div>
                    ))}
                </aside>

                <main className="fav-products">
                    {selectedList ? (
                        <div className="fav-products-grid">
                            {selectedList.products?.map(item => {
                                const amount = getAmount(item.productId);
                                return (
                                    <div key={item.id} className="fav-product-card">
                                        <img src={`http://localhost:8771${item.image}`} alt={item.name} />
                                        <div className="product-info">
                                            <h4>{item.name}</h4>
                                            <p className="price">{item.price?.toLocaleString()} TL</p>

                                            {/* SEPET KONTROLLERİ */}
                                            <div className="fav-cart-controls">
                                                {amount > 0 ? (
                                                    <div className="amount-box-mini">
                                                        <button onClick={() => decrease(item.productId)}><Minus size={14} /></button>
                                                        <span>{amount}</span>
                                                        <button onClick={() => increase(item.productId)}><Plus size={14} /></button>
                                                    </div>
                                                ) : (
                                                    <button className="add-to-cart-mini" onClick={() => increase(item.productId)}>
                                                        <ShoppingCart size={16} /> Sepete Ekle
                                                    </button>
                                                )}
                                            </div>

                                            <button
                                                className="remove-item-link"
                                                onClick={() => removeItem(selectedList.favoriteListName, item.productId)}
                                            >
                                                Favoriden Çıkar
                                            </button>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    ) : <p>Lütfen bir liste seçin.</p>}
                </main>
            </div>
        </div>
    );
};

export default FavoriteList;