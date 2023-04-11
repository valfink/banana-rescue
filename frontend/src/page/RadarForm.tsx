import React, {ChangeEvent, FormEvent, useContext, useEffect, useState} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Link, useLocation} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCrosshairs} from "@fortawesome/free-solid-svg-icons";
import BananaMap from "../component/BananaMap";
import {Location} from "../model/Location";
import useCoordinate from "../hook/useCoordinate";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import "./RadarForm.css";
import axios from "axios";
import {Radar} from "../model/Radar";
import toast from "react-hot-toast";

export default function RadarForm() {
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const location = useLocation();
    const [radarCenterSearchText, setRadarCenterSearchText] = useState("");
    const [radius, setRadius] = useState(1000);
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const {searchForCoordinates, foundCoordinate, coordinateError} = useCoordinate(setAppIsLoading);
    const radarLocation: Location = {title: "My Radar", coordinate: foundCoordinate};

    function handleSearchTextInputChange(e: ChangeEvent<HTMLInputElement>) {
        setRadarCenterSearchText(e.target.value);
    }

    function handleSearchTextSubmit(e: FormEvent) {
        e.preventDefault();
        searchForCoordinates(radarCenterSearchText);
    }

    function handleRadiusChange(e: ChangeEvent<HTMLInputElement>) {
        setRadius(parseInt(e.target.value));
    }

    function handleAddRadarClick() {
        const radar: Radar = {center: radarLocation.coordinate, radiusInMeters: radius};
        axios.post("/api/my-radar", radar)
            .then(() => {
                toast.success("Radar successfully added 🤗");
            })
            .catch(err => {
                console.error(err);
                toast.error(`Could not post radar 😱\n${err.response?.data.error || err.response?.data.message || err.message}`);
            })
    }

    useEffect(() => {
        redirectIfNotSignedIn();
    }, [redirectIfNotSignedIn]);

    return (
        <main className={"form radar-form"}>
            <h1>Add your Radar</h1>
            <form onSubmit={handleSearchTextSubmit}>
                <section>
                    <div className={"input-with-icon"}>
                        <FontAwesomeIcon icon={faCrosshairs}/>
                        <input type={"text"} placeholder={"Radar Location"} value={radarCenterSearchText}
                               onChange={handleSearchTextInputChange} required/>
                    </div>
                    <button type={"submit"} className={"secondary-button"}>Search</button>
                    <input type={"range"} min={500} max={5000} step={100} value={radius} onChange={handleRadiusChange}/>
                    {!coordinateError && <BananaMap location={radarLocation} radius={radius}/>}
                    {coordinateError &&
                        <>Sorry, but the location you entered could not be found. Please try a different location.</>}
                </section>
                <button type={"button"} onClick={handleAddRadarClick}
                        disabled={foundCoordinate.latitude === 0 && foundCoordinate.longitude === 0}>Add Radar
                </button>
                <Link to={location.state?.navBarBackLink || "/"} state={location.state?.oldState}
                      className={"secondary-button"}>Cancel</Link>
            </form>
        </main>
    );
}
