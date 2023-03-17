import "./UserSignUpPage.css"
import UserForm from "../component/UserForm";

export default function UserSignUpPage() {
    return (
        <main className={"user-form"}>
            <h1>Create a new account</h1>
            <UserForm/>
        </main>
    );
}