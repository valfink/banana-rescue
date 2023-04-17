import Modal from "react-modal";
import {Coordinate} from "../model/Coordinate";
import BananaMap from "../component/BananaMap";

type SetCoordinateScreenProps = {
    locationTitle: string;
    coordinate: Coordinate;
    error: boolean;
    modalIsOpen: boolean;
    submit: () => void;
    closeModal: () => void;
}
export default function SetCoordinateScreen(props: SetCoordinateScreenProps) {

    Modal.setAppElement('#root');

    function handleSubmitButtonClick() {
        props.submit();
    }

    return (
        <Modal
            isOpen={props.modalIsOpen}
            bodyOpenClassName={"has-open-modal"}
            htmlOpenClassName={"has-open-modal"}
            className={"modal"}
            overlayClassName={"overlay"}
        >
            <h1>Choose Location</h1>
            <h2>
                Please approve the position for <br/>
                <center>{props.locationTitle}</center>
            </h2>
            {!props.error &&
                <>If the position is not correct, please change the location field in the form.
                    <BananaMap location={{title: props.locationTitle, coordinate: props.coordinate}}/>
                    <section className={"buttons"}>
                        <button className={"primary-button-button"} onClick={handleSubmitButtonClick}>Submit</button>
                        <button className={"secondary-button"} onClick={props.closeModal}>Change Location
                        </button>
                    </section>
                </>}
            {props.error &&
                <><h2 className={"error"}>Error</h2>
                    Sorry, but we could not find any place for the location you entered. Please try again with a
                    different term.
                    <section className={"buttons"}>
                        <button className={"secondary-button"} onClick={props.closeModal}>Change Location
                        </button>
                    </section>
                </>}
        </Modal>
    );
}
