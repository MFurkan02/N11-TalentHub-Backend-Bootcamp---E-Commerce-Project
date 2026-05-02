import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import UserService from '../services/UserService';
import '../App.css';
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

      console.log("LOGIN RESPONSE:", response.data);

      // 🔥 TOKENLARI DOĞRU KAYDET
      localStorage.setItem("token", response.data.accessToken);
      localStorage.setItem("refreshToken", response.data.refreshToken);
      localStorage.setItem("user", JSON.stringify(response.data));

      toast.success("Hoş geldiniz!");

      navigate("/products");

    } catch (err) {
      console.error(err);
      toast.error("Kullanıcı adı veya şifre hatalı!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-card">

        <h2>Giriş Yap</h2>

        <form onSubmit={handleLogin}>

          <div className="input-group">
            <label>Kullanıcı Adı</label>
            <input
              type="text"
              required
              value={loginData.username}
              onChange={(e) =>
                setLoginData({ ...loginData, username: e.target.value })
              }
            />
          </div>

          <div className="input-group">
            <label>Şifre</label>
            <input
              type="password"
              required
              value={loginData.password}
              onChange={(e) =>
                setLoginData({ ...loginData, password: e.target.value })
              }
            />
          </div>

          <button
            type="submit"
            className="signup-button"
            disabled={loading}
          >
            {loading ? "Giriş Yapılıyor..." : "Giriş Yap"}
          </button>

        </form>

        <p style={{ textAlign: 'center', marginTop: '1rem' }}>
          Hesabın yok mu? <Link to="/signup">Kayıt Ol</Link>
        </p>

      </div>
    </div>
  );
}

export default Login;