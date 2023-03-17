import {FoodItem} from "../model/FoodItem";
import moment from "moment";
import "./FoodItemCard.css";

type FoodItemCardProps = {
    foodItem: FoodItem
}

export default function FoodItemCard(props: FoodItemCardProps) {

    return (
        <article className={"food-item-card"}>
            <main>
                <h2>{props.foodItem.title}</h2>
                <ul>
                    <li><strong>Pickup until:</strong> {moment(props.foodItem.pickup_until).calendar()}</li>
                    <li><strong>Consume within:</strong> {moment(props.foodItem.consume_until).fromNow(true)}</li>
                    <li><strong>Location:</strong> {props.foodItem.location}</li>
                </ul>
            </main>
            <aside style={{backgroundImage: `url(${props.foodItem.photo_uri})`}}>
            </aside>
        </article>
    );
}