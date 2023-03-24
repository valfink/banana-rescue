import "./FullScreenNav.css";
import {NavLink} from "react-router-dom";
import React, {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";

type FullScreenNavProps = {
    isOpen: boolean;
    setShowNavBar: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function FullScreenNav(props: FullScreenNavProps) {
    const {user} = useContext(UserContext) as UserContextType;

    function closeNavBar() {
        props.setShowNavBar(false);
    }

    return props.isOpen ? (
        <nav className={"full-screen-nav"} onClick={closeNavBar}>
            <NavLink to={"/food"} className={"secondary-button cursor-pointer"}>All Items</NavLink>
            <NavLink to={"/add-food"} className={"secondary-button cursor-pointer"}>Add Item</NavLink>
            {!user &&
                <>
                    <NavLink to={"/login"} className={"secondary-button cursor-pointer"}>Log In</NavLink>
                    <NavLink to={"/signup"} className={"secondary-button cursor-pointer"}>Sign Up</NavLink>
                </>
            }
        </nav>
    ) : <></>;
}