import {useEffect, useState} from "react";
import {Chat} from "../model/Chat";
import {Client} from "@stomp/stompjs";
import {ChatMessage} from "../model/ChatMessage";

export default function useChat(id: string | undefined) {
    const [chat, setChat] = useState<Chat | undefined>(undefined);
    const [client, setClient] = useState(new Client());

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

    function sendNewMessage(message: string) {
        client.publish({destination: `/api/ws/chat/${id}`, body: message});
    }

    return {chat, setChat, sendNewMessage}
}
