import Modal from "react-modal";
import {useContext} from "react";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {Coordinate} from "../model/Coordinate";
import FoodItemMap from "../component/FoodItemMap";
import useCoordinate from "../hook/useCoordinate";

type SetCoordinateScreenProps = {
    locationTitle: string;
    modalIsOpen: boolean;
    submit: (withCoordinate: Coordinate) => void;
    closeModal: () => void;
}
export default function SetCoordinateScreen(props: SetCoordinateScreenProps) {
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const {error, foundCoordinate} = useCoordinate(props.locationTitle, props.modalIsOpen, setAppIsLoading);

    Modal.setAppElement('#root');

    function handleSubmitButtonClick() {
        props.submit(foundCoordinate);
    }

    return (
        <Modal
            isOpen={props.modalIsOpen}
            bodyOpenClassName={"has-open-modal"}
            htmlOpenClassName={"has-open-modal"}
            className={"modal"}
        >
            <h1>Choose Location</h1>
            <h2>
                Please approve the position for <br/>
                <center>{props.locationTitle}</center>
            </h2>
            {!error &&
                <>If the position is not correct, please change the location field in the form.
                    <FoodItemMap location={{title: props.locationTitle, coordinate: foundCoordinate}}/>
                    <section className={"buttons"}>
                        <button className={"primary-button-button"} onClick={handleSubmitButtonClick}>Submit</button>
                        <button className={"secondary-button"} onClick={props.closeModal}>Change Location
                        </button>
                    </section>
                </>}
            {error &&
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
