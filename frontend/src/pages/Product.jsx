import React, { useState, useEffect } from 'react';
import ProductService from '../services/ProductService';
import ShoppingCartService from '../services/ShoppingCartService';
import SearchService from "../services/SearchService";
import '../Products.css';
import { toast } from "react-toastify";
import { useLocation } from 'react-router-dom';
import FavoriteListService from '../services/FavoriteListService';

const Products = () => {

    const location = useLocation();
    const [products, setProducts] = useState([]);
    const [cart, setCart] = useState(null);
    const [pageInfo, setPageInfo] = useState({ page: 0, totalPages: 0 });
    const [lang, setLang] = useState('tr');
    const [loading, setLoading] = useState(true);
    const [addingId, setAddingId] = useState(null);
    const [favoriteList, setFavoriteList] = useState([]);
    const [showListSelector, setShowListSelector] = useState(null); // Hangi ürün için liste seçici açık? (productId)
    const [newListName, setNewListName] = useState(""); // Yeni liste oluşturmak için input
    const [userLists, setUserLists] = useState([]); // Kullanıcının sahip olduğu tüm listeler

    const user = JSON.parse(localStorage.getItem("user"));
    const DEFAULT_LIST_NAME = "Favorilerim";

    // URL'deki ?q= parametresini yakala
    const queryParams = new URLSearchParams(location.search);
    const searchQuery = queryParams.get("q");

    useEffect(() => {

        // Eğer URL'de 'q' parametresi varsa SearchService'i kullan
        if (searchQuery) {
            fetchSearchResults(searchQuery, 0);
        } else {
            // Yoksa normal ürün listesini getir
            loadProducts(0);
        }

        if (user) {
            fetchCart();
            fetchUserFavoriteList();
            fetchAllUserLists();
        }
        // location.search eklendi: URL her değiştiğinde useEffect çalışır
    }, [lang, searchQuery, location.search]);


    const fetchCart = async () => {
        try {
            const res = await ShoppingCartService.getCartByUsername(user.username);
            setCart(res.data);
        } catch (err) {
            console.log(err);
        }
    };

    // 🔍 ARAMA SONUÇLARINI GETİR (Search Service - Spring Page yapısı döner)
    const fetchSearchResults = async (query, page = 0) => {
        setLoading(true);
        try {
            const res = await SearchService.search(query, page, 6);

            setProducts(res.data.content || []);
            setPageInfo({
                page: res.data.number, // number kullanmalısın
                totalPages: res.data.totalPages
            });
        } catch (err) {
            toast.error("Arama yapılırken bir hata oluştu");
        } finally {
            setLoading(false);
        }
    };

    // 📦 NORMAL ÜRÜNLERİ GETİR (Product Service - Senin Map.of yapın döner)
    const loadProducts = async (page) => {
        setLoading(true);
        try {
            const res = await ProductService.getPagedProducts(page, 6, lang);

            // Product Service (Map.of) JSON yapısı:
            // { "items": [], "page": 0, "totalPages": 5 ... }
            setProducts(res.data.items || []);
            setPageInfo({
                page: res.data.page, // page kullanmalısın
                totalPages: res.data.totalPages
            });
        } catch (err) {
            toast.error("Ürünler yüklenemedi");
        } finally {
            setLoading(false);
        }
    };

     const fetchUserFavoriteList = async () => {
                try {
                    // Backend'deki getOrCreateList mantığını çağıran servis
                    const res = await FavoriteListService.getOrCreate(user.username, DEFAULT_LIST_NAME);
                    setFavoriteList(res.data);
                } catch (err) {
                    console.error("Favori listesi yüklenemedi", err);
                }
            };

     const fetchAllUserLists = async () => {
         try {
             const res = await FavoriteListService.getAllByUsername(user.username);
             setUserLists(res.data);
         } catch (err) {
             console.error("Listeler yüklenemedi", err);
         }
     };

    const handleAddToList = async (productId, listName) => {
        try {
            await FavoriteListService.addItem(user.username, listName, productId);
            toast.success(`Ürün "${listName}" listesine eklendi`);
            setShowListSelector(null); // Seçiciyi kapat
            fetchAllUserLists(); // Listeleri güncelle (kalp ikonları için)
        } catch (err) {
            toast.error("Ekleme başarısız");
        }
    };

    const handleRemoveFromList = async (productId, listName) => {
        try {
            // Backend'e silme isteği atıyoruz
            await FavoriteListService.removeItem(user.username, listName, productId);

            toast.info(`Ürün "${listName}" listesinden çıkarıldı`);

            // UI'daki kalplerin ve listelerin güncellenmesi için listeleri tekrar çek
            fetchAllUserLists();
        } catch (err) {
            console.error("Favoriden çıkarma hatası:", err);
            toast.error("Ürün listeden çıkarılamadı");
        }
    };



    // 🔥 CART AMOUNT BUL
    const getAmount = (productId) => {
        if (!cart?.items) return 0;
        const item = cart.items.find(i => i.productId === productId);
        return item ? item.amount : 0;
    };

    const increase = async (productId) => {
        if (!user) return toast.warning("Giriş yapmalısın");

        try {
            let currentCart = cart;

            // 1. ADIM: Sepet var mı kontrol et. Yoksa (veya id'si yoksa) oluştur.
            if (!currentCart || !currentCart.id) {
                // Backend'de sepeti oluşturuyoruz
                const res = await ShoppingCartService.createCart(user.username);
                currentCart = res.data; // Yeni oluşturulan sepeti alıyoruz
                setCart(currentCart); // State'e kaydediyoruz
            }

            // 2. ADIM: Miktarı kontrol et
            const currentAmount = getAmount(productId);

            if (currentAmount === 0) {
                // Ürün sepette hiç yoksa POST (addProductsToCart)
                await ShoppingCartService.addProductsToCart(currentCart.id, [
                    { productId, amount: 1 }
                ]);
                toast.success("Ürün sepete eklendi");
            } else {
                // Ürün zaten varsa PUT (updateAmount)
                const newAmount = currentAmount + 1;
                await ShoppingCartService.updateAmount(
                    currentCart.id,
                    productId,
                    newAmount
                );
                toast.success("Miktar artırıldı");
            }

            // 3. ADIM: Son durumu çek
            fetchCart();

        } catch (err) {
            console.error("Hata oluştu:", err);
            toast.error("Sepet işlemi başarısız oldu");
        }
    };

    // ➖ AZALT (Update Amount Mantığı ile)
    const decrease = async (productId) => {
        const currentAmount = getAmount(productId);
        if (currentAmount <= 0) return; // Zaten 0 ise işlem yapma

        try {
            const newAmount = currentAmount - 1;

            if (newAmount === 0) {
                // Miktar 0'a düşerse ürünü sepetten tamamen sil
                await ShoppingCartService.removeProductFromCart(cart.id, productId);
                toast.info("Ürün sepetten çıkarıldı");
            } else {
                // Miktar 1'den büyükse güncelle
                await ShoppingCartService.updateAmount(
                    cart.id,
                    productId,
                    newAmount
                );
            toast.info("Ürün miktarı azaltıldı")
            }

            fetchCart(); // UI'ı tazele

        } catch (err) {
            console.error(err);
            toast.error("İşlem başarısız");
        }
    };


    const ensureFavoriteList = async () => {
        if (favoriteList) return favoriteList;

        const name = prompt("Favori listen yok. Bir isim gir:");

        if (!name) {
            toast.warning("Liste oluşturulmadı");
            return null;
        }

        try {
            const res = await FavoriteListService.create(name);

            setFavoriteList(res.data);
            toast.success("Favori liste oluşturuldu");

            return res.data;

        } catch (err) {
            toast.error("Liste oluşturulamadı");
            return null;
        }
    };



        const toggleFavorite = async (productId) => {
            if (!user) {
                toast.warning("Giriş yapmalısın");
                return;
            }

            try {
                // Ürün listede var mı kontrolü
                const isFav = favoriteList?.products?.some(p => p.productId === productId);

                if (isFav) {
                    // Varsa: Kaldır (username ve listName ile)
                    await FavoriteListService.removeItem(user.username, DEFAULT_LIST_NAME, productId);
                    toast.info("Favorilerden çıkarıldı");
                } else {
                    // Yoksa: Ekle (username ve listName ile)
                    await FavoriteListService.addItem(user.username, DEFAULT_LIST_NAME, productId);
                    toast.success("Favorilere eklendi");
                }

                // Güncel listeyi tekrar çekerek UI'ı güncelle
                fetchUserFavoriteList();
            } catch (err) {
                console.error("Favori işlemi hatası:", err);
                toast.error("İşlem başarısız");
            }
        };

    const changePage = (newPage) => {
        // Sayfayı anında en üste taşır
        window.scrollTo(0, 0);

        // Hangi kaynaktan veri çekileceğine karar verir
        if (searchQuery) {
            fetchSearchResults(searchQuery, newPage);
        } else {
            loadProducts(newPage);
        }
    };

    return (
        <div className="product-page-container">
            <div className="product-page-content">

                {/* HEADER */}
                <header className="modern-header">
                    <div className="title-section">
                        <h1>🛍️ Ürünler</h1>
                        <p>{products.length} ürün listeleniyor</p>
                    </div>


                </header>

                {/* LOADING */}
                {loading ? (
                    <div className="modern-loader">Yükleniyor...</div>
                ) : (
                    <>
                        {/* GRID */}
                        <div className="modern-grid">
                            {products.map((product) => {

                                const amount = getAmount(product.id);

                                return (
                                    <div key={product.id} className="modern-card">

                                        <div className="image-wrapper">

                                            {/* Favori Butonu Konteynırı */}
                                            <div className="favorite-container">
                                                <button
                                                    className="favorite-btn"
                                                    onClick={() => setShowListSelector(showListSelector === product.id ? null : product.id)}
                                                >
                                                    {/* Ürün herhangi bir listede varsa dolu kalp */}
                                                    {userLists.some(list => list.products?.some(p => p.productId === product.id))
                                                        ? "❤️"
                                                        : "🤍"
                                                    }
                                                </button>

                                                {/* Liste Seçici ve Düzenleyici Dropdown */}
                                                {showListSelector === product.id && (
                                                    <div className="list-selector-dropdown">
                                                        <h4>Listelerim</h4>

                                                        <div className="existing-lists">
                                                            {userLists.map(list => {
                                                                // Ürün bu listenin içinde mi?
                                                                const isItemInThisList = list.products?.some(p => p.productId === product.id);

                                                                return (
                                                                    <div key={list.id} className="list-item-row">
                                                                        <span className="list-name">{list.favoriteListName}</span>

                                                                        {isItemInThisList ? (
                                                                            // Ürün listedeyse: ÇIKAR BUTONU
                                                                            <button
                                                                                className="remove-from-list-btn"
                                                                                onClick={() => handleRemoveFromList(product.id, list.favoriteListName)}
                                                                            >
                                                                                ❌ Çıkar
                                                                            </button>
                                                                        ) : (
                                                                            // Ürün listede değilse: EKLE BUTONU
                                                                            <button
                                                                                className="add-to-existing-list-btn"
                                                                                onClick={() => handleAddToList(product.id, list.favoriteListName)}
                                                                            >
                                                                                ➕ Ekle
                                                                            </button>
                                                                        )}
                                                                    </div>
                                                                );
                                                            })}
                                                        </div>

                                                        <hr />

                                                        {/* Yeni Liste Oluşturma Alanı */}
                                                        <div className="new-list-input">
                                                            <input
                                                                type="text"
                                                                placeholder="Yeni liste ismi..."
                                                                value={newListName}
                                                                onChange={(e) => setNewListName(e.target.value)}
                                                            />
                                                            <button onClick={() => {
                                                                if(newListName) {
                                                                    handleAddToList(product.id, newListName);
                                                                    setNewListName("");
                                                                }
                                                            }}>
                                                                Oluştur ve Ekle
                                                            </button>
                                                        </div>
                                                    </div>
                                                )}
                                            </div>

                                            <img
                                                src={`http://localhost:8771${product.img}`}
                                                alt={product.brand}
                                                onError={(e) => {
                                                    e.target.onerror = null;
                                                    e.target.src = "https://via.placeholder.com/300";
                                                }}
                                            />

                                            {product.categoryKey &&
                                                <span className="badge">{product.categoryKey}</span>}
                                        </div>

                                        <div className="content-wrapper">

                                            <div className="brand-row">
                                                <span className="brand-text">{product.brand}</span>
                                                <span
                                                    className="color-dot"
                                                    style={{ backgroundColor: product.color || '#ddd' }}
                                                ></span>
                                            </div>

                                            <h3 className="item-title">
                                                {product.title || product.brand}
                                            </h3>

                                            <p className="item-labels">
                                                {product.labels}
                                            </p>

                                            <div className="card-bottom">

                                                <div className="price-box">
                                                    <span className="price-val">
                                                        {product.price?.toLocaleString()}
                                                    </span>
                                                    <span className="price-cur">TL</span>
                                                </div>

                                                <div className="amount-box">
                                                    {amount > 0 ? (
                                                        // MİKTAR 0'DAN BÜYÜKSE: - RAKAM + GÖSTER
                                                        <>
                                                            <button className="control-btn" onClick={() => decrease(product.id)}>-</button>
                                                            <span className="amount-display">{amount}</span>
                                                            <button className="control-btn" onClick={() => increase(product.id)}>+</button>
                                                        </>
                                                    ) : (
                                                        // MİKTAR 0 İSE: SADECE EKLE BUTONU / İKONU GÖSTER
                                                        <button className="add-to-cart-btn" onClick={() => increase(product.id)}>
                                                            <span className="add-icon">🛒</span>
                                                        </button>
                                                    )}
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        {/* PAGINATION */}
                        {/* PAGINATION */}
                        <div className="modern-pagination">
                            <button
                                disabled={pageInfo.page === 0}
                                onClick={() => changePage(pageInfo.page - 1)}
                            >
                                ←
                            </button>

                            <span>
                               {pageInfo.page + 1} / {Math.max(pageInfo.totalPages, 1)}
                            </span>

                            <button
                                disabled={pageInfo.page >= pageInfo.totalPages - 1 || pageInfo.totalPages === 0}
                                onClick={() => changePage(pageInfo.page + 1)}
                            >
                                →
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default Products;