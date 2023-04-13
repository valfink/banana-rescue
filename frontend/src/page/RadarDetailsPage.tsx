import {useContext, useEffect, useState} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Radar} from "../model/Radar";
import axios from "axios";
import RadarForm from "./RadarForm";
import BananaMap from "../component/BananaMap";
import "./RadarDetailsPage.css";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import FoodItemCard from "../component/FoodItemCard";

export default function RadarDetailsPage() {
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const [radar, setRadar] = useState<Radar | undefined>(undefined);
    const [radarHasBeenSet, setRadarHasBeenSet] = useState(true);
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;

    useEffect(() => {
        setAppIsLoading(oldValue => oldValue + 1);
        axios.get("/api/my-radar")
            .then(response => response.data as Radar)
            .then(setRadar)
            .catch(err => {
                if (err.response.status === 404) {
                    setRadarHasBeenSet(false);
                }
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }, [setAppIsLoading]);

    useEffect(redirectIfNotSignedIn, [redirectIfNotSignedIn]);

    if (!radarHasBeenSet) {
        return <RadarForm/>;
    }

    return (
        <main className={"radar-details"}>
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
