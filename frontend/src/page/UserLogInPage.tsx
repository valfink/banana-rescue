import UserForm from "../component/UserForm";
import {Link, useLocation} from "react-router-dom";

export default function UserLogInPage() {
    const location = useLocation();

    return (
        <main className={"user form"}>
            <h1>Log In</h1>
            <UserForm action={"login"}/>
            <footer>
                Don't have an account yet?<br/>
                <Link to={"/signup"} state={{navBarBackLink: location.pathname, oldState: location.state}}>
                    Sign up
                </Link>
            </footer>
        </main>
    );
}