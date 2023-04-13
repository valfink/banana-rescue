import {useParams} from "react-router-dom";
import React, {ChangeEvent, FormEvent, useContext, useEffect, useRef, useState} from "react";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import FoodItemCard from "../component/FoodItemCard";
import "./ChatDetailsPage.css";
import ChatMessageCard from "../component/ChatMessageCard";
import {UserContext, UserContextType} from "../context/UserContext";
import useChat from "../hook/useChat";

export default function ChatDetailsPage() {
    const {id} = useParams();
    const {appIsLoading, setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const {user, redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const {chat, sendNewMessage, markAllUnreadMessagesAsRead} = useChat(id, user, setAppIsLoading);
    const [chatContentIsScrolled, setChatContentIsScrolled] = useState(false);
    const [messageDraft, setMessageDraft] = useState("");
    const scrollRef = useRef<HTMLSpanElement>(null);
    const messageDraftRef = useRef<HTMLInputElement>(null);

    useEffect(redirectIfNotSignedIn, [redirectIfNotSignedIn]);


    useEffect(() => {
        scrollRef.current && scrollRef.current.scrollIntoView({block: 'end'});
    }, [chat]);

    useEffect(() => {
        const timeOut = setTimeout(markAllUnreadMessagesAsRead, 5_000);
        return () => {
            clearTimeout(timeOut);
        }
    }, [markAllUnreadMessagesAsRead]);

    function handleChatScroll(e: React.UIEvent) {
        setChatContentIsScrolled(e.currentTarget.scrollTop > 0);
    }

    function handleChangeMessageDraft(e: ChangeEvent<HTMLInputElement>) {
        setMessageDraft(e.target.value);
        markAllUnreadMessagesAsRead();
    }

    function handleFormSubmit(e: FormEvent) {
        e.preventDefault();
        messageDraftRef.current && messageDraftRef.current.focus();
        sendNewMessage(messageDraft);
        setMessageDraft("");
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
        <>
            <main className={"chat-view"} onScroll={handleChatScroll}>
                <header className={"app-header chat-infos" + (chatContentIsScrolled ? " with-shadow" : "")}>
                    <FoodItemCard foodItem={chat.foodItem} compactView={true}/>
                </header>
                <section className={"chat-messages"} ref={scrollRef}>
                    {chatCards}
                    {chatCards.length === 0 && <h2>No messages yet...</h2>}
                </section>
            </main>
            <footer>
                <form className={"new-message-bar"} onSubmit={handleFormSubmit}>
                    <input type={"text"} value={messageDraft} onChange={handleChangeMessageDraft}
                           placeholder={"Type your message here..."} autoFocus={true} ref={messageDraftRef}/>
                    <button>Send</button>
                </form>
            </footer>
        </>
    );
}
