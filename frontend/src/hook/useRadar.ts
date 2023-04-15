import React, {useEffect, useState} from "react";
import {Radar} from "../model/Radar";
import axios from "axios";
import toast from "react-hot-toast";

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
                } else {
                    console.error(err);
                    toast.error(`Could not fetch radar 😱\n${err.response?.data.error || err.response?.data.message || err.message}`);
                }
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }, [setAppIsLoading]);

    function postRadar(radar: Radar) {
        axios.post("/api/my-radar", radar)
            .then((res) => {
                setRadar(res.data);
                setRadarHasBeenSet(true);
                toast.success("Radar successfully added 🤗");
            })
            .catch(err => {
                console.error(err);
                toast.error(`Could not post radar 😱\n${err.response?.data.error || err.response?.data.message || err.message}`);
            })
    }

    return {radar, radarHasBeenSet, postRadar};
}
