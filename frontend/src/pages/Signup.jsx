import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import UserService from '../services/UserService';
import '../App.css'; // Modern stiller bu dosyanın içinde olmalı
import { toast } from "react-toastify";

function Signup() {
  const [user, setUser] = useState({
    username: '',
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await UserService.signup(user);
      toast.success("✨ Kayıt Başarılı! Şimdi giriş yapabilirsiniz.");
      navigate("/login"); // Kayıttan sonra kullanıcıyı login'e yönlendiriyoruz
    } catch (err) {
      console.error("Kayıt Hatası:", err);
      // CORS hatası olsa bile veritabanına kayıt düştüğü için
      // kullanıcıyı bilgilendiriyoruz.
      toast.error("İşlem tamamlanmış olabilir. Lütfen giriş yapmayı deneyin.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-card">
        <h2>Hesap Oluştur</h2>
        <form onSubmit={handleSignup}>

          <div className="input-group">
            <label htmlFor="username">Kullanıcı Adı</label>
            <input
              id="username"
              type="text"
              placeholder="Örn: furkan123"
              required
              value={user.username}
              onChange={(e) => setUser({ ...user, username: e.target.value })}
            />
          </div>

          <div className="input-group">
            <label htmlFor="email">E-posta</label>
            <input
              id="email"
              type="email"
              placeholder="ornek@mail.com"
              required
              value={user.email}
              onChange={(e) => setUser({ ...user, email: e.target.value })}
            />
          </div>

          <div className="input-group">
            <label htmlFor="password">Şifre</label>
            <input
              id="password"
              type="password"
              placeholder="••••••••"
              required
              value={user.password}
              onChange={(e) => setUser({ ...user, password: e.target.value })}
            />
          </div>

          <button type="submit" className="signup-button" disabled={loading}>
            {loading ? "Kaydediliyor..." : "Kaydol"}
          </button>

        </form>

        <p className="auth-footer">
          Zaten bir hesabın var mı? <Link to="/login">Giriş Yap</Link>
        </p>
      </div>
    </div>
  );
}

export default Signup;