import {useEffect, useState} from "react";
import {FoodItem} from "../model/FoodItem";
import axios from "axios";
import FoodItemCard from "../component/FoodItemCard";
import "./FoodItemGallery.css";

export default function FoodItemGallery() {
    const [foodItems, setFoodItems] = useState<FoodItem[]>([]);

    useEffect(() => {
        axios.get("/api/food")
            .then(response => response.data)
            .then(setFoodItems)
            .catch(console.error);
    }, [])

    return (
        <main className={"food-item-gallery"}>
            <h1>All Items</h1>
            <section>
                {foodItems.map(e => <FoodItemCard key={e.id} foodItem={e}/>)}
                {foodItems.length === 0 &&
                    <article className={"food-item-card"}><h2>No items to display 😢</h2></article>
                }
            </section>
        </main>
    );
}