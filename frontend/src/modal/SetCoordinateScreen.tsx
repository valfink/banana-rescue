import Modal from "react-modal";
import {useContext, useEffect, useState} from "react";
import {OpenStreetMapsSearchResult} from "../model/OpenStreetMapsSearchResult";
import axios from "axios";
import toast from "react-hot-toast";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {Coordinate} from "../model/Coordinate";
import FoodItemMap from "../component/FoodItemMap";

type SetCoordinateScreenProps = {
    locationTitle: string;
    modalIsOpen: boolean;
    submit: (withCoordinate: Coordinate) => void;
    closeModal: () => void;
}
export default function SetCoordinateScreen(props: SetCoordinateScreenProps) {
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const [selectedCoordinate, setSelectedCoordinate] = useState<Coordinate>({latitude: 0, longitude: 0});
    const [error, setError] = useState(false);

    Modal.setAppElement('#root');

    useEffect(() => {
        if (props.modalIsOpen && selectedCoordinate.latitude === 0) {
            setAppIsLoading(oldValue => oldValue + 1);
            axios.get(`https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=${props.locationTitle}`)
                .then(res => res.data as OpenStreetMapsSearchResult[])
                .then((results) => {
                    if (results.length > 0) {
                        setSelectedCoordinate({latitude: results[0].lat, longitude: results[0].lon});
                    } else {
                        setError(true);
                    }
                })
                .catch(err => {
                    console.error(err);
                    toast.error(`Could not fetch location search results 😱\n${err.response?.data.error || err.response?.data.message || err.message}`);
                })
                .finally(() => {
                    setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
                });
        }
    }, [props.locationTitle, props.modalIsOpen, selectedCoordinate.latitude, setAppIsLoading]);

    function handleSubmitButtonClick() {
        props.submit(selectedCoordinate);
    }

    function handleCancelButtonClick() {
        props.closeModal();
        setSelectedCoordinate({latitude: 0, longitude: 0});
        setError(false);
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
                    <FoodItemMap location={{title: props.locationTitle, coordinate: selectedCoordinate}}/>
                    <section className={"buttons"}>
                        <button className={"primary-button-button"} onClick={handleSubmitButtonClick}>Submit</button>
                        <button className={"secondary-button"} onClick={handleCancelButtonClick}>Change Location
                        </button>
                    </section>
                </>}
            {error &&
                <><h2 className={"error"}>Error</h2>
                    Sorry, but we could not find any place for the location you entered. Please try again with a
                    different term.
                    <section className={"buttons"}>
                        <button className={"secondary-button"} onClick={handleCancelButtonClick}>Change Location
                        </button>
                    </section>
                </>}
        </Modal>
    );
}
