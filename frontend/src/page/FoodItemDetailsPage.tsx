import {useParams} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {FoodItem} from "../model/FoodItem";
import axios from "axios";
import {SetAppIsLoadingContext} from "../context/SetAppIsLoadingContext";
import "./FoodItemDetailsPage.css";
import moment from "moment/moment";

export default function FoodItemDetailsPage() {
    const [foodItem, setFoodItem] = useState<FoodItem | undefined>(undefined);
    const {id} = useParams();
    const setAppIsLoading = useContext(SetAppIsLoadingContext);

    useEffect(() => {
        setAppIsLoading(true);
        axios.get(`/api/food/${id}`)
            .then(res => res.data)
            .then(setFoodItem)
            .catch(console.error)
            .finally(() => {
                setAppIsLoading(false);
            });
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