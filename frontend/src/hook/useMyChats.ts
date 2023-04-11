import React, {useEffect, useState} from "react";
import {Chat} from "../model/Chat";
import axios from "axios";
import toast from "react-hot-toast";
import {User} from "../model/User";

export default function useMyChats(user: User | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [chats, setChats] = useState<Chat[]>([]);

    useEffect(() => {
        setAppIsLoading(oldValue => oldValue + 1);
        axios.get(`/api/chats`)
            .then(res => res.data as Chat[])
            .then(loadedChats => loadedChats.map(chat => ({
                ...chat,
                hasUnreadMessages: chat.messages.some(message => message.isUnread && message.senderId !== user?.id)
            })).sort((a: Chat, b: Chat) => Number(b.hasUnreadMessages) - Number(a.hasUnreadMessages)))
            .then(setChats)
            .catch(err => {
                toast.error(`Could not fetch chat list 😱\n${err.response?.data.error || err.response?.data.message || err.message}`);
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }, [setAppIsLoading, user]);

    return chats;
}