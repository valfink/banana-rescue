import {createContext} from "react";
import {User} from "../model/User";

type UserContextType = {
    user: User | undefined,
    redirectToLogin: () => void
};

export const UserContext = createContext<UserContextType>({user: undefined, redirectToLogin: () => undefined});
