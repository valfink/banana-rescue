import {User} from "../model/User";
import React, {useEffect, useState} from "react";
import toast from "react-hot-toast";
import {Link} from "react-router-dom";
import {FoodItem} from "../model/FoodItem";
import {Client} from "@stomp/stompjs";

export default function useWebSocketNotifications(user: User | undefined) {
    const API_BROKER_URL = generateBrokerUrl();
    const API_SUBSCRIPTION_ENDPOINT = "/user/queue/radar";
    const [, setClient] = useState(new Client());

    function generateBrokerUrl(): string {
        if (process.env.NODE_ENV !== 'production') {
            return "ws://localhost:8080/api/ws";
        }
        const scheme = window.location.protocol === "https:" ? "wss" : "ws";
        return `${scheme}://${window.location.hostname}:${window.location.port}/api/ws`;
    }

    useEffect(() => {
        console.log("Use WebSocket Notification...");
        if (user) {
            console.log("User exists. Connecting...");
            const chatClient = new Client();
            chatClient.configure({
                brokerURL: API_BROKER_URL,
                onConnect: () => {
                    console.log("Connected. Subscribing...");
                    chatClient.subscribe(API_SUBSCRIPTION_ENDPOINT, message => {
                        const newRadarItem = JSON.parse(message.body) as FoodItem;
                        toast((t) => (
                                <>
                                    <main>
                                        <h4>New Food Item on your Radar</h4>
                                        There is a new Food Item within your Radar's radius.<br/>Would you like to see it
                                        now?
                                        <section>
                                            <Link to={`/food/${newRadarItem.id}`} onClick={() => toast.dismiss(t.id)}
                                                  className={"primary-button"}>View Food Item</Link>
                                            <button className={"secondary-button"} onClick={() => toast.dismiss(t.id)}>
                                                Stay here
                                            </button>
                                        </section>
                                    </main>
                                    <aside
                                        style={{backgroundImage: `url(${newRadarItem.photoUri || "/surprise-food.jpg"})`}}/>
                                </>
                            ),
                            {
                                icon: "🍌",
                                duration: Infinity,
                                className: "actionable-toast radar-toast"
                            });
                    });
                }
            });
            chatClient.activate();
            setClient(chatClient);

            return () => {
                chatClient.deactivate();
            }
        }
    }, [API_BROKER_URL, API_SUBSCRIPTION_ENDPOINT, user])
}
