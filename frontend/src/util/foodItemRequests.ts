import axios from "axios";
import {FoodItem} from "../model/FoodItem";
import moment from "moment/moment";
import {FoodItemFormData} from "../model/FoodItemFormData";

const API_URL = "/api/food";

export async function fetchAllFoodItems(setAppIsLoading: React.Dispatch<React.SetStateAction<boolean>>) {
    let foodItems: FoodItem[] = [];

    setAppIsLoading(true);
    try {
        const response = await axios.get(API_URL);
        foodItems = response.data;
    } catch (err: any) {
        console.error(err);
        return Promise.reject(err.response.data.error || err.response.data.message);
    } finally {
        setAppIsLoading(false);
    }

    return foodItems;
}

export async function fetchSingleFoodItem(id: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<boolean>>) {
    let foodItem: FoodItem | undefined;

    setAppIsLoading(true);
    try {
        const res = await axios.get(`${API_URL}/${id}`);
        foodItem = res.data;
    } catch (err: any) {
        console.error(err);
        return Promise.reject(err.response.data.error || err.response.data.message);
    } finally {
        setAppIsLoading(false);
    }

    return foodItem;
}

export async function postNewFoodItem(formData: FoodItemFormData, photo: File | null, setAppIsLoading: React.Dispatch<React.SetStateAction<boolean>>) {
    let savedFoodItem: FoodItem;

    setAppIsLoading(true);
    let payload = new FormData();
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

    try {
        const res = await axios.post(API_URL, payload);
        savedFoodItem = res.data;
    } catch (err: any) {
        console.error(err);
        return Promise.reject(err.response.data.error || err.response.data.message);
    } finally {
        setAppIsLoading(false);
    }

    return savedFoodItem;
}
