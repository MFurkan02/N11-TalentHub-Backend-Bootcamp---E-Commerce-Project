import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import UserService from '../services/UserService';
import '../Login.css'; // Ortak stiller için Login.css kullanıyoruz
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
      navigate("/login");
    } catch (err) {
      console.error("Kayıt Hatası:", err);
      toast.error("Kayıt sırasında bir hata oluştu veya bu kullanıcı zaten mevcut.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page-wrapper">
      <div className="login-card">
        <h2>Hesap Oluştur</h2>
        <p style={{ textAlign: 'center', color: '#6b7280', marginBottom: '20px' }}>
          Aramıza katılmak için formu doldurun.
        </p>

        <form onSubmit={handleSignup}>
          <div className="form-group">
            <label htmlFor="username">Kullanıcı Adı</label>
            <input
              id="username"
              type="text"
              placeholder="Kullanıcı adınızı seçin"
              required
              value={user.username}
              onChange={(e) => setUser({ ...user, username: e.target.value })}
            />
          </div>

          <div className="form-group">
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

          <div className="form-group">
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

          <button type="submit" className="login-submit-btn" disabled={loading}>
            {loading ? "Kaydediliyor..." : "Kaydol"}
          </button>
        </form>

        <p className="login-redirect">
          Zaten bir hesabın var mı? <Link to="/login">Giriş Yap</Link>
        </p>
      </div>
    </div>
  );
}

export default Signup;