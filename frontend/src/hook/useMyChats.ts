import React, {useEffect, useState} from "react";
import {Chat} from "../model/Chat";
import axios from "axios";
import toast from "react-hot-toast";

export default function useMyChats(setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [chats, setChats] = useState<Chat[]>([]);

    useEffect(() => {
        setAppIsLoading(oldValue => oldValue + 1);
        axios.get(`/api/chats`)
            .then(res => res.data)
            .then(setChats)
            .catch(err => {
                toast.error(`Could not fetch chat list 😱\n${err.response.data.error || err.response.data.message}`);
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }, [setAppIsLoading]);

    return chats;
}