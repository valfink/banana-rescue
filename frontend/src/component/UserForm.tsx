import {ChangeEvent, FormEvent, useContext, useEffect, useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faKey, faUser} from "@fortawesome/free-solid-svg-icons";
import {UserContext, UserContextType} from "../context/UserContext";
import "./UserForm.css";

type UserFormProps = {
    action: "login" | "signup"
}

export default function UserForm(props: UserFormProps) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [formError, setFormError] = useState("");
    const navigate = useNavigate();
    const location = useLocation();
    const successMessage = useLocation().state?.successMessage || "";
    const {user, signUpUser, logInUser} = useContext(UserContext) as UserContextType;

    function handleInputChange(event: ChangeEvent<HTMLInputElement>) {
        event.target.placeholder === "Username"
            ? setUsername(event.currentTarget.value)
            : setPassword(event.currentTarget.value);
    }

    function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setFormError("");
        let navigateTo = location.state.redirectAfterLogIn || "/",
            navigateOptions = {state: {}};
        if (props.action === "signup") {
            navigateTo = "/login";
            navigateOptions.state = {successMessage: "Successfully registered."};
            signUpUser(username, password)
                .then(() => {
                    navigate(navigateTo, navigateOptions);
                })
                .catch(setFormError);
        } else {
            navigateOptions.state = {successMessage: "Successfully logged in."};
            logInUser(username, password)
                .then(() => {
                    navigate(navigateTo, navigateOptions);
                })
                .catch(setFormError);
        }
    }

    useEffect(() => {
        if (user) {
            navigate(location.state.redirectAfterLogIn || "/");
        }
    }, [location.state.redirectAfterLogIn, navigate, user]);

    return (
        <form onSubmit={handleFormSubmit}>
            {formError && <div className={"form-error"}>Error: {formError}</div>}
            {successMessage && <div className={"form-success"}>{successMessage}</div>}
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faUser}/>
                <input type={"text"} placeholder={"Username"} value={username} onChange={handleInputChange}
                       required={true} autoFocus={true}/>
            </div>
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faKey}/>
                <input type={"password"} placeholder={"Password"} value={password} onChange={handleInputChange}
                       required={true}/>
            </div>
            <button type={"submit"}>{props.action === "signup" ? "Sign Up" : "Log In"}</button>
        </form>
    );
}
