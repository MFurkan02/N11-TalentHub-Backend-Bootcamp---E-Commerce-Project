import axios from "axios";

const API_URL = "http://localhost:8763/api/search"; // Gateway portun veya 8772 gibi search service portun

const SearchService = {
    // q: arama metni, p: sayfa numarası, s: sayfa boyutu
    search: (query, page = 0, size = 3) => {
        return axios.get(`${API_URL}?q=${query}&page=${page}&size=${size}`);
    }
};

export default SearchService;