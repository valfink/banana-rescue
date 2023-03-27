import FoodItemForm from "../component/FoodItemForm";
import {useParams} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {fetchSingleFoodItem} from "../util/foodItemRequests";
import {UserContext, UserContextType} from "../context/UserContext";
import {FoodItem} from "../model/FoodItem";

export default function FoodItemEditPage() {
    const {id} = useParams();
    const {user} = useContext(UserContext) as UserContextType;
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
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
            <FoodItemForm action={"edit"} oldFoodItem={foodItem}/>
        </main>
    );
}
