import React from 'react';
import './App.css';
import {Navigate, Route, Routes} from "react-router-dom";
import FoodItemGallery from "./page/FoodItemGallery";
import UserSignUpPage from "./page/UserSignUpPage";

function App() {
  return (
    <div className="App">
        <Routes>
            <Route path={"/"} element={<Navigate to={"/food"} />} />
            <Route path={"/food"} element={<FoodItemGallery/>}/>
            <Route path={"/signup"} element={<UserSignUpPage/>}/>
        </Routes>
    </div>
  );
}

export default App;
