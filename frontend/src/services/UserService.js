import api from "../axiosConfig";

const API_URL = "http://localhost:8763/api/user";

class UserService {

    // Kayıt
    signup(userData) {
        return api.post(`${API_URL}/signup`, userData);
    }

    // Login
    signin(loginData) {
        return api.post(`${API_URL}/signin`, loginData);
    }

    // Kullanıcı sil
    deleteUser(userId) {
        return api.delete(`${API_URL}/delete/${userId}`);
    }

    // Güncelle
    updateUser(userId, updateData) {
        return api.put(`${API_URL}/update/${userId}`, updateData);
    }
}

export default new UserService();