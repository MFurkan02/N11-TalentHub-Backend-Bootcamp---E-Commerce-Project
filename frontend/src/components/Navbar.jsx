import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Search } from 'lucide-react'; // Büyüteç ikonu için
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");

  const userData = localStorage.getItem("user");
  const user = userData ? JSON.parse(userData) : null;

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/login");
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      // Arama sonuçları sayfasına yönlendiriyoruz
      navigate(`/products?q=${searchQuery}`);
      setSearchQuery(""); // Arama yaptıktan sonra kutuyu temizle
    }
  };

  return (
    <nav className="navbar">
      {/* 1. LOGO */}
      <div className="navbar-logo">
        <Link to="/">n11<span>Bootcamp</span></Link>
      </div>

      {/* 2. ARAMA ÇUBUĞU (YENİ) */}
      <div className="navbar-search">
        <form onSubmit={handleSearch}>
          <input
            type="text"
            placeholder="Ürün, marka veya kategori ara..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button type="submit" className="search-btn">
            <Search size={20} />
          </button>
        </form>
      </div>

      {/* 3. LİNKLER */}
      <div className="navbar-links">
        <Link to="/products" className="nav-item">Ürünler</Link>

        {user ? (
          <>
            <Link to="/cart" className="cart-link">
              <span className="cart-icon">🛒</span> Sepetim
            </Link>

            <div className="user-dropdown-container">
              <span className="user-welcome">
                👤 {user.username}
              </span>

              <div className="dropdown-menu">
                <div className="dropdown-arrow"></div>
                <Link to="/profile" className="dropdown-item">Hesabım</Link>
                <Link to="/favorites" className="dropdown-item">Favori Listelerim</Link>
                <Link to="/orders" className="dropdown-item">Siparişlerim</Link>
                <hr />
                <button onClick={handleLogout} className="dropdown-item logout-item">
                  Çıkış Yap
                </button>
              </div>
            </div>
          </>
        ) : (
          <>
            <Link to="/login" className="login-btn">Giriş Yap</Link>
            <Link to="/signup" className="signup-btn">Kaydol</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;