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
    const {chat, sendNewMessage} = useChat(id, setAppIsLoading);
    const [chatContentIsScrolled, setChatContentIsScrolled] = useState(false);
    const [messageDraft, setMessageDraft] = useState("");
    const scrollRef = useRef<HTMLSpanElement>(null);
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;

    useEffect(() => {
        redirectIfNotSignedIn();
    }, [redirectIfNotSignedIn]);


    useEffect(() => {
        scrollRef.current && scrollRef.current.scrollIntoView({behavior: 'smooth', block: 'end'});
    }, [chat]);

    function handleChatScroll(e: React.UIEvent) {
        setChatContentIsScrolled(e.currentTarget.scrollTop > 0);
    }

    function handleChangeMessageDraft(e: ChangeEvent<HTMLInputElement>) {
        setMessageDraft(e.target.value);
    }

    function handleFormSubmit(e: FormEvent) {
        e.preventDefault();
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
                           placeholder={"Type your message here..."} autoFocus={true}/>
                    <button>Send</button>
                </form>
            </footer>
        </>
    );
}
