import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();

  const userData = localStorage.getItem("user");
  const user = userData ? JSON.parse(userData) : null;

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <Link to="/">n11<span>Bootcamp</span></Link>
      </div>

      <div className="navbar-links">
        <Link to="/products" className="nav-item">Ürünler</Link>

        {user ? (
          <>
            {/* SEPET */}
            <Link to="/cart" className="cart-link">
              <span className="cart-icon">🛒</span> Sepetim
            </Link>

            {/* KULLANICI DROPDOWN */}
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