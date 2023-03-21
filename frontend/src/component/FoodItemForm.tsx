import React, {ChangeEvent, FormEvent, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faLocationDot, faQuoteLeft, faTrainSubway, faUtensils} from '@fortawesome/free-solid-svg-icons';
import axios from "axios";
import {useNavigate} from "react-router-dom";
import moment from "moment";

export default function FoodItemForm() {
    const initialFormState = {
        title: "",
        location: "",
        pickup_until: "",
        consume_until: "",
        description: ""
    };
    const [formData, setFormData] = useState(initialFormState);
    const [formError, setFormError] = useState("");
    const navigate = useNavigate();

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

    function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setFormError("");
        let url = "/api/food",
            data = {
                ...formData,
                pickup_until: moment(formData.pickup_until),
                consume_until: moment(formData.consume_until)
            },
            navigateTo = "/",
            navigateOptions = {state: {successMessage: "Successfully registered."}};
        axios.post(url, data)
            .then(() => {
                navigate(navigateTo, navigateOptions);
            })
            .catch(err => {
                console.error(err);
                setFormError(err.response.data.error || err.response.data.message);
            });
    }

    return (
        <form onSubmit={handleFormSubmit}>
            {formError && <div className={"form-error"}>Error: {formError}</div>}
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faQuoteLeft}/>
                <input type={"text"} name={"title"} placeholder={"Title"} required={true} value={formData.title}
                       onChange={handleInputChange}/>
            </div>
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faLocationDot}/>
                <input type={"text"} name={"location"} placeholder={"Location"} required={true}
                       value={formData.location} onChange={handleInputChange}/>
            </div>
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faTrainSubway}/>
                <input type={"text"} name={"pickup_until"} placeholder={"Pickup until"} readOnly={true} required={true}
                       value={formData.pickup_until} onChange={handleInputChange}
                       onFocus={setInputTypeToDateOrTime} onBlur={resetInputTypeToText}
                       data-on-focus-type={"datetime-local"} data-has-focus={"false"}/>
            </div>
            <div className={"input-with-icon"}>
                <FontAwesomeIcon icon={faUtensils}/>
                <input type={"text"} name={"consume_until"} placeholder={"Consume until"} readOnly={true}
                       required={true} value={formData.consume_until} onChange={handleInputChange}
                       onFocus={setInputTypeToDateOrTime} onBlur={resetInputTypeToText}
                       data-on-focus-type={"datetime-local"} data-has-focus={"false"}/>
            </div>
            <textarea name={"description"} placeholder={"Description & Comments"} value={formData.description}
                      required={true} onChange={handleInputChange}/>
            <button type={"submit"}>Add Item</button>
        </form>
    );
}