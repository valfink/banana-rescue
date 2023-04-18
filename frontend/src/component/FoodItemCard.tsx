import {FoodItem} from "../model/FoodItem";
import moment from "moment";
import "./FoodItemCard.css";
import {Link, useNavigate} from "react-router-dom";
import {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";

type FoodItemCardProps = {
    foodItem: FoodItem;
    compactView?: boolean;
}

export default function FoodItemCard(props: FoodItemCardProps) {
    const {user} = useContext(UserContext) as UserContextType;
    const navigate = useNavigate();
    const momentPickupUntil = moment(props.foodItem.pickupUntil);
    const momentConsumeUntil = moment(props.foodItem.consumeUntil);

    const foodItemActionButtons = (
        <>
            <Link to={`/food/${props.foodItem.id}`} className={"secondary-button"} state={{showBackLink: true}}
                  onClick={event => event.stopPropagation()}>Find out more</Link>
            {user?.id === props.foodItem.donator.id &&
                <Link to={`/food/${props.foodItem.id}/edit`} className={"primary-button"}
                      state={{showBackLink: true}} onClick={event => event.stopPropagation()}>Edit</Link>}
        </>
    );

    return (
        <article className={"item-card"}
                 onClick={() => navigate(`/food/${props.foodItem.id}`, {state: {showBackLink: true}})}>
            <section>
                <h2>{props.foodItem.title}</h2>
                <ul>
                    <li className={momentPickupUntil.isBefore() ? "too-late" : ""}><strong>Pickup
                        until:</strong> {momentPickupUntil.calendar()}</li>
                    <li className={momentConsumeUntil.isBefore() ? "too-late" : ""}><strong>Consume
                        within:</strong> {momentConsumeUntil.fromNow(!momentConsumeUntil.isBefore())}</li>
                    <li><strong>Location:</strong> {props.foodItem.location.title}</li>
                </ul>
                {!props.compactView && foodItemActionButtons}
            </section>
            <aside style={{backgroundImage: `url(${props.foodItem.photoUri || "/surprise-food.jpg"})`}}/>
        </article>
    );
}