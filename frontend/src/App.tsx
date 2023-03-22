import React, {useState} from 'react';
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
import {SetAppIsLoadingContext} from "./context/SetAppIsLoadingContext";
import LoadingScreen from "./component/LoadingScreen";

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
    const [appIsLoading, setAppIsLoading] = useState(false);

    return (
        <div className="App">
            {appIsLoading && <LoadingScreen/>}
            <SetAppIsLoadingContext.Provider value={setAppIsLoading}>
                <UserContext.Provider value={useUserAuth(setAppIsLoading)}>
                    <Routes>
                        <Route path={"/"} element={<Navigate to={"/food"}/>}/>
                        <Route path={"/food"} element={<FoodItemGallery/>}/>
                        <Route path={"/add-food"} element={<FoodItemAddPage/>}/>
                        <Route path={"/signup"} element={<UserSignUpPage/>}/>
                        <Route path={"/login"} element={<UserLogInPage/>}/>
                    </Routes>
                </UserContext.Provider>
            </SetAppIsLoadingContext.Provider>
        </div>
    );
}

export default App;
