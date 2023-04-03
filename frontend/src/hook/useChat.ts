import React, {useEffect, useState} from "react";
import {Chat} from "../model/Chat";
import {Client} from "@stomp/stompjs";
import {ChatMessage} from "../model/ChatMessage";
import axios from "axios";
import toast from "react-hot-toast";

export default function useChat(chatId: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [chat, setChat] = useState<Chat | undefined>(undefined);
    const [client, setClient] = useState(new Client());
    const API_BROKER_URL = `${window.location.protocol === "http:" ? 'ws' : 'wss'}://${window.location.hostname}:8080/api/ws/chat`;
    const API_SUBSCRIPTION_ENDPOINT = "/user/queue";
    const API_PUBLISH_ENDPOINT = `/api/ws/chat/${chatId}`;

    useEffect(() => {
        if (chatId) {
            setAppIsLoading(oldValue => oldValue + 1);
            axios.get(`/api/chats/${chatId}`)
                .then(res => res.data)
                .then(setChat)
                .catch(err => {
                    toast.error(`Could not fetch chat 😱\n${err.response.data.error || err.response.data.message}`);
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
                            newMessage.comesFromLiveChat = true;
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
    }, [API_BROKER_URL, API_SUBSCRIPTION_ENDPOINT, chatId, setAppIsLoading])

    function sendNewMessage(message: string) {
        client.publish({destination: API_PUBLISH_ENDPOINT, body: message});
    }

    function startNewChat(foodItemId: string) {
        setAppIsLoading(oldValue => oldValue + 1);
        return axios.post(`/api/chats?foodItemId=${foodItemId}`)
            .then(res => res.data)
            .catch(err => {
                console.error(err);
                toast.error(`Could not start a chat 😱\n${err.response.data.error || err.response.data.message}`);
                return Promise.reject(err);
            })
            .finally(() => {
                setAppIsLoading(oldValue => oldValue - 1);
            });
    }

    return {chat, sendNewMessage, startNewChat}
}
