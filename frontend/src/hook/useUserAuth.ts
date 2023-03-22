import {useEffect, useState} from "react";
import {User} from "../model/User";
import axios from "axios";
import {useLocation, useNavigate} from "react-router-dom";

export default function useUserAuth() {
    const [user, setUser] = useState<User | undefined>(undefined);
    const [isLoading, setIsLoading] = useState(false);
    const [doRedirect, setDoRedirect] = useState(false);
    const navigate = useNavigate();
    const {pathname} = useLocation();

    function redirectIfNotSignedIn() {
        if (!user) {
            setDoRedirect(true);
        }
    }

    useEffect(() => {
        setIsLoading(true);
        axios.get("/api/users/me")
            .then(res => res.data)
            .then(setUser)
            .catch(() => {
                setUser(undefined);
            })
            .finally(() => setIsLoading(false));
    }, []);

    useEffect(() => {
        if (doRedirect && !isLoading) {
            if (!user) {
                window.sessionStorage.setItem("signInRedirect", pathname || "/");
                navigate("/login");
            }
            setDoRedirect(false);
        }
    }, [doRedirect, isLoading, user, navigate, pathname])

    return {user, redirectIfNotSignedIn, setUser};
}
