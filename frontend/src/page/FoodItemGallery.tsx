import {useContext, useEffect, useState} from "react";
import FoodItemCard from "../component/FoodItemCard";
import "./FoodItemGallery.css";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {fetchFoodItems} from "../util/foodItemRequests";
import {FoodItem} from "../model/FoodItem";

type FoodItemGalleryProps = {
    showOnlyMyItems?: boolean
}

export default function FoodItemGallery(props: FoodItemGalleryProps) {
    const {appIsLoading, setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const [foodItems, setFoodItems] = useState<FoodItem[]>([]);

    useEffect(() => {
        fetchFoodItems(setAppIsLoading, props.showOnlyMyItems)
            .then(setFoodItems)
            .catch(console.error);
    }, [props.showOnlyMyItems, setAppIsLoading]);

    if (appIsLoading) {
        return <></>;
    }

    return (
        <main className={"item-gallery"}>
            <h1>All Items</h1>
            <section>
                {foodItems.map(e => <FoodItemCard key={e.id} foodItem={e}/>)}
                {foodItems.length === 0 &&
                    <article className={"item-card"}><h2>No items to display 😢</h2></article>
                }
            </section>
        </main>
    );
}