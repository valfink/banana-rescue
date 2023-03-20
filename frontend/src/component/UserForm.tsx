import {Key, Person} from "react-bootstrap-icons";
import {ChangeEvent, FormEvent, useState} from "react";
import axios from "axios";
import {useLocation, useNavigate} from "react-router-dom";

type UserFormProps = {
    action: "login" | "signup"
}

export default function UserForm(props: UserFormProps) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [formError, setFormError] = useState("");
    const navigate = useNavigate();
    const successMessage = useLocation().state?.successMessage || "";


    function handleInputChange(event: ChangeEvent<HTMLInputElement>) {
        event.target.placeholder === "Username"
            ? setUsername(event.currentTarget.value)
            : setPassword(event.currentTarget.value);
    }

    function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setFormError("");
        let url = "/api/users",
            data = {},
            config = {},
            navigateTo = "/",
            navigateOptions = {state: {}};
        if (props.action === "signup") {
            data = {username, password};
            navigateTo = "/login";
            navigateOptions.state = {successMessage: "Successfully registered."};
        } else {
            url += "/login";
            const btoaString = `${username}:${password}`;
            config = {headers: {Authorization: `Basic ${window.btoa(btoaString)}`}};
            navigateOptions.state = {successMessage: "Successfully logged in."};
        }
        axios.post(url, data, config)
            .then(() => {
                navigate(navigateTo, navigateOptions);
            })
            .catch(err => {
                console.error(err);
                setFormError(err.response.data.error || err.response.data.message);
            });
    }

    return (
        <form onSubmit={handleFormSubmit}>
            {formError && <div className={"form-error"}>Error: {formError}</div>}
            {successMessage && <div className={"form-success"}>{successMessage}</div>}
            <div className={"input-with-icon"}>
                <Person/>
                <input type={"text"} placeholder={"Username"} value={username} onChange={handleInputChange}
                       required={true}/>
            </div>
            <div className={"input-with-icon"}>
                <Key/>
                <input type={"password"} placeholder={"Password"} value={password} onChange={handleInputChange}
                       required={true}/>
            </div>
            <button type={"submit"}>{props.action === "signup" ? "Sign Up" : "Log In"}</button>
        </form>
    );
}