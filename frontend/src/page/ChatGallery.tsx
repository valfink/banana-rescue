import {useContext, useEffect, useState} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import axios from "axios";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import toast from "react-hot-toast";
import {Chat} from "../model/Chat";
import ChatCard from "../component/ChatCard";

export default function ChatGallery() {
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
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

    useEffect(() => {
        redirectIfNotSignedIn();
    }, [redirectIfNotSignedIn]);

    return (
        <main className={"item-gallery"}>
            <h1>My Chats</h1>
            <section>
                {chats.map(chat => <ChatCard key={chat.id} chat={chat}/>)}
            </section>
        </main>
    );
}
