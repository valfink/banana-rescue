import {FoodItem} from "../model/FoodItem";
import moment from "moment";
import "./FoodItemCard.css";
import {Link} from "react-router-dom";
import {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";

type FoodItemCardProps = {
    foodItem: FoodItem;
    compactView?: boolean;
}

export default function FoodItemCard(props: FoodItemCardProps) {
    const {user} = useContext(UserContext) as UserContextType;

    const foodItemActionButtons = (
        <>
            <Link to={`/food/${props.foodItem.id}`} className={"secondary-button"}
                  state={{navBarBackLink: "/food"}}>Find out more</Link>
            {user?.id === props.foodItem.donator.id &&
                <Link to={`/food/${props.foodItem.id}/edit`} className={"primary-button"}
                      state={{navBarBackLink: "/food"}}>Edit</Link>}
        </>
    );

    return (
        <article className={"food-item-card"}>
            <section>
                <h2>{props.foodItem.title}</h2>
                <ul>
                    <li><strong>Pickup until:</strong> {moment(props.foodItem.pickupUntil).calendar()}</li>
                    <li><strong>Consume within:</strong> {moment(props.foodItem.consumeUntil).fromNow(true)}</li>
                    <li><strong>Location:</strong> {props.foodItem.location}</li>
                </ul>
                {!props.compactView && foodItemActionButtons}
            </section>
            {props.foodItem.photoUri && <aside style={{backgroundImage: `url(${props.foodItem.photoUri})`}}/>}
        </article>
    );
}