import "./HeaderBar.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowLeft, faBars, faCircleUser as loggedInUser, faTruckMedical} from "@fortawesome/free-solid-svg-icons";
import {faCircleUser as loggedOutUser} from "@fortawesome/free-regular-svg-icons";
import React, {useContext, useState} from "react";
import {UserContext, UserContextType} from "../context/UserContext";
import {Link, useLocation} from "react-router-dom";
import LogOutWarningScreen from "../modal/LogOutWarningScreen";

type HeaderBarProps = {
    displayShadow: boolean;
    setShowNavBar: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function HeaderBar(props: HeaderBarProps) {
    const {user, logOutUser} = useContext(UserContext) as UserContextType;
    const [showLogOutWarning, setShowLogOutWarning] = useState(false);
    const location = useLocation();
    const navBarBackLink = location.state?.navBarBackLink;

    function handleShowNavBarClick() {
        props.setShowNavBar(showNavBar => !showNavBar);
    }

    function closeNavBarIfOpen() {
        props.setShowNavBar(false);
    }

    function handleUserIconClick() {
        setShowLogOutWarning(true);
    }

    function handleCloseLogOutWarningClick() {
        setShowLogOutWarning(false);
    }

    function handleLogOutClick() {
        logOutUser()
            .finally(() => {
                setShowLogOutWarning(false);
            });
    }

    return (
        <section className={"header-bar" + (props.displayShadow ? " with-shadow" : "")}>
            <LogOutWarningScreen modalIsOpen={showLogOutWarning} closeModal={handleCloseLogOutWarningClick}
                                 logOut={handleLogOutClick}/>
            {navBarBackLink
                ? <Link to={navBarBackLink} state={location.state.oldState}><FontAwesomeIcon icon={faArrowLeft}/></Link>
                : <span className={"cursor-pointer"} onClick={handleShowNavBarClick}><FontAwesomeIcon
                    icon={faBars}/></span>}
            <Link to={"/"} onClick={closeNavBarIfOpen}><FontAwesomeIcon icon={faTruckMedical}/> Banana Rescue</Link>
            {user ?
                <span onClick={handleUserIconClick} className={"cursor-pointer"}>
                    <FontAwesomeIcon icon={loggedInUser}/>
                </span>
                :
                <Link to={"/login"} state={{
                    navBarBackLink: location.pathname,
                    redirectAfterLogIn: location.pathname,
                    oldState: location.state
                }}
                      onClick={closeNavBarIfOpen}>
                    <FontAwesomeIcon icon={loggedOutUser} className={"not-logged-in"}/>
                </Link>
            }
        </section>
    );
}
