import "./HeaderBar.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCircleUser as loggedInUser} from "@fortawesome/free-solid-svg-icons";
import {faCircleUser as loggedOutUser} from "@fortawesome/free-regular-svg-icons";
import {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Link} from "react-router-dom";

type HeaderBarProps = {
    displayShadow: boolean;
}

export default function HeaderBar(props: HeaderBarProps) {
    const {user} = useContext(UserContext) as UserContextType;

    return (
        <header className={"header-bar" + (props.displayShadow ? " with-shadow" : "")}>
            <span>...</span>
            Banana Rescue
            <Link to={"/login"}><span><FontAwesomeIcon icon={user ? loggedInUser : loggedOutUser}/></span></Link>
        </header>
    );
}
