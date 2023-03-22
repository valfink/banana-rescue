import {useParams} from "react-router-dom";
import {useContext} from "react";
import {SetAppIsLoadingContext} from "../context/SetAppIsLoadingContext";
import "./FoodItemDetailsPage.css";
import moment from "moment/moment";
import {useFetchSingleFoodItem} from "../hook/useFoodItems";

export default function FoodItemDetailsPage() {
    const {id} = useParams();
    const setAppIsLoading = useContext(SetAppIsLoadingContext);
    const foodItem = useFetchSingleFoodItem(id, setAppIsLoading);

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
