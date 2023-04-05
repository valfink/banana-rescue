import {Chat} from "../model/Chat";
import {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Link, useLocation, useNavigate} from "react-router-dom";
import "./ChatCard.css";

type ChatCardProps = {
    chat: Chat;
}

export default function ChatCard(props: ChatCardProps) {
    const {user} = useContext(UserContext) as UserContextType;
    const conversationalPartner = user?.id === props.chat.candidate.id ? props.chat.foodItem.donator.username : props.chat.candidate.username;
    const lastMessage = props.chat.messages.length > 0 ? props.chat.messages[props.chat.messages.length - 1].content : "";
    const truncatedMessage = lastMessage.length > 30 ? lastMessage.substring(0, 30) + "…" : lastMessage;
    const location = useLocation();
    const navigate = useNavigate();

    return (
        <article className={"item-card chat-item" + (props.chat.hasUnreadMessages ? " has-unread-messages" : "")}
                 onClick={() => navigate(`/chats/${props.chat.id}`, {
                     state: {
                         navBarBackLink: location.pathname,
                         oldState: location.state
                     }
                 })}>
            <section>
                <h2>{props.chat.foodItem.title}</h2>
                <h4>Chat with <strong>{conversationalPartner}</strong></h4>
                <q>{truncatedMessage}</q>
                <Link to={`/chats/${props.chat.id}`} className={"primary-button"}
                      state={{navBarBackLink: location.pathname, oldState: location.state}}
                      onClick={event => event.stopPropagation()}>Chat</Link>
            </section>
            <aside style={{backgroundImage: `url(${props.chat.foodItem.photoUri || "/surprise-food.jpg"})`}}/>
        </article>
    );
}
