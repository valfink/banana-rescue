import React, {useEffect, useState} from "react";
import {FoodItem} from "../model/FoodItem";
import axios from "axios";
import toast from "react-hot-toast";

export default function useFoodItems(setAppIsLoading: React.Dispatch<React.SetStateAction<number>>, fetchOnlyMyItems = false) {
    const [foodItems, setFoodItems] = useState<FoodItem[]>([]);
    const API_URL = "/api/food";

    useEffect(() => {
        setAppIsLoading(oldValue => oldValue + 1);
        axios.get(API_URL + (fetchOnlyMyItems ? "/my-items" : ""))
            .then(res => res.data as FoodItem[])
            .then(setFoodItems)
            .catch(err => handleRequestError("Could not fetch food items", err))
            .finally(() => setAppIsLoading(oldValue => Math.max(0, oldValue - 1)));
    }, [fetchOnlyMyItems, setAppIsLoading]);

    function handleRequestError(userMessage: string, err: any) {
        console.error(err);
        const errorMsg = err.response?.data.error || err.response?.data.message || err.message;
        toast.error(`${userMessage} 😱\n${errorMsg}`);
        return Promise.reject(errorMsg);
    }

    return {foodItems};
}
