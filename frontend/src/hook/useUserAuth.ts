import React, {useEffect, useState} from "react";
import {User} from "../model/User";
import axios from "axios";
import {useLocation, useNavigate} from "react-router-dom";
import toast from "react-hot-toast";

export default function useUserAuth(setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
    const [user, setUser] = useState<User | undefined>(undefined);
    const [isLoading, setIsLoading] = useState(false);
    const [doRedirect, setDoRedirect] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();

    function redirectIfNotSignedIn() {
        if (!user) {
            setDoRedirect(true);
        }
    }

    function signUpUser(username: string, password: string) {
        setAppIsLoading(oldValue => oldValue + 1);
        const url = "/api/users",
            data = {username, password};
        return axios.post(url, data)
            .then((res) => {
                const signedUpUser = res.data as User;
                toast.success(`Successfully signed up as ${signedUpUser.username} 🤗`);
            })
            .catch(err => {
                console.error(err);
                const errorMsg = err.response?.data.error || err.response?.data.message || err.message;
                toast.error(`Could not sign up 😱\n${errorMsg}`);
                return Promise.reject(errorMsg);
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }

    function logInUser(username: string, password: string) {
        setAppIsLoading(oldValue => oldValue + 1);
        const url = "/api/users/login",
            btoaString = `${username}:${password}`,
            config = {headers: {Authorization: `Basic ${window.btoa(btoaString)}`}};
        return axios.post(url, {}, config)
            .then(res => {
                const loggedInUser = res.data as User;
                setUser(loggedInUser);
                toast.success(`Successfully logged in as ${loggedInUser.username} 🤗`);
                if (location.state.redirectAfterLogIn) {
                    navigate(location.state.redirectAfterLogIn);
                }
            })
            .catch(err => {
                console.error(err);
                const errorMsg = err.response?.data.error || err.response?.data.message || err.message;
                toast.error(`Could not log in 😱\n${errorMsg}`);
                return Promise.reject(errorMsg);
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }

    function logOutUser() {
        setAppIsLoading(oldValue => oldValue + 1);
        const url = "/api/users/logout";
        return axios.post(url)
            .then(() => {
                setUser(undefined);
                toast.success("Successfully logged out 🤗");
            })
            .catch(err => {
                console.error(err);
                const errorMsg = err.response?.data.error || err.response?.data.message || err.message;
                toast.error(`Could not log out 😱\n${errorMsg}`);
                return Promise.reject(errorMsg);
            })
            .finally(() => {
                setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
            });
    }

    useEffect(() => {
        setIsLoading(true);
        axios.get("/api/users/me")
            .then(res => res.data)
            .then(setUser)
            .catch(() => {
                setUser(undefined);
            })
            .finally(() => {
                setIsLoading(false);
            });
    }, []);

    useEffect(() => {
        if (doRedirect && !isLoading) {
            if (!user) {
                navigate("/login", {state: {redirectAfterLogIn: location.pathname}});
            }
            setDoRedirect(false);
        }
    }, [doRedirect, isLoading, user, navigate, location.pathname])

    return {user, redirectIfNotSignedIn, signUpUser, logInUser, logOutUser};
}
