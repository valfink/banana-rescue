import {FoodItem} from "../model/FoodItem";

type FoodItemCardProps = {
    foodItem: FoodItem
}

export default function FoodItemCard(props: FoodItemCardProps) {
    return (
        <div>
            <h2>{props.foodItem.title}</h2>
            <ul>
                <li>Pickup until: {props.foodItem.pickup_until}</li>
                <li>Consume until: {props.foodItem.consume_until}</li>
                <li>Location: {props.foodItem.location}</li>
            </ul>
        </div>
    );
}