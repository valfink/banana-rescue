import "./HeaderBar.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowLeft, faBars, faCircleUser as loggedInUser, faTruckMedical} from "@fortawesome/free-solid-svg-icons";
import {faCircleUser as loggedOutUser} from "@fortawesome/free-regular-svg-icons";
import React, {useContext} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Link, useLocation} from "react-router-dom";

type HeaderBarProps = {
    displayShadow: boolean;
    setShowNavBar: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function HeaderBar(props: HeaderBarProps) {
    const {user, logOutUser} = useContext(UserContext) as UserContextType;
    const location = useLocation();
    const navBarBackLink = location.state?.navBarBackLink;

    function handleShowNavBarClick() {
        props.setShowNavBar(showNavBar => !showNavBar);
    }

    return (
        <section className={"header-bar" + (props.displayShadow ? " with-shadow" : "")}>
            {navBarBackLink ?
                <Link to={navBarBackLink} state={location.state.oldState}><FontAwesomeIcon icon={faArrowLeft}/></Link> :
                <span className={"cursor-pointer"} onClick={handleShowNavBarClick}><FontAwesomeIcon
                    icon={faBars}/></span>}
            <span><FontAwesomeIcon icon={faTruckMedical}/> Banana Rescue</span>
            {user ?
                <span onClick={logOutUser} className={"cursor-pointer"}>
                    <FontAwesomeIcon icon={loggedInUser}/>
                </span>
                :
                <Link to={"/login"} state={{navBarBackLink: location.pathname, oldState: location.state}}>
                    <FontAwesomeIcon icon={loggedOutUser} className={"not-logged-in"}/>
                </Link>
            }
        </section>
    );
}
