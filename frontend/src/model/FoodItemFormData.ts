import {Location} from "./Location";

export type FoodItemFormData = {
    title: string;
    location: Location;
    pickupUntil: string;
    consumeUntil: string;
    description: string;
}
