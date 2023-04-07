import axios from "axios";
import {FoodItem} from "../model/FoodItem";
import moment from "moment/moment";
import {FoodItemFormData} from "../model/FoodItemFormData";
import React from "react";
import toast from "react-hot-toast";

const API_URL = "/api/food";

export async function fetchFoodItems(setAppIsLoading: React.Dispatch<React.SetStateAction<number>>, fetchOnlyMyItems = false) {
    let foodItems: FoodItem[] = [];

    setAppIsLoading(oldValue => oldValue + 1);
    try {
        const response = await axios.get(API_URL + (fetchOnlyMyItems ? "/my-items" : ""));
        foodItems = response.data as FoodItem[];
    } catch (err: any) {
        return handleRequestError("Could not fetch food items", err);
    } finally {
        setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
    }

    return foodItems;
}

export async function fetchSingleFoodItem(id: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    let foodItem: FoodItem | undefined;

    setAppIsLoading(oldValue => oldValue + 1);
    try {
        const res = await axios.get(`${API_URL}/${id}`);
        foodItem = res.data as FoodItem;
    } catch (err: any) {
        return handleRequestError("Could not fetch food item", err);
    } finally {
        setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
    }

    return foodItem;
}

export async function postNewFoodItem(formData: FoodItemFormData, photo: File | null, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    let savedFoodItem: FoodItem;

    setAppIsLoading(oldValue => oldValue + 1);
    const payload = createFormDataPayload(formData, photo);
    try {
        const res = await axios.post(API_URL, payload);
        savedFoodItem = res.data;
        toast.success("Food item successfully added 🤗");
    } catch (err: any) {
        return handleRequestError("Could not add food item", err);
    } finally {
        setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
    }

    return savedFoodItem;
}

export async function updateFoodItem(id: string, formData: FoodItemFormData, photo: File | null, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    let updatedFoodItem: FoodItem;

    setAppIsLoading(oldValue => oldValue + 1);
    const payload = createFormDataPayload(formData, photo);
    try {
        const res = await axios.put(`${API_URL}/${id}`, payload);
        updatedFoodItem = res.data;
        toast.success("Food item successfully updated 🤗");
    } catch (err: any) {
        return handleRequestError("Could not update food item", err);
    } finally {
        setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
    }

    return updatedFoodItem;
}

export async function deletePhotoFromFoodItem(id: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    setAppIsLoading(oldValue => oldValue + 1);
    try {
        await axios.delete(`${API_URL}/${id}/photo`);
        toast.success("Photo successfully deleted 🤗");
    } catch (err: any) {
        return handleRequestError("Could not delete photo from food item", err);
    } finally {
        setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
    }

    return true;
}

export async function deleteFoodItem(id: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    setAppIsLoading(oldValue => oldValue + 1);
    try {
        await axios.delete(`${API_URL}/${id}`);
        toast.success("Food item successfully deleted 🤗");
    } catch (err: any) {
        return handleRequestError("Could not delete food item", err);
    } finally {
        setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
    }

    return true;
}

function createFormDataPayload(formData: FoodItemFormData, photo: File | null) {
    const payload = new FormData();
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

    return payload;
}

function handleRequestError(userMessage: string, err: any) {
    console.error(err);
    const errorMsg = err.response?.data.error || err.response?.data.message || err.message;
    toast.error(`${userMessage} 😱\n${errorMsg}`);
    return Promise.reject(errorMsg);
}
