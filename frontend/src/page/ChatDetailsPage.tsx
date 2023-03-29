import {useParams} from "react-router-dom";
import React, {ChangeEvent, FormEvent, useContext, useEffect, useRef, useState} from "react";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {Chat} from "../model/Chat";
import axios from "axios";
import toast from "react-hot-toast";
import FoodItemCard from "../component/FoodItemCard";
import "./ChatDetailsPage.css";
import ChatMessageCard from "../component/ChatMessageCard";
import {Client} from "@stomp/stompjs";
import {ChatMessage} from "../model/ChatMessage";
import {UserContext, UserContextType} from "../context/UserContext";

export default function ChatDetailsPage() {
    const {id} = useParams();
    const {appIsLoading, setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const [chat, setChat] = useState<Chat | undefined>(undefined);
    const [chatContentIsScrolled, setChatContentIsScrolled] = useState(false);
    const [client, setClient] = useState(new Client());
    const [messageDraft, setMessageDraft] = useState("");
    const scrollRef = useRef<HTMLSpanElement>(null);
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;

    // TODO: In Hook oder Util auslagern!

    useEffect(() => {
        redirectIfNotSignedIn();
    }, [redirectIfNotSignedIn]);

    useEffect(() => {
        if (id) {
            const chatClient = new Client();
            chatClient.configure({
                brokerURL: 'ws://localhost:8080/api/ws/chat',
                onConnect: () => {
                    chatClient.subscribe(`/topic/chat/${id}`, message => {
                            const newMessage = JSON.parse(message.body) as ChatMessage;
                            setChat(chat => chat && {
                                ...chat,
                                messages: [
                                    ...chat.messages,
                                    newMessage
                                ]
                            });
                        }
                    );
                }
            });
            chatClient.activate();
            setClient(chatClient);

            return () => {
                chatClient.deactivate();
            }
        }
    }, [id])

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

    useEffect(() => {
        scrollRef.current && scrollRef.current.scrollIntoView({behavior: 'smooth', block: 'end'});
    }, [chat]);

    function handleChatScroll(e: React.UIEvent) {
        setChatContentIsScrolled(e.currentTarget.scrollTop > 0);
    }

    function sendNewMessage(e: FormEvent) {
        e.preventDefault();
        client.publish({destination: `/api/ws/chat/${id}`, body: messageDraft});
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

    function handleChangeMessageDraft(e: ChangeEvent<HTMLInputElement>) {
        setMessageDraft(e.target.value);
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
                <form className={"new-message-bar"} onSubmit={sendNewMessage}>
                    <input type={"text"} value={messageDraft} onChange={handleChangeMessageDraft}
                           placeholder={"Type your message here..."} autoFocus={true}/>
                    <button>Send</button>
                </form>
            </footer>
        </>
    );
}
