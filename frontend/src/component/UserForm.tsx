import {Key, Person} from "react-bootstrap-icons";

export default function UserForm() {
    return (
        <form>
            <div className={"input-with-icon"}>
                <Person/>
                <input type={"text"} placeholder={"Username"}/>
            </div>
            <div className={"input-with-icon"}>
                <Key/>
                <input type={"password"} placeholder={"Password"}/>
            </div>
            <button type={"submit"}>Sign Up</button>
        </form>
    );
}