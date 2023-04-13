import React, {useMemo, useState} from 'react';
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
import {AppIsLoadingContext} from "./context/AppIsLoadingContext";
import LoadingScreen from "./modal/LoadingScreen";
import FoodItemDetailsPage from "./page/FoodItemDetailsPage";
import FoodItemEditPage from "./page/FoodItemEditPage";
import HeaderBarAndFullScreenNav from "./page/HeaderBarAndFullScreenNav";
import {Toaster} from "react-hot-toast";
import ChatDetailsPage from "./page/ChatDetailsPage";
import ChatGallery from "./page/ChatGallery";
import Div100vh from "react-div-100vh";
import RadarDetailsPage from "./page/RadarDetailsPage";

axios.defaults.timeout = 5000;
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
    const [appIsLoading, setAppIsLoading] = useState(0);
    const loadingContext = useMemo(() => ({appIsLoading, setAppIsLoading}), [appIsLoading]);
    const [appContentIsScrolled, setAppContentIsScrolled] = useState(false);

    function handleAppScroll(e: React.UIEvent) {
        setAppContentIsScrolled(e.currentTarget.scrollTop > 0);
    }

    return (
        <Div100vh className="App" onScroll={handleAppScroll}>
            <AppIsLoadingContext.Provider value={loadingContext}>
                <UserContext.Provider value={useUserAuth(setAppIsLoading)}>
                    {appIsLoading !== 0 && <LoadingScreen/>}
                    <HeaderBarAndFullScreenNav displayHeaderBarShadow={appContentIsScrolled}/>
                    <Routes>
                        <Route path={"/"} element={<Navigate to={"/food"}/>}/>
                        <Route path={"/food"} element={<FoodItemGallery/>}/>
                        <Route path={"/food/my-items"} element={<FoodItemGallery showOnlyMyItems={true}/>}/>
                        <Route path={"/food/:id"} element={<FoodItemDetailsPage/>}/>
                        <Route path={"/food/:id/edit"} element={<FoodItemEditPage/>}/>
                        <Route path={"/add-food"} element={<FoodItemAddPage/>}/>
                        <Route path={"/signup"} element={<UserSignUpPage/>}/>
                        <Route path={"/login"} element={<UserLogInPage/>}/>
                        <Route path={"/chats"} element={<ChatGallery/>}/>
                        <Route path={"/chats/:id"} element={<ChatDetailsPage/>}/>
                        <Route path={"/my-radar"} element={<RadarDetailsPage/>}/>
                    </Routes>
                    <Toaster/>
                </UserContext.Provider>
            </AppIsLoadingContext.Provider>
        </Div100vh>
    );
}

export default App;
