import React, {ChangeEvent, FormEvent, useContext, useEffect, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCamera, faLocationDot, faQuoteLeft, faTrainSubway, faUtensils} from '@fortawesome/free-solid-svg-icons';
import {Link, useNavigate} from "react-router-dom";
import {UserContext, UserContextType} from "../context/UserContext";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {FoodItemFormData} from "../model/FoodItemFormData";
import {deletePhotoFromFoodItem, postNewFoodItem, updateFoodItem} from "../util/foodItemRequests";
import {FoodItem} from "../model/FoodItem";
import moment from "moment";
import "./FoodItemForm.css";
import DeletionWarningScreen from "../modal/DeletionWarningScreen";

type FoodItemFormProps = {
    action: "add" | "edit";
    oldFoodItem?: FoodItem;
}

export default function FoodItemForm(props: FoodItemFormProps) {
    const initialFormState = {
        title: props.oldFoodItem?.title || "",
        location: props.oldFoodItem?.location || "",
        pickupUntil: props.oldFoodItem?.pickupUntil ? moment(props.oldFoodItem?.pickupUntil).format("YYYY-MM-DDTHH:mm") : "",
        consumeUntil: props.oldFoodItem?.consumeUntil ? moment(props.oldFoodItem?.consumeUntil).format("YYYY-MM-DDTHH:mm") : "",
        description: props.oldFoodItem?.description || ""
    };
    const [formData, setFormData] = useState<FoodItemFormData>(initialFormState);
    const [photo, setPhoto] = useState<File | null>(null)
    const [oldPhotoUri, setOldPhotoUri] = useState<string | undefined>(props.oldFoodItem?.photoUri);
    const [formError, setFormError] = useState("");
    const [showDeletePhotoWarning, setShowDeletePhotoWarning] = useState(false);
    const navigate = useNavigate();
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;

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

    function handleClickOldImage() {
        setShowDeletePhotoWarning(true);
    }

    function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setFormError("");

        if (props.action === "add") {
            let navigateOptions = {state: {successMessage: "Food item successfully added."}};

            postNewFoodItem(formData, photo, setAppIsLoading)
                .then(foodItemResponse => {
                    navigate(`/food/${foodItemResponse.id}`, navigateOptions);
                })
                .catch(setFormError);

        } else {
            if (!props.oldFoodItem?.id) {
                setFormError("Id not specified!");
            } else {
                let navigateOptions = {state: {successMessage: "Food item successfully updated."}};

                updateFoodItem(props.oldFoodItem.id, formData, photo, setAppIsLoading)
                    .then(foodItemResponse => {
                        navigate(`/food/${foodItemResponse.id}`, navigateOptions);
                    })
                    .catch(setFormError);
            }

        }
    }

    function handleCloseModalClick() {
        setShowDeletePhotoWarning(false);
    }

    function handleDeletePhotoClick() {
        deletePhotoFromFoodItem(props.oldFoodItem?.id || "", setAppIsLoading)
            .then(() => setOldPhotoUri(undefined))
            .catch(setFormError)
            .finally(() => {
                setShowDeletePhotoWarning(false)
            });
    }

    useEffect(() => {
        redirectIfNotSignedIn();
    }, [redirectIfNotSignedIn]);

    return (
        <form onSubmit={handleFormSubmit}>
            {props.action === "edit" &&
                <DeletionWarningScreen closeModal={handleCloseModalClick} deleteItem={handleDeletePhotoClick}
                                       modalIsOpen={showDeletePhotoWarning} itemDescriptor={"photo"}/>}
            {formError && <div className={"form-error"}>Error: {formError}</div>}
            <main>
                <div className={"input-with-icon"}>
                    <FontAwesomeIcon icon={faQuoteLeft}/>
                    <input type={"text"} name={"title"} placeholder={"Title"} required={true} value={formData.title}
                           onChange={handleInputChange}/>
                </div>
                {oldPhotoUri
                    ? <img className={"old-item-image"} src={oldPhotoUri} alt={"Click to deleteπ"}
                           onClick={handleClickOldImage}/>
                    : <div className={"input-with-icon"}>
                        <FontAwesomeIcon icon={faCamera}/>
                        <input type={"file"} accept={"image/jpeg, image/png"} id={"photo"} name={"photo"}
                               className={"dont-display"} onChange={handleFileChange}/>
                        <label htmlFor={"photo"} className={"input-replacement"}>Photo (optional)</label>
                    </div>
                }
                <div className={"input-with-icon"}>
                    <FontAwesomeIcon icon={faLocationDot}/>
                    <input type={"text"} name={"location"} placeholder={"Location"} required={true}
                           value={formData.location} onChange={handleInputChange}/>
                </div>
                <div className={"input-with-icon"}>
                    <FontAwesomeIcon icon={faTrainSubway}/>
                    <input type={!formData.pickupUntil ? "text" : "datetime-local"} name={"pickupUntil"}
                           placeholder={"Pickup until"} readOnly={true} required={true}
                           value={formData.pickupUntil} onChange={handleInputChange}
                           onFocus={setInputTypeToDateOrTime} onBlur={resetInputTypeToText}
                           data-on-focus-type={"datetime-local"} data-has-focus={"false"}/>
                </div>
                <div className={"input-with-icon"}>
                    <FontAwesomeIcon icon={faUtensils}/>
                    <input type={!formData.consumeUntil ? "text" : "datetime-local"} name={"consumeUntil"}
                           placeholder={"Consume until"} readOnly={true}
                           required={true} value={formData.consumeUntil} onChange={handleInputChange}
                           onFocus={setInputTypeToDateOrTime} onBlur={resetInputTypeToText}
                           data-on-focus-type={"datetime-local"} data-has-focus={"false"}/>
                </div>
                <textarea name={"description"} placeholder={"Description & Comments"} value={formData.description}
                          required={true} onChange={handleInputChange}/>
            </main>
            <button type={"submit"}>
                {props.action === "add" ? "Add Item" : "Update Item"}
            </button>
            <Link to={props.action === "add" ? "/" : `/food/${props.oldFoodItem?.id}`}
                  className={"secondary-button"}>Cancel</Link>
        </form>
    );
}
