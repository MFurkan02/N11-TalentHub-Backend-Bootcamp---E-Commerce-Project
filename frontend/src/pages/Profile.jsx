import React, { useState } from "react";
import UserService from "../services/UserService";
import "../Profile.css";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

const Profile = () => {

    const navigate = useNavigate();

    const user = JSON.parse(localStorage.getItem("user"));

    const [email, setEmail] = useState(user.email);
    const [password, setPassword] = useState("");

    const updateUser = async () => {
        try {
            await UserService.updateUser(user.id, {
                email,
                password
            });

            const updatedUser = {
                ...user,
                email: email
            };

            localStorage.setItem("user", JSON.stringify(updatedUser));

            toast.success("Güncellendi!");

            navigate("/products");

        } catch (err) {
            toast.error("Hata oluştu");
        }
    };

    return (
        <div className="profile-container">

            <div className="profile-card">

                <h2>👤 Profil Bilgileri</h2>

                <div className="info">
                    <p><b>Kullanıcı:</b> {user.username}</p>
                </div>

                <div className="input-group">
                    <label>Email</label>
                    <input
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Email"
                    />
                </div>

                <div className="input-group">
                    <label>Yeni Şifre</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Yeni şifre"
                    />
                </div>

                <button className="update-btn" onClick={updateUser}>
                    Güncelle
                </button>

            </div>

        </div>
    );
};

export default Profile;