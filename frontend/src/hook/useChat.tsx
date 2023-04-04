import React, {useEffect, useState} from "react";
import {Chat} from "../model/Chat";
import {Client} from "@stomp/stompjs";
import {ChatMessage} from "../model/ChatMessage";
import axios from "axios";
import toast from "react-hot-toast";
import {faEnvelope} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Link} from "react-router-dom";

export default function useChat(chatId: string | undefined, setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const API_BROKER_URL = `ws://${window.location.hostname}:8080/api/ws/chat`;
    const API_SUBSCRIPTION_ENDPOINT = "/user/queue";
    const API_PUBLISH_ENDPOINT = `/api/ws/chat/${chatId}`;
    const [chat, setChat] = useState<Chat | undefined>(undefined);
    const [client, setClient] = useState(new Client());

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
                            if (newMessage.chatId === chatId) {
                                newMessage.comesFromLiveChat = true;
                                setChat(chat => chat && {
                                    ...chat,
                                    messages: [
                                        ...chat.messages,
                                        newMessage
                                    ]
                                });
                            } else {
                                toast((t) => (
                                        <>
                                            <h4>Message in another chat</h4>
                                            You received a message in another chat.<br/>Would you like to go there now to read
                                            it?
                                            <section>
                                                <Link to={`/chats/${newMessage.chatId}`} onClick={() => toast.dismiss(t.id)}
                                                      className={"primary-button"}>Read message</Link>
                                                <button className={"secondary-button"} onClick={() => toast.dismiss(t.id)}>Stay
                                                    here
                                                </button>
                                            </section>
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
