import UserForm from "../component/UserForm";
import {Link} from "react-router-dom";

export default function UserLogInPage() {

    return (
        <main className={"user form"}>
            <h1>Log In</h1>
            <UserForm action={"login"}/>
            <footer>
                Don't have an account yet?<br/>
                <Link to={"/signup"} state={{showBackLink: true}}>
                    Sign up
                </Link>
            </footer>
        </main>
    );
}