import {useContext, useEffect} from "react";
import FoodItemCard from "../component/FoodItemCard";
import "./FoodItemGallery.css";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {UserContext, UserContextType} from "../context/UserContext";
import useFoodItems from "../hook/useFoodItems";

type FoodItemGalleryProps = {
    showOnlyMyItems?: boolean
}

export default function FoodItemGallery(props: FoodItemGalleryProps) {
    const {appIsLoading, setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const {foodItems} = useFoodItems(setAppIsLoading, props.showOnlyMyItems);
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;

    useEffect(() => {
        props.showOnlyMyItems && redirectIfNotSignedIn();
    }, [props.showOnlyMyItems, redirectIfNotSignedIn]);

    if (appIsLoading) {
        return <></>;
    }

    return (
        <main className={"item-gallery"}>
            <h1>{props.showOnlyMyItems ? "My Items" : "All Items"}</h1>
            <section>
                {foodItems.map(e => <FoodItemCard key={e.id} foodItem={e}/>)}
                {foodItems.length === 0 &&
                    <article className={"item-card"}><h2>No items to display 😢</h2></article>
                }
            </section>
        </main>
    );
}