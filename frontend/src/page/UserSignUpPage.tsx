import UserForm from "../component/UserForm";
import {Link, useLocation} from "react-router-dom";

export default function UserSignUpPage() {
    const location = useLocation();

    return (
        <main className={"user form"}>
            <h1>Create a new account</h1>
            <UserForm action={"signup"}/>
            <footer>
                Already have an account?<br/>
                <Link to={"/login"} state={{navBarBackLink: location.pathname, oldState: location.state}}>
                    Log in
                </Link>
            </footer>
        </main>
    );
}