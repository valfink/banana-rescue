import {Link, useLocation, useNavigate, useParams} from "react-router-dom";
import React, {useContext, useEffect, useState} from "react";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import "./FoodItemDetailsPage.css";
import moment from "moment/moment";
import {deleteFoodItem, fetchSingleFoodItem} from "../util/foodItemRequests";
import {FoodItem} from "../model/FoodItem";
import {UserContext, UserContextType} from "../context/UserContext";
import DeletionWarningScreen from "../modal/DeletionWarningScreen";
import useChat from "../hook/useChat";

export default function FoodItemDetailsPage() {
    const {id} = useParams();
    const {appIsLoading, setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const [foodItem, setFoodItem] = useState<FoodItem | undefined>(undefined);
    const {user} = useContext(UserContext) as UserContextType;
    const {pathname, state} = useLocation();
    const [showDeleteFoodItemModal, setShowDeleteFoodItemModal] = useState(false);
    const navigate = useNavigate();
    const {startNewChat} = useChat(undefined, setAppIsLoading);

    useEffect(() => {
        fetchSingleFoodItem(id, setAppIsLoading)
            .then(setFoodItem)
            .catch(console.error);
    }, [id, setAppIsLoading]);

    function handleDeleteButtonClick() {
        setShowDeleteFoodItemModal(true);
    }

    function handleCloseModalClick() {
        setShowDeleteFoodItemModal(false);
    }

    function handleDeleteItemClick() {
        deleteFoodItem(foodItem?.id || "", setAppIsLoading)
            .then(() => {
                navigate("/");
            })
            .finally(() => {
                setShowDeleteFoodItemModal(false)
            });
    }

    function handleWriteAMessageClick() {
        if (foodItem?.id) {
            startNewChat(foodItem.id)
                .then(chatId => {
                    navigate(`/chats/${chatId}`, {state: {navBarBackLink: pathname, oldState: state}});
                })
        }
    }

    if (!foodItem) {
        if (appIsLoading === 0) {
            return (
                <main>
                    <h1>Sorry, this food item doesn't seem to exist 😢</h1>
                </main>
            );
        } else {
            return <></>;
        }
    }

    return (
        <section className={"food-item-details"}>
            <header style={{backgroundImage: `url(${foodItem.photoUri || "/surprise-food.jpg"})`}}/>
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
                {user?.id === foodItem.donator.id
                    ? <>
                        <Link to={`/food/${foodItem.id}/edit`} className={"primary-button"}
                              state={{navBarBackLink: pathname, oldState: state}}>Edit Item</Link>
                        <button className={"danger-button"} onClick={handleDeleteButtonClick}>Delete Item</button>
                        <DeletionWarningScreen closeModal={handleCloseModalClick} deleteItem={handleDeleteItemClick}
                                               modalIsOpen={showDeleteFoodItemModal} itemDescriptor={"food item"}
                                               itemName={foodItem.title}/>
                    </>
                    : <>
                        <button onClick={handleWriteAMessageClick} disabled={!user}>Write a message</button>
                    </>}
            </section>
        </section>
    );
}
