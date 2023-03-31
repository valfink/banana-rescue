import {ChatMessage} from "../model/ChatMessage";
import "./ChatMessageCard.css";
import {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import moment from "moment";

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
            <section className={"message-timestamp"}>{moment(props.message.timestamp).fromNow()}</section>
        </article>
    );
}