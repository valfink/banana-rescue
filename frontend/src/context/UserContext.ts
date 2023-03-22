import {createContext} from "react";
import {User} from "../model/User";

type UserContextType = undefined | {
    user: User | undefined,
    redirectToLogin: () => void
};

export const UserContext = createContext<UserContextType>(undefined);
