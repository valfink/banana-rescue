import Modal from "react-modal";

type SetCoordinateScreenProps = {
    locationTitle: string;
    modalIsOpen: boolean;
    closeModal: () => void;
}
export default function SetCoordinateScreen(props: SetCoordinateScreenProps) {
    Modal.setAppElement('#root');
    return (
        <Modal
            isOpen={props.modalIsOpen}
            bodyOpenClassName={"has-open-modal"}
            htmlOpenClassName={"has-open-modal"}
            className={"modal deletion-warning"}
        >
            <h1>Choose Location</h1>
            <h2>
                Please select the appropriate position for <br/>
                <center>{props.locationTitle}</center>
            </h2>
            <section className={"buttons"}>
                {/*<button className={"danger-button"} onClick={props.deleteItem}>Yes, delete it!</button>*/}
                <button className={"secondary-button"} onClick={props.closeModal}>Cancel</button>
            </section>
        </Modal>
    );
}
