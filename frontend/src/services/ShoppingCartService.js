import api from "../axiosConfig";

const API_BASE_URL = "/api/shopping-cart";

class ShoppingCartService {

    createCart(username) {
        return api.post(`${API_BASE_URL}`, null, {
            params: { name: username }
        });
    }

    getCartByUsername(username, lang = 'tr') {
        return api.get(`${API_BASE_URL}/by-name/${username}`, {
            headers: { 'Accept-Language': lang }
        });
    }

    updateAmount(cartId, productId, amount) {
        return api.put(`${API_BASE_URL}/${cartId}/items/${productId}?amount=${amount}`);
    }

    addProductsToCart(cartId, products) {
        return api.post(`${API_BASE_URL}/${cartId}`, products);
    }

    removeProductFromCart(cartId, productId) {
        return api.delete(`${API_BASE_URL}/${cartId}/products/${productId}`);
    }

    getTotalPrice(cartId) {
        return api.get(`${API_BASE_URL}/totalprice/${cartId}`);
    }

    clearCartByShoppingCartName(username) {
            return api.delete(`${API_BASE_URL}/clear/${username}`);
    }
}

export default new ShoppingCartService();