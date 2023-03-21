import UserForm from "../component/UserForm";

export default function UserLogInPage() {
    return (
        <main className={"form"}>
            <h1>Log In</h1>
            <UserForm action={"login"}/>
        </main>
    );
}