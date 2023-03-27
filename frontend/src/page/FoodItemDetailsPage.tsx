import {Link, Navigate, useLocation, useParams} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import "./FoodItemDetailsPage.css";
import moment from "moment/moment";
import {fetchSingleFoodItem} from "../util/foodItemRequests";
import {FoodItem} from "../model/FoodItem";
import {UserContext, UserContextType} from "../context/UserContext";

export default function FoodItemDetailsPage() {
    const {id} = useParams();
    const {appIsLoading, setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const [foodItem, setFoodItem] = useState<FoodItem | undefined>(undefined);
    const {user} = useContext(UserContext) as UserContextType;
    const {pathname, state} = useLocation();

    useEffect(() => {
        fetchSingleFoodItem(id, setAppIsLoading)
            .then(setFoodItem)
            .catch(console.error);
    }, [id, setAppIsLoading]);

    if (!foodItem) {
        // TODO: DOESNT WORK
        if (appIsLoading === 0) {
            return <Navigate to={"/"}/>;
        } else {
            return <></>;
        }
        // return (
        //     <main>
        //         <h1>Sorry, this food item doesn't seem to exist 😢</h1>
        //     </main>
        // );
    }

    return (
        <section className={"food-item-details"}>
            {foodItem.photoUri && <header style={{backgroundImage: `url(${foodItem.photoUri})`}}/>}
            <section>
                <main>
                    <h1>{foodItem.title}</h1>
                    <ul>
                        <li><strong>Pickup until:</strong> {moment(foodItem.pickupUntil).calendar()}</li>
                        <li><strong>Consume within:</strong> {moment(foodItem.consumeUntil).fromNow(true)}</li>
                        <li><strong>Location:</strong> {foodItem.location}</li>
                        <li><strong>Donator:</strong> {foodItem.donator.username}</li>
                        <li><strong>Comment:</strong> {foodItem.description}</li>
                    </ul>
                </main>
                {user?.id === foodItem.donator.id &&
                    <Link to={`/food/${foodItem.id}/edit`} className={"primary-button"}
                          state={{navBarBackLink: pathname, oldState: state}}>Edit Item</Link>}
            </section>
        </section>
    );
}
