import "./DeletionWarningScreen.css";
import Modal from 'react-modal';

type DeletionWarningScreenProps = {
    itemName: string;
    modalIsOpen: boolean;
    closeModal: () => void;
    deleteItem: () => void;
}

export default function DeletionWarningScreen(props: DeletionWarningScreenProps) {
    Modal.setAppElement('#root');
    return (
        <Modal
            isOpen={props.modalIsOpen}
            bodyOpenClassName={"has-open-modal"}
            htmlOpenClassName={"has-open-modal"}
            className={"modal delete-image-warning"}
        >
            <h1>ATTENTION</h1>
            <h2>
                Do you want to delete the {props.itemName}?<br/>
                This cannot be undone!
            </h2>
            <section className={"buttons"}>
                <button className={"danger-button"} onClick={props.deleteItem}>Yes, delete it!</button>
                <button className={"secondary-button"} onClick={props.closeModal}>Cancel</button>
            </section>
        </Modal>
    );
}
