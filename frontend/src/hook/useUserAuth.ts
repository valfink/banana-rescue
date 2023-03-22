import {useEffect, useState} from "react";
import {User} from "../model/User";
import axios from "axios";
import {useLocation, useNavigate} from "react-router-dom";

export default function useUserAuth() {
    const [user, setUser] = useState<User | undefined>(undefined)
    const navigate = useNavigate();
    const {pathname} = useLocation();

    function redirectToLogin() {
        window.sessionStorage.setItem("signInRedirect", pathname || "/");
        navigate("/login");
    }

    useEffect(() => {
        axios.get("/api/users/me")
            .then(res => res.data)
            .then(setUser)
            .catch(() => {
                setUser(undefined);
            });
    }, []);

    return {user, redirectToLogin};
}
