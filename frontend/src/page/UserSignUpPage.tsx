import UserForm from "../component/UserForm";

export default function UserSignUpPage() {
    return (
        <main className={"form"}>
            <h1>Create a new account</h1>
            <UserForm action={"signup"}/>
        </main>
    );
}