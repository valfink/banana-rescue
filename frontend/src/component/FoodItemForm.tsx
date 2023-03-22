import React, {ChangeEvent, FormEvent, useContext, useEffect, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCamera, faLocationDot, faQuoteLeft, faTrainSubway, faUtensils} from '@fortawesome/free-solid-svg-icons';
import axios from "axios";
import {useNavigate} from "react-router-dom";
import moment from "moment";
import {UserContext} from "../context/UserContext";
import {SetAppIsLoadingContext} from "../context/SetAppIsLoadingContext";

export default function FoodItemForm() {
    const initialFormState = {
        title: "",
        location: "",
        pickupUntil: "",
        consumeUntil: "",
        description: ""
    };
    const [formData, setFormData] = useState(initialFormState);
    const [photo, setPhoto] = useState<File | null>(null)
    const [formError, setFormError] = useState("");
    const navigate = useNavigate();
    const {redirectIfNotSignedIn} = useContext(UserContext);
    const setAppIsLoading = useContext(SetAppIsLoadingContext);

    function setInputTypeToDateOrTime(e: React.FocusEvent<HTMLInputElement>) {
        if (e.target.dataset.hasFocus === "false") {
            e.target.dataset.hasFocus = "between";
            e.target.type = e.target.dataset.onFocusType || "text";
            e.target.blur();
            e.target.readOnly = false;
            e.target.focus();
            e.target.dataset.hasFocus = "true";
        }
    }

    function resetInputTypeToText(e: React.FocusEvent<HTMLInputElement>) {
        if (!e.target.value && e.target.dataset.hasFocus === "true") {
            e.target.dataset.hasFocus = "false";
            e.target.type = "text";
            e.target.readOnly = true;
        }
    }

    function handleInputChange(e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        setFormData(oldData => ({
            ...oldData,
            [e.target.name]: e.target.value
        }))
    }

    function handleFileChange(e: ChangeEvent<HTMLInputElement>) {
        if (e.target.files && e.target.files.length > 0) {
            setPhoto(e.target.files[0]);
            e.target.classList.remove("dont-display");
        }
    }

    function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setAppIsLoading(true);
        setFormError("");
        let url = "/api/food",
            payload = new FormData(),
            navigateTo = "/",
            navigateOptions = {state: {successMessage: "Successfully registered."}};
        if (photo) {
            payload.set("photo", photo);
        }
        payload.set("form", new Blob([JSON.stringify({
            ...formData,
            pickupUntil: moment(formData.pickupUntil),
            consumeUntil: moment(formData.consumeUntil)
        })], {
            type: "application/json"
        }));
        axios.post(url, payload)
            .then(() => {
                navigate(navigateTo, navigateOptions);
            })
            .catch(err => {
                console.error(err);
                setFormError(err.response.data.error || err.response.data.message);
            })
            .finally(() => {
                setAppIsLoading(false);
            });
    }

    useEffect(() => {
        redirectIfNotSignedIn();
    })

    return (
        <form onSubmit={handleFormSubmit}>
            {formError && <div className={"form-error"}>Error: {formError}</div>}
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faQuoteLeft}/>
                <input type={"text"} name={"title"} placeholder={"Title"} required={true} value={formData.title}
                       onChange={handleInputChange}/>
            </div>
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faCamera}/>
                <input type={"file"} accept={"image/jpeg, image/png"} id={"photo"} name={"photo"}
                       className={"dont-display"} onChange={handleFileChange}/>
                <label htmlFor={"photo"} className={"input-replacement"}>Photo (optional)</label>
            </div>
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faLocationDot}/>
                <input type={"text"} name={"location"} placeholder={"Location"} required={true}
                       value={formData.location} onChange={handleInputChange}/>
            </div>
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faTrainSubway}/>
                <input type={"text"} name={"pickupUntil"} placeholder={"Pickup until"} readOnly={true} required={true}
                       value={formData.pickupUntil} onChange={handleInputChange}
                       onFocus={setInputTypeToDateOrTime} onBlur={resetInputTypeToText}
                       data-on-focus-type={"datetime-local"} data-has-focus={"false"}/>
            </div>
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faUtensils}/>
                <input type={"text"} name={"consumeUntil"} placeholder={"Consume until"} readOnly={true}
                       required={true} value={formData.consumeUntil} onChange={handleInputChange}
                       onFocus={setInputTypeToDateOrTime} onBlur={resetInputTypeToText}
                       data-on-focus-type={"datetime-local"} data-has-focus={"false"}/>
            </div>
            <textarea name={"description"} placeholder={"Description & Comments"} value={formData.description}
                      required={true} onChange={handleInputChange}/>
            <button type={"submit"}>Add Item</button>
        </form>
    );
}