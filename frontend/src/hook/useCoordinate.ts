import React, {useState} from "react";
import {Coordinate} from "../model/Coordinate";
import axios from "axios";
import {OpenStreetMapsSearchResult} from "../model/OpenStreetMapsSearchResult";
import toast from "react-hot-toast";

export default function useCoordinate(setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [foundCoordinate, setFoundCoordinate] = useState<Coordinate>({latitude: 0, longitude: 0});
    const [error, setError] = useState(false);

    function searchForCoordinates(searchString: string) {
        console.log("SEARCH");
        console.log("go");
        setAppIsLoading(oldValue => oldValue + 1);
        setError(false);
        axios.get(`https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=${searchString}`)
            .then(res => res.data as OpenStreetMapsSearchResult[])
            .then((results) => {
                if (results.length > 0) {
                    setFoundCoordinate({latitude: results[0].lat, longitude: results[0].lon});
                } else {
                        setError(true);
                    }
                })
                .catch(err => {
                    console.error(err);
                    toast.error(`Could not fetch location search results 😱\n${err.response?.data.error || err.response?.data.message || err.message}`);
                })
                .finally(() => {
                    setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
                });
    }

    return {searchForCoordinates, foundCoordinate, error};
}
