import {User} from "./User";
import {Location} from "./Location";

export type FoodItem = {
    id?: string;
    title: string;
    photoUri: string;
    location: Location;
    pickupUntil: string;
    consumeUntil: string;
    description: string;
    donator: User;
}
