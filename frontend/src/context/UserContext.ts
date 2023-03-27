import {createContext} from "react";
import {User} from "../model/User";

export type UserContextType = {
    user: User | undefined;
    redirectIfNotSignedIn: () => void;
    signUpUser: (username: string, password: string) => Promise<void>;
    logInUser: (username: string, password: string) => Promise<void>;
    logOutUser: () => Promise<void>;
};

export const UserContext = createContext<UserContextType | UserContextType["user"]>(undefined);
