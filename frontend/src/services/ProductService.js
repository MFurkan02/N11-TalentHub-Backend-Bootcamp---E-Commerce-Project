import axios from 'axios';

const API_BASE_URL = "http://localhost:8763/api/product"; // Gateway portu

class ProductService {
    getPagedProducts(page = 0, size = 8) {
        return axios.get(`${API_BASE_URL}/paged`, {
            params: { page, size },
        });
    }

    getProductById(id) {
        return axios.get(`${API_BASE_URL}/${id}`, {

        });
    }
}

export default new ProductService();