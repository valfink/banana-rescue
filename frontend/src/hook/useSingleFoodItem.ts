import axios from "axios";
import {FoodItem} from "../model/FoodItem";
import moment from "moment/moment";
import {FoodItemFormData} from "../model/FoodItemFormData";
import React, {useEffect, useState} from "react";
import useGenerateToast from "./useGenerateToast";

export default function useSingleFoodItem(id: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [foodItem, setFoodItem] = useState<FoodItem>();
    const {errorToast, successToast} = useGenerateToast();
    const API_URL = "/api/food";

    useEffect(() => {
        if (id) {
            setAppIsLoading(oldValue => oldValue + 1);
            axios.get(`${API_URL}/${id}`)
                .then(res => res.data as FoodItem)
                .then(setFoodItem)
                .catch(err => errorToast("Could not fetch food item", err))
                .finally(() => {
                    setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
                });
        }
    }, [errorToast, id, setAppIsLoading]);

    async function postNewFoodItem(formData: FoodItemFormData, photo: File | null) {
        setAppIsLoading(oldValue => oldValue + 1);
        const payload = createFormDataPayload(formData, photo);
        try {
            const res = await axios.post(API_URL, payload);
            successToast("Food item successfully added");
            return res.data as FoodItem;
        } catch (err: any) {
            errorToast("Could not add food item", err);
            return Promise.reject(err);
        } finally {
            setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
        }
    }

    async function updateFoodItem(formData: FoodItemFormData, photo: File | null) {
        setAppIsLoading(oldValue => oldValue + 1);
        const payload = createFormDataPayload(formData, photo);
        try {
            const res = await axios.put(`${API_URL}/${id}`, payload);
            successToast("Food item successfully updated");
            return res.data as FoodItem;
        } catch (err: any) {
            errorToast("Could not update food item", err);
            return Promise.reject(err);
        } finally {
            setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
        }
    }

    async function deletePhotoFromFoodItem() {
        setAppIsLoading(oldValue => oldValue + 1);
        try {
            await axios.delete(`${API_URL}/${id}/photo`);
            successToast("Photo successfully deleted");
            return true;
        } catch (err: any) {
            errorToast("Could not delete photo from food item", err);
            return Promise.reject(err);
        } finally {
            setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
        }
    }

    async function deleteFoodItem() {
        setAppIsLoading(oldValue => oldValue + 1);
        try {
            await axios.delete(`${API_URL}/${id}`);
            successToast("Food item successfully deleted");
            return true;
        } catch (err: any) {
            errorToast("Could not delete food item", err);
            return Promise.reject(err);
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
}
