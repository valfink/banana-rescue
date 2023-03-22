import {createContext, Dispatch, SetStateAction} from "react";

export const SetAppIsLoadingContext = createContext<Dispatch<SetStateAction<boolean>>>(() => undefined);
