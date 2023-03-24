import React, {createContext} from "react";
import {User} from "../model/User";

export type UserContextType = {
    user: User | undefined;
    redirectIfNotSignedIn: () => void;
    setUser: React.Dispatch<React.SetStateAction<User | undefined>>;
    signUpUser: (username: string, password: string) => Promise<any>;
    logInUser: (username: string, password: string) => Promise<any>;
};

export const UserContext = createContext<UserContextType | UserContextType["user"]>(undefined);
