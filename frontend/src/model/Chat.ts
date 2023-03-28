import {FoodItem} from "./FoodItem";
import {User} from "./User";

export type Chat = {
    id: string,
    foodItem: FoodItem,
    candidate: User
}