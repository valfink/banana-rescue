import React, {useEffect, useState} from "react";
import {User} from "../model/User";
import axios from "axios";
import {useLocation, useNavigate} from "react-router-dom";

export default function useUserAuth(setAppIsLoading: React.Dispatch<React.SetStateAction<number>>) {
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

    function signUpUser(username: string, password: string) {
        setAppIsLoading(oldValue => oldValue + 1);
        const url = "/api/users",
            data = {username, password};
        return axios.post(url, data)
            .then(() => {
                return;
            })
            .catch(err => {
                console.error(err);
                return Promise.reject(err.response.data.error || err.response.data.message);
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
                setUser(res.data);
                return;
            })
            .catch(err => {
                console.error(err);
                return Promise.reject(err.response.data.error || err.response.data.message);
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
                return;
            })
            .catch(err => {
                console.error(err);
                return Promise.reject(err.response.data.error || err.response.data.message);
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
                window.sessionStorage.setItem("signInRedirect", pathname || "/");
                navigate("/login");
            }
            setDoRedirect(false);
        }
    }, [doRedirect, isLoading, user, navigate, pathname])

    return {user, redirectIfNotSignedIn, signUpUser, logInUser, logOutUser};
}
