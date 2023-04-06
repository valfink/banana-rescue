import Modal from "react-modal";
import {useContext, useEffect, useState} from "react";
import {OpenStreetMapsSearchResult} from "../model/OpenStreetMapsSearchResult";
import axios from "axios";
import toast from "react-hot-toast";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {Coordinate} from "../model/Coordinate";

type SetCoordinateScreenProps = {
    locationTitle: string;
    modalIsOpen: boolean;
    submit: (withCoordinate: Coordinate) => void;
    closeModal: () => void;
}
export default function SetCoordinateScreen(props: SetCoordinateScreenProps) {
    const [searchResults, setSearchResults] = useState<OpenStreetMapsSearchResult[]>([]);
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const [selectedCoordinate, setSelectedCoordinate] = useState<Coordinate>({latitude: 0, longitude: 0});

    Modal.setAppElement('#root');

    useEffect(() => {
        if (props.modalIsOpen && selectedCoordinate.latitude === 0) {
            setAppIsLoading(oldValue => oldValue + 1);
            axios.get(`https://nominatim.openstreetmap.org/search?format=jsonv2&limit=3&q=${props.locationTitle}`)
                .then(res => res.data as OpenStreetMapsSearchResult[])
                .then((results) => {
                    setSearchResults(results);
                    setSelectedCoordinate({latitude: searchResults[0].lat, longitude: searchResults[0].lon});
                })
                .catch(err => {
                    console.error(err);
                    toast.error(`Could not fetch location search results 😱\n${err.response.data.error || err.response.data.message}`);
                })
                .finally(() => {
                    setAppIsLoading(oldValue => Math.max(0, oldValue - 1));
                });
        }
    }, [props.locationTitle, props.modalIsOpen, searchResults, selectedCoordinate.latitude, setAppIsLoading]);

    function handleSubmitButtonClick() {
        props.submit(selectedCoordinate);
    }

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
            <ul>
                {searchResults.map(result => <li key={result.place_id}>{result.display_name}</li>)}
            </ul>
            <section className={"buttons"}>
                <button className={"primary-button-button"} onClick={handleSubmitButtonClick}>Submit</button>
                <button className={"secondary-button"} onClick={props.closeModal}>Cancel</button>
            </section>
        </Modal>
    );
}