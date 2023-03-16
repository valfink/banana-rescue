import React from 'react';
import './App.css';
import {Navigate, Route, Routes} from "react-router-dom";
import FoodItemGallery from "./page/FoodItemGallery";

function App() {
  return (
    <div className="App">
        <Routes>
            <Route path={"/"} element={<Navigate to={"/food"} />} />
            <Route path={"/food"} element={<FoodItemGallery />} />
        </Routes>
    </div>
  );
}

export default App;
