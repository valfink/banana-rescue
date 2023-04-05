import {FoodItem} from "./FoodItem";
import {User} from "./User";
import {ChatMessage} from "./ChatMessage";

export type Chat = {
    id: string,
    foodItem: FoodItem,
    candidate: User,
    messages: ChatMessage[],
    hasUnreadMessages?: boolean
}