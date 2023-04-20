import React, {useEffect, useState} from "react";
import {Radar} from "../model/Radar";
import axios from "axios";
import useGenerateToast from "./useGenerateToast";

export default function useRadar(setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [radar, setRadar] = useState<Radar | undefined>(undefined);
    const [radarHasBeenSet, setRadarHasBeenSet] = useState(true);
    const {errorToast, successToast} = useGenerateToast();

    useEffect(() => {
        setAppIsLoading(oldValue => oldValue + 1);
        axios.get("/api/my-radar")
            .then(response => response.data as Radar)
            .then(setRadar)
            .catch(err => {
                if (err.response.status === 404) {
                    setRadarHasBeenSet(false);
                } else {
                    errorToast("Could not fetch radar", err);
                }
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }, [errorToast, setAppIsLoading]);

    function postRadar(radar: Radar) {
        axios.post("/api/my-radar", radar)
            .then((res) => {
                setRadar(res.data);
                setRadarHasBeenSet(true);
                successToast("Radar successfully added");
            })
            .catch(err => {
                errorToast("Could not post radar", err);
            })
    }

    return {radar, radarHasBeenSet, postRadar};
}
