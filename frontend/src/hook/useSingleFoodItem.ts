import axios from "axios";
import {FoodItem} from "../model/FoodItem";
import moment from "moment/moment";
import {FoodItemFormData} from "../model/FoodItemFormData";
import React, {useEffect, useState} from "react";
import toast from "react-hot-toast";

export default function useSingleFoodItem(id: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [foodItem, setFoodItem] = useState<FoodItem>();
    const API_URL = "/api/food";

    useEffect(() => {
        setAppIsLoading(oldValue => oldValue + 1);
        axios.get(`${API_URL}/${id}`)
            .then(res => res.data as FoodItem)
            .then(setFoodItem)
            .catch(err => handleRequestError("Could not fetch food item", err))
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }, [id, setAppIsLoading]);

    async function postNewFoodItem(formData: FoodItemFormData, photo: File | null) {
        setAppIsLoading(oldValue => oldValue + 1);
        const payload = createFormDataPayload(formData, photo);
        try {
            const res = await axios.post(API_URL, payload);
            toast.success("Food item successfully added 🤗");
            return res.data as FoodItem;
        } catch (err: any) {
            return handleRequestError("Could not add food item", err);
        } finally {
            setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
        }
    }

    async function updateFoodItem(formData: FoodItemFormData, photo: File | null) {
        setAppIsLoading(oldValue => oldValue + 1);
        const payload = createFormDataPayload(formData, photo);
        try {
            const res = await axios.put(`${API_URL}/${id}`, payload);
            toast.success("Food item successfully updated 🤗");
            return res.data as FoodItem;
        } catch (err: any) {
            return handleRequestError("Could not update food item", err);
        } finally {
            setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
        }
    }

    async function deletePhotoFromFoodItem() {
        setAppIsLoading(oldValue => oldValue + 1);
        try {
            await axios.delete(`${API_URL}/${id}/photo`);
            toast.success("Photo successfully deleted 🤗");
            return true;
        } catch (err: any) {
            return handleRequestError("Could not delete photo from food item", err);
        } finally {
            setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
        }
    }

    async function deleteFoodItem() {
        setAppIsLoading(oldValue => oldValue + 1);
        try {
            await axios.delete(`${API_URL}/${id}`);
            toast.success("Food item successfully deleted 🤗");
            return true;
        } catch (err: any) {
            return handleRequestError("Could not delete food item", err);
        } finally {
            setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
        }
    }

    return {foodItem, postNewFoodItem, updateFoodItem, deletePhotoFromFoodItem, deleteFoodItem};

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
}
