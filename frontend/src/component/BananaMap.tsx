import {Circle, MapContainer, Marker, Popup, TileLayer, useMap} from "react-leaflet";
import React, {useEffect} from "react";
import {Location} from "../model/Location";
import "./BananaMap.css";
import {FoodItem} from "../model/FoodItem";
import {DivIcon} from "leaflet";
import {useNavigate} from "react-router-dom";

type BananaMapProps = {
    location: Location;
    radius?: number;
    itemsToDisplay?: FoodItem[];
}

function BananaMapController(props: BananaMapProps) {
    const map = useMap();

    useEffect(() => {
        map.setView([props.location.coordinate.latitude || 52.5170365,
            props.location.coordinate.longitude || 13.3888599]);
    }, [map, props.location.coordinate.latitude, props.location.coordinate.longitude]);

    useEffect(() => {
        if (props.radius) {
            map.setZoom(23 - Math.floor(Math.log2(props.radius)));
        }
    }, [map, props.radius]);

    return <></>;
}

export default function BananaMap(props: BananaMapProps) {
    const navigate = useNavigate();

    return (
        <MapContainer center={[props.location.coordinate.latitude, props.location.coordinate.longitude]} zoom={15}
                      scrollWheelZoom={false}>
            <BananaMapController location={props.location} radius={props.radius}/>
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <Marker position={[props.location.coordinate.latitude, props.location.coordinate.longitude]}>
                <Popup>
                    <strong>{props.location.title}</strong>
                </Popup>
            </Marker>
            {props.radius &&
                <>
                    <Circle center={[props.location.coordinate.latitude, props.location.coordinate.longitude]}
                            radius={props.radius} color={"var(--success-bg)"} fillColor={"var(--bright-bg)"}/>
                    <Circle fillColor={"url(#circleGradient)"} fillOpacity={1} className={"radar-line"}
                            center={[props.location.coordinate.latitude, props.location.coordinate.longitude]}
                            radius={props.radius} color={"transparent"}/>
                    <svg style={{position: "absolute", width: "0", height: "0"}}>
                        <defs>
                            <linearGradient id="circleGradient" mask={"url(#halfHeight)"}>
                                <stop offset="49.5%" stopColor="transparent"/>
                                <stop offset="49.5%" stopColor="var(--success-accent)"/>
                                <stop offset="50%" stopColor="var(--success-accent)" stopOpacity={1}/>
                                <stop offset="50.5%" stopColor="var(--success-accent)"/>
                                <stop offset="50.5%" stopColor="transparent"/>
                            </linearGradient>
                            <linearGradient id="halfHeight" x2={"0%"} y2={"100%"}>
                                <stop offset="49%" stopColor="white" stopOpacity={1}/>
                                <stop offset="51%" stopColor="white" stopOpacity={0}/>
                            </linearGradient>
                            <mask id="halfMask" maskContentUnits="objectBoundingBox">
                                <rect width="1" height="1" fill="url(#halfHeight)"/>
                            </mask>
                        </defs>
                    </svg>
                </>}
            {props.itemsToDisplay &&
                <>
                    {props.itemsToDisplay.map(item =>
                        <Marker icon={new DivIcon({className: 'banana-marker', iconSize: [41, 41]})} key={item.id}
                                position={[item.location.coordinate.latitude, item.location.coordinate.longitude]}>
                            <Popup><strong onClick={() =>
                                navigate(`/food/${item.id}`, {state: {showBackLink: true}})}>{item.title}</strong></Popup>
                        </Marker>)
                    }
                </>
            }
        </MapContainer>
    );
}
