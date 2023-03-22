import {FoodItem} from "../model/FoodItem";
import moment from "moment";
import "./FoodItemCard.css";
import {Link} from "react-router-dom";

type FoodItemCardProps = {
    foodItem: FoodItem
}

export default function FoodItemCard(props: FoodItemCardProps) {

    return (
        <article className={"food-item-card"}>
            <main>
                <h2>{props.foodItem.title}</h2>
                <ul>
                    <li><strong>Pickup until:</strong> {moment(props.foodItem.pickupUntil).calendar()}</li>
                    <li><strong>Consume within:</strong> {moment(props.foodItem.consumeUntil).fromNow(true)}</li>
                    <li><strong>Location:</strong> {props.foodItem.location}</li>
                </ul>
                <Link to={`/food/${props.foodItem.id}`}>Find out more</Link>
            </main>
            {props.foodItem.photoUri && <aside style={{backgroundImage: `url(${props.foodItem.photoUri})`}}/>}
        </article>
    );
}