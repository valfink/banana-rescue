import React, {createContext} from "react";
import {User} from "../model/User";

type UserContextType = {
    user: User | undefined;
    redirectIfNotSignedIn: () => void;
    setUser: React.Dispatch<React.SetStateAction<User | undefined>>;
};

export const UserContext = createContext<UserContextType>({
    user: undefined,
    redirectIfNotSignedIn: () => undefined,
    setUser: () => undefined
});
