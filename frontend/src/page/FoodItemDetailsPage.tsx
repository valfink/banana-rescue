import {useParams} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {SetAppIsLoadingContext} from "../context/SetAppIsLoadingContext";
import "./FoodItemDetailsPage.css";
import moment from "moment/moment";
import {fetchSingleFoodItem} from "../util/foodItemRequests";
import {FoodItem} from "../model/FoodItem";

export default function FoodItemDetailsPage() {
    const {id} = useParams();
    const setAppIsLoading = useContext(SetAppIsLoadingContext);
    const [foodItem, setFoodItem] = useState<FoodItem | undefined>(undefined);

    useEffect(() => {
        fetchSingleFoodItem(id, setAppIsLoading)
            .then(setFoodItem)
            .catch(console.error);
    }, [id, setAppIsLoading]);

    if (!foodItem) {
        return (
            <main>
                <h1>Sorry, this food item doesn't seem to exist 😢</h1>
            </main>
        );
    }

    return (
        <section className={"food-item-details"}>
            {foodItem.photoUri && <header style={{backgroundImage: `url(${foodItem.photoUri})`}}/>}
            <main>
                <h1>{foodItem.title}</h1>
                <ul>
                    <li><strong>Pickup until:</strong> {moment(foodItem.pickupUntil).calendar()}</li>
                    <li><strong>Consume within:</strong> {moment(foodItem.consumeUntil).fromNow(true)}</li>
                    <li><strong>Location:</strong> {foodItem.location}</li>
                    <li><strong>Donator:</strong> {foodItem.donator.username}</li>
                    <li><strong>Comment:</strong> {foodItem.description}</li>
                </ul>
            </main>
        </section>
    );
}
