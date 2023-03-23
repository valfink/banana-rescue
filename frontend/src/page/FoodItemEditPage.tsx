import FoodItemForm from "../component/FoodItemForm";
import {useParams} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {SetAppIsLoadingContext} from "../context/SetAppIsLoadingContext";
import {fetchSingleFoodItem} from "../util/foodItemRequests";
import {UserContext} from "../context/UserContext";
import {FoodItem} from "../model/FoodItem";

export default function FoodItemEditPage() {
    const {id} = useParams();
    const {user} = useContext(UserContext);
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

    if (user && foodItem.donator.id !== user.id) {
        return (
            <main>
                <h1>Sorry, you may only edit your own items 🤔</h1>
            </main>
        );
    }

    return (
        <main className={"form"}>
            <h1>Edit your Item</h1>
            <FoodItemForm action={"edit"} oldData={foodItem}/>
        </main>
    );
}