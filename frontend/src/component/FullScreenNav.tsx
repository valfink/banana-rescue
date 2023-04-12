import "./FullScreenNav.css";
import {NavLink, useLocation} from "react-router-dom";
import React, {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";

type FullScreenNavProps = {
    isOpen: boolean;
    setShowNavBar: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function FullScreenNav(props: FullScreenNavProps) {
    const {user, logOutUser} = useContext(UserContext) as UserContextType;
    const location = useLocation();

    function closeNavBar() {
        props.setShowNavBar(false);
    }

    return props.isOpen ? (
        <nav className={"full-screen-nav"} onClick={closeNavBar}>
            <NavLink to={"/food"} className={"secondary-button cursor-pointer"}>All Items</NavLink>
            <NavLink to={"/add-food"} className={"secondary-button cursor-pointer"}
                     state={{navBarBackLink: location.pathname, oldState: location.state}}>Add Item</NavLink>
            {user ?
                <>
                    <NavLink to={"/food/my-items"} className={"secondary-button cursor-pointer"}>My Items</NavLink>
                    <NavLink to={"/chats"} className={"secondary-button cursor-pointer"}>My Chats</NavLink>
                    <NavLink to={"/my-radar"} className={"secondary-button cursor-pointer"}>My Radar</NavLink>
                    <button onClick={logOutUser} className={"secondary-button cursor-pointer"}>Log Out</button>
                </>
                :
                <>
                    <NavLink to={"/login"} className={"secondary-button cursor-pointer"}
                             state={{navBarBackLink: location.pathname, oldState: location.state}}>Log In</NavLink>
                    <NavLink to={"/signup"} className={"secondary-button cursor-pointer"}
                             state={{navBarBackLink: location.pathname, oldState: location.state}}>Sign Up</NavLink>
                </>
            }
        </nav>
    ) : <></>;
}