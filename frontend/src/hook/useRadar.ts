import React, {useEffect, useState} from "react";
import {Radar} from "../model/Radar";
import axios from "axios";

export default function useRadar(setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [radar, setRadar] = useState<Radar | undefined>(undefined);
    const [radarHasBeenSet, setRadarHasBeenSet] = useState(true);

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

    return {radar, radarHasBeenSet};
}
