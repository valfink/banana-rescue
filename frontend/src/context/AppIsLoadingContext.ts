import {createContext, Dispatch, SetStateAction} from "react";

export type AppIsLoadingContextType = {
    appIsLoading: number;
    setAppIsLoading: Dispatch<SetStateAction<number>>;
}

export const AppIsLoadingContext = createContext<AppIsLoadingContextType | undefined>(undefined);
