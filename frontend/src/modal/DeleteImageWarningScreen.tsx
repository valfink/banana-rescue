import "./DeleteImageWarningScreen.css";
import Modal from 'react-modal';

type DeleteImageWarningScreenProps = {
    modalIsOpen: boolean;
    closeModal: () => void;
    deletePhoto: () => void;
}

export default function DeleteImageWarningScreen(props: DeleteImageWarningScreenProps) {
    Modal.setAppElement('#root');
    return (
        // <section className={"delete-image-warning"}>
        //     WARNING
        // </section>
        <Modal
            isOpen={props.modalIsOpen}
            contentLabel="Example Modal"
            bodyOpenClassName={"has-open-modal"}
            htmlOpenClassName={"has-open-modal"}
            className={"modal delete-image-warning"}
        >
            <h1>ATTENTION</h1>
            <h2>
                Do you want to delete the image?<br/>
                This cannot be undone!
            </h2>
            <section className={"buttons"}>
                <button className={"danger"} onClick={props.deletePhoto}>Yes, delete it!</button>
                <button className={"secondary-button"} onClick={props.closeModal}>Cancel</button>
            </section>
        </Modal>
    );
}
