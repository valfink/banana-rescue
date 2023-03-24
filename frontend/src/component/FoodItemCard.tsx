import {FoodItem} from "../model/FoodItem";
import moment from "moment";
import "./FoodItemCard.css";
import {Link} from "react-router-dom";
import {useContext} from "react";
import {UserContext} from "../context/UserContext";

type FoodItemCardProps = {
    foodItem: FoodItem
}

export default function FoodItemCard(props: FoodItemCardProps) {
    const {user} = useContext(UserContext);

    return (
        <article className={"food-item-card"}>
            <main>
                <h2>{props.foodItem.title}</h2>
                <ul>
                    <li><strong>Pickup until:</strong> {moment(props.foodItem.pickupUntil).calendar()}</li>
                    <li><strong>Consume within:</strong> {moment(props.foodItem.consumeUntil).fromNow(true)}</li>
                    <li><strong>Location:</strong> {props.foodItem.location}</li>
                </ul>
                <Link to={`/food/${props.foodItem.id}`} className={"secondary-button"}>Find out more</Link>
                {user?.id === props.foodItem.donator.id &&
                    <Link to={`/food/${props.foodItem.id}/edit`} className={"primary-button"}>Edit</Link>}
            </main>
            {props.foodItem.photoUri && <aside style={{backgroundImage: `url(${props.foodItem.photoUri})`}}/>}
        </article>
    );
}