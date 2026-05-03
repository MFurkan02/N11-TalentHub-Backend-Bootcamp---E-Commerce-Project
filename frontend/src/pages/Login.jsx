import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import UserService from '../services/UserService';
import '../Login.css'; // Yeni CSS dosyanı buraya import et
import { toast } from "react-toastify";

function Login() {
  const [loginData, setLoginData] = useState({
    username: '',
    password: ''
  });

  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await UserService.signin(loginData);
      localStorage.setItem("token", response.data.accessToken);
      localStorage.setItem("refreshToken", response.data.refreshToken);
      localStorage.setItem("user", JSON.stringify(response.data));

      toast.success("Hoş geldiniz!");
      navigate("/products");
    } catch (err) {
      toast.error("Kullanıcı adı veya şifre hatalı!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page-wrapper">
      <div className="login-card">
        <h2>Giriş Yap</h2>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label>Kullanıcı Adı</label>
            <input
              type="text"
              required
              placeholder="Kullanıcı adınızı girin"
              value={loginData.username}
              onChange={(e) =>
                setLoginData({ ...loginData, username: e.target.value })
              }
            />
          </div>

          <div className="form-group">
            <label>Şifre</label>
            <input
              type="password"
              required
              placeholder="••••••••"
              value={loginData.password}
              onChange={(e) =>
                setLoginData({ ...loginData, password: e.target.value })
              }
            />
          </div>

          <button
            type="submit"
            className="login-submit-btn"
            disabled={loading}
          >
            {loading ? "Giriş Yapılıyor..." : "Giriş Yap"}
          </button>
        </form>

        <p className="login-redirect">
          Hesabın yok mu? <Link to="/signup">Kayıt Ol</Link>
        </p>
      </div>
    </div>
  );
}

export default Login;