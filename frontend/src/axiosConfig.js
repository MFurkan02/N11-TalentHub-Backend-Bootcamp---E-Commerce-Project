import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8763"
});

// REQUEST
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

// RESPONSE (REFRESH)
api.interceptors.response.use(
    res => res,
    async error => {

        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem("refreshToken");

                const res = await axios.post(
                    "http://localhost:8763/api/user/refresh",
                    { refreshToken }
                );

                localStorage.setItem("token", res.data.accessToken);

                originalRequest.headers.Authorization =
                    `Bearer ${res.data.accessToken}`;

                return api(originalRequest);

            } catch (err) {
                localStorage.clear();
                window.location.href = "/login";
                return Promise.reject(err);
            }
        }

        return Promise.reject(error);
    }
);

export default api;