import HeaderBar from "../component/HeaderBar";
import {useState} from "react";
import FullScreenNav from "../component/FullScreenNav";
import "./HeaderBarAndFullScreenNav.css";

type HeaderBarAndFullScreenNavProps = {
    displayHeaderBarShadow: boolean;
}

export default function HeaderBarAndFullScreenNav(props: HeaderBarAndFullScreenNavProps) {
    const [showNavBar, setShowNavBar] = useState(false);

    return (
        <header className={"app-header" + (showNavBar ? " full-screen" : "")}>
            <HeaderBar displayShadow={props.displayHeaderBarShadow} setShowNavBar={setShowNavBar}/>
            <FullScreenNav isOpen={showNavBar} setShowNavBar={setShowNavBar}/>
        </header>
    );
}
