import {useParams} from "react-router-dom";
import React, {useContext, useEffect, useState} from "react";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {Chat} from "../model/Chat";
import axios from "axios";
import toast from "react-hot-toast";
import FoodItemCard from "../component/FoodItemCard";
import "./ChatDetailsPage.css";
import ChatMessageCard from "../component/ChatMessageCard";

export default function ChatDetailsPage() {
    const {id} = useParams();
    const {appIsLoading, setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const [chat, setChat] = useState<Chat | undefined>(undefined);
    const [chatContentIsScrolled, setChatContentIsScrolled] = useState(false);

    useEffect(() => {
        setAppIsLoading(oldValue => oldValue + 1);
        axios.get(`/api/chats/${id}`)
            .then(res => res.data)
            .then(setChat)
            .catch(err => {
                toast.error(`Could not fetch chat 😱\n${err.response.data.error || err.response.data.message}`);
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }, [id, setAppIsLoading]);

    function handleChatScroll(e: React.UIEvent) {
        setChatContentIsScrolled(e.currentTarget.scrollTop > 0);
    }

    if (!chat) {
        if (appIsLoading === 0) {
            return (
                <main>
                    <h1>Sorry, this chat doesn't seem to exist 😢</h1>
                </main>
            );
        } else {
            return <></>;
        }
    }

    const chatCards = chat.messages.map(message => <ChatMessageCard key={message.id} message={message}/>);

    return (
        <main className={"chat-view"} onScroll={handleChatScroll}>
            <header className={"app-header chat-infos" + (chatContentIsScrolled ? " with-shadow" : "")}>
                <FoodItemCard foodItem={chat.foodItem} compactView={true}/>
            </header>
            <section className={"chat-messages"}>
                {chatCards}
                {chatCards.length === 0 && <h2>No messages yet...</h2>}
            </section>
        </main>
    );
}