import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Navbar from "./components/Navbar";
import Signup from "./pages/Signup";
import Login from "./pages/Login";
import Products from "./pages/Product";
import Cart from "./pages/Cart";
import Profile from "./pages/Profile";
import Order from "./pages/Order"
import Checkout from "./pages/Checkout"
import FavoriteList from "./pages/FavoriteList"
import Search from "./pages/SearchResult"

import './App.css';

import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function App() {
  return (
    <Router>
      <div className="app-container">

        <Navbar />

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Products />} />
            <Route path="/products" element={<Products />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/orders" element={<Order />} />
            <Route path="/checkout" element={<Checkout />} />
            <Route path="/favorites" element={<FavoriteList />} />
            <Route path="/search" element={<Search />} />
            <Route path="*" element={<Navigate to="/" />} />
          </Routes>
        </main>

        <ToastContainer
          position="top-center"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop
          closeOnClick
          pauseOnHover
          draggable
          theme="light"
        />

      </div>
    </Router>
  );
}

export default App;