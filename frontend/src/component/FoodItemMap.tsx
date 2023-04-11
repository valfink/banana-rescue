import {MapContainer, Marker, Popup, TileLayer, useMap} from "react-leaflet";
import React, {useEffect} from "react";
import {Location} from "../model/Location";
import "./FoodItemMap.css";

type FoodItemMapProps = {
    location: Location;
}

function FoodItemMapController(props: FoodItemMapProps) {
    const map = useMap();
    useEffect(() => {
        map.setView([props.location.coordinate.latitude, props.location.coordinate.longitude]);
    }, [map, props.location.coordinate.latitude, props.location.coordinate.longitude]);

    return <></>;
}

export default function FoodItemMap(props: FoodItemMapProps) {
    if (props.location.coordinate.latitude === 0) {
        return <div className={"leaflet-container"}>Loading…</div>;
    }

    return (
        <MapContainer center={[props.location.coordinate.latitude, props.location.coordinate.longitude]} zoom={15}
                      scrollWheelZoom={false}>
            <FoodItemMapController location={props.location}/>
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <Marker position={[props.location.coordinate.latitude, props.location.coordinate.longitude]}>
                <Popup>
                    <strong>{props.location.title}</strong>
                </Popup>
            </Marker>
        </MapContainer>
    );
}
