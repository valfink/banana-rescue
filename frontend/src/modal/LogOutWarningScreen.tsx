import "./DeletionWarningScreen.css";
import Modal from 'react-modal';

type LogOutWarningScreenProps = {
    modalIsOpen: boolean;
    closeModal: () => void;
    logOut: () => void;
}

export default function LogOutWarningScreen(props: LogOutWarningScreenProps) {
    Modal.setAppElement('#root');
    return (
        <Modal
            isOpen={props.modalIsOpen}
            bodyOpenClassName={"has-open-modal"}
            htmlOpenClassName={"has-open-modal"}
            className={"modal deletion-warning"}
            overlayClassName={"overlay"}
        >
            <h1>LOG OUT</h1>
            <h2>
                Do you want to log out of your account?<br/>
                You can log in later again.
            </h2>
            <section className={"buttons"}>
                <button className={"danger-button"} onClick={props.logOut}>Yes, log me out!</button>
                <button className={"secondary-button"} onClick={props.closeModal}>Cancel</button>
            </section>
        </Modal>
    );
}
