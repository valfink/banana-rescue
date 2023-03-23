import {User} from "./User";

export type FoodItem = {
    id?: string;
    title: string;
    photoUri: string;
    location: string;
    pickupUntil: string;
    consumeUntil: string;
    description: string;
    donator: User;
}