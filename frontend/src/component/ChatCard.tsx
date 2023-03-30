import {Chat} from "../model/Chat";
import {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Link, useLocation} from "react-router-dom";
import "./ChatCard.css";

type ChatCardProps = {
    chat: Chat;
}

export default function ChatCard(props: ChatCardProps) {
    const {user} = useContext(UserContext) as UserContextType;
    const conversationalPartner = user?.id === props.chat.candidate.id ? props.chat.foodItem.donator.username : props.chat.candidate.username;
    const lastMessage = props.chat.messages.length > 0 ? props.chat.messages[props.chat.messages.length - 1].content : "";
    const truncatedMessage = lastMessage.length > 65 ? lastMessage.substring(0, 65) + "…" : lastMessage;
    const {pathname} = useLocation();

    console.log(`Message count: ${props.chat.messages.length}`)
    props.chat.messages.length > 0 && console.log(`First message: ${props.chat.messages[0].content}`);
    props.chat.messages.length > 0 && console.log(`Last message: ${props.chat.messages[props.chat.messages.length - 1].content}`);

    return (
        <article className={"item-card chat-item"}>
            <section>
                <h2>{props.chat.foodItem.title}</h2>
                <h4>Chat with <strong>{conversationalPartner}</strong></h4>
                <q>{truncatedMessage}</q>
                <Link to={`/chats/${props.chat.id}`} className={"primary-button"}
                      state={{navBarBackLink: pathname}}>Chat</Link>
            </section>
            <aside style={{backgroundImage: `url(${props.chat.foodItem.photoUri || "/surprise-food.jpg"})`}}/>
        </article>
    );
}
