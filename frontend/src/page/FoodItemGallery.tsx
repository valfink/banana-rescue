import {useContext, useEffect, useState} from "react";
import FoodItemCard from "../component/FoodItemCard";
import "./FoodItemGallery.css";
import {SetAppIsLoadingContext} from "../context/SetAppIsLoadingContext";
import {fetchAllFoodItems} from "../util/foodItemRequests";
import {FoodItem} from "../model/FoodItem";

export default function FoodItemGallery() {
    const setAppIsLoading = useContext(SetAppIsLoadingContext);
    const [foodItems, setFoodItems] = useState<FoodItem[]>([]);

    useEffect(() => {
        fetchAllFoodItems(setAppIsLoading)
            .then(setFoodItems)
            .catch(console.error);
    }, [setAppIsLoading]);

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