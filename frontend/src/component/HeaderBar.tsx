import "./HeaderBar.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBars, faCircleUser as loggedInUser, faTruckMedical} from "@fortawesome/free-solid-svg-icons";
import {faCircleUser as loggedOutUser} from "@fortawesome/free-regular-svg-icons";
import React, {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Link} from "react-router-dom";

type HeaderBarProps = {
    displayShadow: boolean;
    setShowNavBar: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function HeaderBar(props: HeaderBarProps) {
    const {user} = useContext(UserContext) as UserContextType;

    function handleShowNavBarClick() {
        props.setShowNavBar(showNavBar => !showNavBar);
    }

    return (
        <section className={"header-bar" + (props.displayShadow ? " with-shadow" : "")}>
            <span className={"cursor-pointer"} onClick={handleShowNavBarClick}><FontAwesomeIcon icon={faBars}/></span>
            <span><FontAwesomeIcon icon={faTruckMedical}/> Banana Rescue</span>
            <Link to={"/login"}><FontAwesomeIcon icon={user ? loggedInUser : loggedOutUser}/></Link>
        </section>
    );
}
