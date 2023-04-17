import UserForm from "../component/UserForm";
import {Link} from "react-router-dom";

export default function UserSignUpPage() {

    return (
        <main className={"user form"}>
            <h1>Create a new account</h1>
            <UserForm action={"signup"}/>
            <footer>
                Already have an account?<br/>
                <Link to={"/login"} state={{showBackLink: true}}>
                    Log in
                </Link>
            </footer>
        </main>
    );
}