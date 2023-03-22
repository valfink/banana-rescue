import React from 'react';
import './App.css';
import {Navigate, Route, Routes} from "react-router-dom";
import FoodItemGallery from "./page/FoodItemGallery";
import UserSignUpPage from "./page/UserSignUpPage";
import axios from "axios";
import Cookies from "js-cookie";
import UserLogInPage from "./page/UserLogInPage";
import FoodItemAddPage from "./page/FoodItemAddPage";
import {UserContext} from "./context/UserContext";
import useUserAuth from "./hook/useUserAuth";

axios.interceptors.request.use(config => {
    if (["put", "post", "delete"].includes(config.method || "")) {
        return fetch("/api/csrf")
            .then(() => {
                config.headers["X-XSRF-TOKEN"] = Cookies.get("XSRF-TOKEN");
                return config;
            });
    } else {
        return config;
    }
}, error => Promise.reject(error));

function App() {
    const {user, redirectToLogin} = useUserAuth();

    return (
        <div className="App">
            <UserContext.Provider value={{user, redirectToLogin}}>
                <Routes>
                    <Route path={"/"} element={<Navigate to={"/food"}/>}/>
                    <Route path={"/food"} element={<FoodItemGallery/>}/>
                    <Route path={"/add-food"} element={<FoodItemAddPage/>}/>
                    <Route path={"/signup"} element={<UserSignUpPage/>}/>
                    <Route path={"/login"} element={<UserLogInPage/>}/>
                </Routes>
            </UserContext.Provider>
        </div>
    );
}

export default App;
