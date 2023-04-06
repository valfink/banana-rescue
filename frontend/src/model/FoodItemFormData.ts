import {Location} from "./Location";

export type FoodItemFormData = {
    title: string;
    location?: Location;
    locationTitle?: string;
    pickupUntil: string;
    consumeUntil: string;
    description: string;
}
