import "./LoadingScreen.css";
import Modal from "react-modal";

type LoadingScreenProps = {
    modalIsOpen: boolean;
}

export default function LoadingScreen(props: LoadingScreenProps) {
    return (
        <Modal
            isOpen={props.modalIsOpen}
            bodyOpenClassName={"has-open-modal"}
            htmlOpenClassName={"has-open-modal"}
            className={"modal loading-screen"}
            overlayClassName={"loading-screen-overlay"}
        >
            <span className={"spinner"}/>
        </Modal>
    );
}