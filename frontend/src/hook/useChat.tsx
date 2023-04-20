import React, {useCallback, useEffect, useState} from "react";
import {Chat} from "../model/Chat";
import {Client} from "@stomp/stompjs";
import {ChatMessage} from "../model/ChatMessage";
import axios from "axios";
import toast from "react-hot-toast";
import {faEnvelope} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Link} from "react-router-dom";
import {User} from "../model/User";
import useGenerateToast from "./useGenerateToast";

export default function useChat(chatId: string | undefined, user: User | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const API_BROKER_URL = generateBrokerUrl();
    const API_SUBSCRIPTION_ENDPOINT = "/user/queue";
    const API_PUBLISH_ENDPOINT = `/api/ws/chat/${chatId}`;
    const [chat, setChat] = useState<Chat | undefined>(undefined);
    const [client, setClient] = useState(new Client());
    const [errorOnInit, setErrorOnInit] = useState<{ message: string, err: any }>();
    const [messageToMarkAsRead, setMessageToMarkAsRead] = useState<ChatMessage>();
    const {errorToast} = useGenerateToast();

    function generateBrokerUrl(): string {
        if (process.env.NODE_ENV !== 'production') {
            return "ws://localhost:8080/api/ws";
        }
        const scheme = window.location.protocol === "https:" ? "wss" : "ws";
        return `${scheme}://${window.location.hostname}:${window.location.port}/api/ws`;
    }

    function markAllUnreadMessagesAsRead() {
        if (chat && user) {
            chat.messages
                .filter(message => message.isUnread && message.senderId !== user.id)
                .forEach(message => markSingleMessageAsRead(message.id))
        }
    }

    const markSingleMessageAsRead = useCallback((messageId: string) => {
        axios.put(`/api/chats/read/${messageId}`)
            .then(response => response.data as ChatMessage)
            .then(readMessage => setChat(chat => chat && ({
                ...chat,
                messages: chat.messages.map(message => message.id !== readMessage.id ? message : readMessage),
                hasUnreadMessages: false
            })))
            .catch(err => {
                errorToast("Could not mark message as read", err);
            })
    }, [errorToast]);

    useEffect(() => {
        if (errorOnInit) {
            errorToast(errorOnInit.message, errorOnInit.err);
            setErrorOnInit(undefined);
        }
    }, [errorOnInit, errorToast]);

    useEffect(() => {
        if (messageToMarkAsRead) {
            setTimeout(() => markSingleMessageAsRead(messageToMarkAsRead.id), 5_000);
            setMessageToMarkAsRead(undefined);
        }
    }, [markSingleMessageAsRead, messageToMarkAsRead]);

    useEffect(() => {
        if (chatId) {
            setAppIsLoading(oldValue => oldValue + 1);
            axios.get(`/api/chats/${chatId}`)
                .then(res => res.data)
                .then(setChat)
                .catch(err => {
                    setErrorOnInit({message: "Could not fetch chat", err: err});
                })
                .finally(() => {
                    setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
                });
            const chatClient = new Client();
            chatClient.configure({
                brokerURL: API_BROKER_URL,
                onConnect: () => {
                    chatClient.subscribe(API_SUBSCRIPTION_ENDPOINT, message => {
                            const newMessage = JSON.parse(message.body) as ChatMessage;
                            if (newMessage.chatId === chatId) {
                                newMessage.comesFromLiveChat = true;
                                setChat(chat => chat && {
                                    ...chat,
                                    messages: [
                                        ...chat.messages,
                                        newMessage
                                    ]
                                });
                                if (newMessage.senderId !== user?.id) {
                                    setMessageToMarkAsRead(newMessage);
                                }
                            } else {
                                toast((t) => (
                                        <>
                                            <h4>Message in another chat</h4>
                                            You received a message in another chat.<br/>Would you like to go there now to read
                                            it?
                                            <footer>
                                                <Link to={`/chats/${newMessage.chatId}`}
                                                      onClick={() => toast.dismiss(t.id)}
                                                      className={"primary-button"}>Read message</Link>
                                                <button className={"secondary-button"}
                                                        onClick={() => toast.dismiss(t.id)}>Stay
                                                    here
                                                </button>
                                            </footer>
                                        </>
                                    ),
                                    {
                                        icon: <FontAwesomeIcon icon={faEnvelope}/>,
                                        duration: Infinity,
                                        className: "actionable-toast"
                                    }
                                );
                            }
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
    }, [API_BROKER_URL, API_SUBSCRIPTION_ENDPOINT, chatId, setAppIsLoading, user?.id])

    function sendNewMessage(message: string) {
        client.publish({destination: API_PUBLISH_ENDPOINT, body: message});
    }

    function startNewChat(foodItemId: string) {
        setAppIsLoading(oldValue => oldValue + 1);
        return axios.post(`/api/chats?foodItemId=${foodItemId}`)
            .then(res => res.data)
            .catch(err => {
                errorToast("Could not start a chat", err);
                return Promise.reject(err);
            })
            .finally(() => {
                setAppIsLoading(oldValue => oldValue - 1);
            });
    }

    return {chat, sendNewMessage, startNewChat, markAllUnreadMessagesAsRead}
}
