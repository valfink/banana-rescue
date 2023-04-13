import {useContext, useEffect} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import RadarForm from "./RadarForm";
import BananaMap from "../component/BananaMap";
import "./RadarDetailsPage.css";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import FoodItemCard from "../component/FoodItemCard";
import useRadar from "../hook/useRadar";

export default function RadarDetailsPage() {
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const {radar, radarHasBeenSet, postRadar} = useRadar(setAppIsLoading);

    useEffect(redirectIfNotSignedIn, [redirectIfNotSignedIn]);

    if (!radarHasBeenSet) {
        return <RadarForm postRadar={postRadar}/>;
    }

    return (
        <main className={"radar-details item-gallery"}>
            {radar &&
                <>
                    <header><BananaMap location={{title: "My Radar", coordinate: radar.center}}
                                       radius={radar.radiusInMeters} itemsToDisplay={radar.foodItems}/></header>
                    <h1>My Radar</h1>
                    <section>
                        {radar.foodItems?.map(e => <FoodItemCard key={e.id} foodItem={e}/>)}
                        {radar.foodItems?.length === 0 &&
                            <article className={"item-card"}><h2>No items to display 😢</h2></article>
                        }
                    </section>
                </>}
        </main>
    );
}
