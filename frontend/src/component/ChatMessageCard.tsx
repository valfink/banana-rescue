import {ChatMessage} from "../model/ChatMessage";
import "./ChatMessageCard.css";
import {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";

type ChatMessageCardProps = {
    message: ChatMessage;
}

export default function ChatMessageCard(props: ChatMessageCardProps) {
    const {user} = useContext(UserContext) as UserContextType;

    return (
        <article className={"single-message" + (user?.id === props.message.senderId ? " from-me" : "")}>
            <section className={"message-content"}>
                {props.message.content}
            </section>
        </article>
    );
}