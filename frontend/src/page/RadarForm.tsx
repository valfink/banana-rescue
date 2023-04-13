import React, {ChangeEvent, FormEvent, useContext, useEffect, useState} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Link, useLocation} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCrosshairs, faLocationDot, faMagnifyingGlass} from "@fortawesome/free-solid-svg-icons";
import BananaMap from "../component/BananaMap";
import {Location} from "../model/Location";
import useCoordinate from "../hook/useCoordinate";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import "./RadarForm.css";
import axios from "axios";
import {Radar} from "../model/Radar";
import toast from "react-hot-toast";
import {OpenStreetMapsSearchResult} from "../model/OpenStreetMapsSearchResult";

type RadarFormProps = {
    postRadar: (radar: Radar) => void;
}

export default function RadarForm(props: RadarFormProps) {
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const location = useLocation();
    const [radarCenterSearchText, setRadarCenterSearchText] = useState("");
    const [radius, setRadius] = useState(1000);
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const {searchForCoordinates, foundCoordinate, setFoundCoordinate, coordinateError} = useCoordinate(setAppIsLoading);
    const radarLocation: Location = {title: "My Radar", coordinate: foundCoordinate};

    function handleSearchTextInputChange(e: ChangeEvent<HTMLInputElement>) {
        setRadarCenterSearchText(e.target.value);
    }

    function handleSearchTextSubmit(e: FormEvent) {
        e.preventDefault();
        searchForCoordinates(radarCenterSearchText);
    }

    function handleUseCurrentPositionClick() {
        setRadarCenterSearchText("");
        if ("geolocation" in navigator) {
            setAppIsLoading(oldValue => oldValue + 1);
            navigator.geolocation.getCurrentPosition(position => {
                    setFoundCoordinate({latitude: position.coords.latitude, longitude: position.coords.longitude});
                    axios.get(`https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=${[position.coords.latitude, position.coords.longitude]}`)
                        .then(res => res.data as OpenStreetMapsSearchResult[])
                        .then((results) => {
                            if (results.length > 0) {
                                setRadarCenterSearchText(results[0].display_name);
                            }
                        });
                    setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
                },
                err => {
                    toast.error(`Could not get current position from Browser 😱\n${err.message}`);
                    setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
                });
        } else {
            toast.error("Can't get current position from Browser 😱");
        }
    }

    function handleRadiusChange(e: ChangeEvent<HTMLInputElement>) {
        setRadius(parseInt(e.target.value));
    }

    function handleAddRadarClick() {
        const radar: Radar = {center: radarLocation.coordinate, radiusInMeters: radius};
        props.postRadar(radar);
    }

    useEffect(redirectIfNotSignedIn, [redirectIfNotSignedIn]);

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
                    <div className={"buttons-inline"}>
                        <button type={"submit"} className={"secondary-button"}>
                            <FontAwesomeIcon icon={faMagnifyingGlass} aria-label={"Search"}/>
                        </button>
                        <button type={"button"} className={"secondary-button"} onClick={handleUseCurrentPositionClick}>
                            <FontAwesomeIcon icon={faLocationDot} aria-label={"Use current position"}/>
                        </button>
                    </div>
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
