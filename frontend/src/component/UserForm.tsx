import {Key, Person} from "react-bootstrap-icons";
import {ChangeEvent, FormEvent, useState} from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";

export default function UserForm() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [formError, setFormError] = useState("");
    const navigate = useNavigate();

    function handleInputChange(event: ChangeEvent<HTMLInputElement>) {
        event.target.placeholder === "Username"
            ? setUsername(event.currentTarget.value)
            : setPassword(event.currentTarget.value);
    }

    function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setFormError("");
        const url = "/api/users";
        const data = {username, password};
        const navigateTo = "/";
        axios.post(url, data)
            .then(() => {
                window.sessionStorage.setItem("successMessage", "Successfully registered")
                navigate(navigateTo);
            })
            .catch(err => {
                console.error(err);
                setFormError(err.response.data.error || err.response.data.message);
            });
    }

    return (
        <form onSubmit={handleFormSubmit}>
            {formError && <div className={"form-error"}>Error: {formError}</div>}
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
            <button type={"submit"}>Sign Up</button>
        </form>
    );
}