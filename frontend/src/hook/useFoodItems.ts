import React, {useEffect, useState} from "react";
import axios from "axios";
import {FoodItem} from "../model/FoodItem";
import moment from "moment/moment";
import {FoodItemFormData} from "../model/FoodItemFormData";

export function useFetchAllFoodItems(setAppIsLoading: React.Dispatch<React.SetStateAction<boolean>>) {
    const [foodItems, setFoodItems] = useState<FoodItem[]>([]);

    useEffect(() => {
        setAppIsLoading(true);
        axios.get("/api/food")
            .then(response => response.data)
            .then(setFoodItems)
            .catch(console.error)
            .finally(() => {
                setAppIsLoading(false);
            });
    }, [setAppIsLoading]);

    return foodItems;
}

export function useFetchSingleFoodItem(id: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<boolean>>) {
    const [foodItem, setFoodItem] = useState<FoodItem | undefined>(undefined);

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

    return foodItem;
}

export function postNewFoodItem(formData: FoodItemFormData, photo: File | null, setAppIsLoading: React.Dispatch<React.SetStateAction<boolean>>) {
    setAppIsLoading(true);
    let url = "/api/food",
        payload = new FormData();
    if (photo) {
        payload.set("photo", photo);
    }
    payload.set("form", new Blob([JSON.stringify({
        ...formData,
        pickupUntil: moment(formData.pickupUntil),
        consumeUntil: moment(formData.consumeUntil)
    })], {
        type: "application/json"
    }));
    return axios.post(url, payload)
        .catch(err => {
            console.error(err);
            return err.response.data.error || err.response.data.message;
        })
        .finally(() => {
            setAppIsLoading(false);
        });
}
