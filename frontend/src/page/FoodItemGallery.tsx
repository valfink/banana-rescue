import {useEffect, useState} from "react";
import {FoodItem} from "../model/FoodItem";
import axios from "axios";
import FoodItemCard from "../component/FoodItemCard";

export default function FoodItemGallery() {
    const [foodItems, setFoodItems] = useState<FoodItem[]>([]);

    useEffect(() => {
        axios.get("/api/food")
            .then(response => response.data)
            .then(setFoodItems)
            .catch(console.error);
    }, [])

    return (
        <main>
            <h1>All Items</h1>
            {foodItems.map(e => <FoodItemCard  foodItem={e}/>)}
        </main>
    );
}