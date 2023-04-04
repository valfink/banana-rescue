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
    const possiblyFromMe = user?.id === props.message.senderId ? " from-me" : "";
    const possiblyUnread = user?.id !== props.message.senderId && props.message.isUnread ? " unread" : "";

    return (
        <article
            className={"single-message" + possiblyFromMe + possiblyUnread}>
            <section className={"message-content" + (props.message.comesFromLiveChat ? " from-live-chat" : "")}>
                {props.message.content}
            </section>
            <section className={"message-timestamp"}>{moment(props.message.timestamp).fromNow()}</section>
        </article>
    );
}