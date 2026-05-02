import axios from "axios";

const BASE_URL = "http://localhost:8763/api/orders";

class OrderService {

    createOrder(data) {
        return axios.post(`${BASE_URL}/checkout`, data);
    }

    getAllOrders() {
        return axios.get(BASE_URL + "/all");
    }

    getOrderById(id) {
        return axios.get(`${BASE_URL}/${id}`);
    }

    getOrdersByUser(username) {
        return axios.get(`${BASE_URL}/user/${username}`);
    }
}

export default new OrderService();