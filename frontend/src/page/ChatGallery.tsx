import {useContext, useEffect} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import ChatCard from "../component/ChatCard";
import useMyChats from "../hook/useMyChats";

export default function ChatGallery() {
    const {user, redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const chats = useMyChats(user, setAppIsLoading);

    useEffect(redirectIfNotSignedIn, [redirectIfNotSignedIn]);

    return (
        <main className={"item-gallery"}>
            <h1>My Chats</h1>
            <section>
                {chats.map(chat => <ChatCard key={chat.id} chat={chat}/>)}
                {chats.length === 0 && <article className={"item-card chat-item"}>
                    <section><h2>No chats to display (yet) 😢</h2></section>
                </article>}
            </section>
        </main>
    );
}
