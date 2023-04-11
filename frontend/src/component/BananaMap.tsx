import {Circle, MapContainer, Marker, Popup, TileLayer, useMap} from "react-leaflet";
import React, {useEffect} from "react";
import {Location} from "../model/Location";
import "./FoodItemMap.css";

type BananaMapProps = {
    location: Location;
    radius?: number;
}

function BananaMapController(props: BananaMapProps) {
    const map = useMap();

    useEffect(() => {
        map.setView([props.location.coordinate.latitude, props.location.coordinate.longitude]);
    }, [map, props.location.coordinate.latitude, props.location.coordinate.longitude]);

    useEffect(() => {
        if (props.radius) {
            map.setZoom(23 - Math.floor(Math.log2(props.radius)));
        }
    }, [map, props.radius]);

    return <></>;
}

export default function BananaMap(props: BananaMapProps) {
    if (props.location.coordinate.latitude === 0) {
        return <div className={"leaflet-container"}>Nothing to display yet…</div>;
    }

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
            {props.radius && <Circle radius={props.radius}
                                     center={[props.location.coordinate.latitude, props.location.coordinate.longitude]}/>}
        </MapContainer>
    );
}
