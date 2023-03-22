import FoodItemForm from "../component/FoodItemForm";
import {useParams} from "react-router-dom";
import {useContext} from "react";
import {SetAppIsLoadingContext} from "../context/SetAppIsLoadingContext";
import {useFetchSingleFoodItem} from "../hook/useFoodItems";
import {UserContext} from "../context/UserContext";

export default function FoodItemEditPage() {
    const {id} = useParams();
    const setAppIsLoading = useContext(SetAppIsLoadingContext);
    const foodItem = useFetchSingleFoodItem(id, setAppIsLoading);
    const {user} = useContext(UserContext);

    if (!foodItem) {
        return (
            <main>
                <h1>Sorry, this food item doesn't seem to exist 😢</h1>
            </main>
        );
    }

    if (foodItem.donator.id !== user?.id) {
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