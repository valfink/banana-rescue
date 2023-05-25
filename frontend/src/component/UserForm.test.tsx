import {cleanup, render, screen} from "@testing-library/react";
import UserForm from "./UserForm";
import userEvent from "@testing-library/user-event";
import {BrowserRouter} from "react-router-dom";
import {UserContext, UserContextType} from "../context/UserContext";

afterEach(cleanup);

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => jest.fn(),
    useLocation: () => ({state: {redirectAfterLogIn: false}})
}));
const genericPromise = new Promise<void>(resolve => resolve());
const mockedUserContext: UserContextType = {
    user: undefined,
    redirectIfNotSignedIn: () => undefined,
    signUpUser: () => genericPromise,
    logInUser: () => genericPromise,
    logOutUser: () => genericPromise
};

describe("The user form", () => {
    it("updates username state correctly", async () => {
        render(
            <BrowserRouter>
                <UserContext.Provider value={mockedUserContext}>
                    <UserForm action={"login"}/>
                </UserContext.Provider>
            </BrowserRouter>
        );
        const usernameTextbox = screen.getByPlaceholderText("Username");
        await userEvent.type(usernameTextbox, "User_a");
        expect(usernameTextbox).toHaveValue("User_a");
    });

    it("updates password state correctly", async () => {
        render(
            <BrowserRouter>
                <UserContext.Provider value={mockedUserContext}>
                    <UserForm action={"login"}/>
                </UserContext.Provider>
            </BrowserRouter>
        );
        const usernameTextbox = screen.getByPlaceholderText("Password");
        await userEvent.type(usernameTextbox, "Pass");
        expect(usernameTextbox).toHaveValue("Pass");
    })
});