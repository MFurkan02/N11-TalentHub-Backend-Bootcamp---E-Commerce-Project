import axios from "axios";

const BASE_URL = "http://localhost:8763/api/favorite-list";

class FavoriteListService {

    // 🟢 Tüm listeleri getir (Admin veya debug için)
    getAll() {
        return axios.get(`${BASE_URL}`);
    }

    // 🟡 Kullanıcının tüm listelerini getir
    getAllByUsername(username) {
        return axios.get(`${BASE_URL}/user/${username}`);
    }

    // 🔍 Belirli bir listeyi getir veya yoksa oluştur (Backend'deki getOrCreateList)
    getOrCreate(username, listName) {
        return axios.get(`${BASE_URL}/find`, {
            params: { username, listName }
        });
    }

    // 🔵 Listeye Ürün Ekle
    // Params: username, listName, productId
    addItem(username, listName, productId) {
        return axios.post(`${BASE_URL}/add`, null, {
            params: {
                username: username,
                listName: listName,
                productId: productId
            }
        });
    }

    // 🔴 Listeden Ürün Çıkar
    // Params: username, listName, productId
    removeItem(username, listName, productId) {
        return axios.delete(`${BASE_URL}/remove`, {
            params: {
                username: username,
                listName: listName,
                productId: productId
            }
        });
    }

    // 🟡 ID ile getirme (Gerekiyorsa)
    getById(id) {
        return axios.get(`${BASE_URL}/${id}`);
    }
}

export default new FavoriteListService();