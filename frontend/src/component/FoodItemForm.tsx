import React, {ChangeEvent, FormEvent, useContext, useEffect, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCamera, faLocationDot, faQuoteLeft, faTrainSubway, faUtensils} from '@fortawesome/free-solid-svg-icons';
import {Link, useNavigate} from "react-router-dom";
import {UserContext, UserContextType} from "../context/UserContext";
import {AppIsLoadingContext, AppIsLoadingContextType} from "../context/AppIsLoadingContext";
import {FoodItemFormData} from "../model/FoodItemFormData";
import {deleteFoodItem, deletePhotoFromFoodItem, postNewFoodItem, updateFoodItem} from "../hook/foodItemRequests";
import {FoodItem} from "../model/FoodItem";
import moment from "moment";
import "./FoodItemForm.css";
import DeletionWarningScreen from "../modal/DeletionWarningScreen";
import SetCoordinateScreen from "../modal/SetCoordinateScreen";
import useCoordinate from "../hook/useCoordinate";

type FoodItemFormProps = {
    action: "add" | "edit";
    oldFoodItem?: FoodItem;
}

export default function FoodItemForm(props: FoodItemFormProps) {
    const initialFormState: FoodItemFormData = {
        title: props.oldFoodItem?.title || "",
        locationTitle: props.oldFoodItem?.location.title || "",
        pickupUntil: props.oldFoodItem?.pickupUntil ? moment(props.oldFoodItem?.pickupUntil).format("YYYY-MM-DDTHH:mm") : "",
        consumeUntil: props.oldFoodItem?.consumeUntil ? moment(props.oldFoodItem?.consumeUntil).format("YYYY-MM-DDTHH:mm") : "",
        description: props.oldFoodItem?.description || ""
    };
    const [formData, setFormData] = useState<FoodItemFormData>(initialFormState);
    const [photo, setPhoto] = useState<File | null>(null)
    const [oldPhotoUri, setOldPhotoUri] = useState<string | undefined>(props.oldFoodItem?.photoUri);
    const [formError, setFormError] = useState("");
    const [showDeletePhotoModal, setShowDeletePhotoModal] = useState(false);
    const [showDeleteFoodItemModal, setShowDeleteFoodItemModal] = useState(false);
    const [showSetCoordinateModal, setShowSetCoordinateModal] = useState(false);
    const navigate = useNavigate();
    const {redirectIfNotSignedIn} = useContext(UserContext) as UserContextType;
    const {setAppIsLoading} = useContext(AppIsLoadingContext) as AppIsLoadingContextType;
    const {searchForCoordinates, foundCoordinate, coordinateError} = useCoordinate(setAppIsLoading);

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
        }));
    }

    function handleFileChange(e: ChangeEvent<HTMLInputElement>) {
        if (e.target.files && e.target.files.length > 0) {
            setPhoto(e.target.files[0]);
            e.target.classList.remove("dont-display");
        }
    }

    function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setFormError("");
        searchForCoordinates(formData.locationTitle || "");
        setShowSetCoordinateModal(true);
    }

    function handleCloseSetShowCoordinateModalClick() {
        setShowSetCoordinateModal(false);
    }

    function submitFoodItem() {
        const dataToSubmit: FoodItemFormData = {...formData};
        if (dataToSubmit.locationTitle) {
            dataToSubmit.location = {
                title: dataToSubmit.locationTitle,
                coordinate: foundCoordinate
            }
            delete dataToSubmit.locationTitle;
        }
        if (props.action === "add") {
            let navigateOptions = {state: {successMessage: "Food item successfully added."}};

            postNewFoodItem(dataToSubmit, photo, setAppIsLoading)
                .then(foodItemResponse => {
                    navigate(`/food/${foodItemResponse.id}`, navigateOptions);
                })
                .catch(setFormError);
        } else {
            if (!props.oldFoodItem?.id) {
                setFormError("Id not specified!");
            } else {
                let navigateOptions = {state: {successMessage: "Food item successfully updated."}};

                updateFoodItem(props.oldFoodItem.id, dataToSubmit, photo, setAppIsLoading)
                    .then(foodItemResponse => {
                        navigate(`/food/${foodItemResponse.id}`, navigateOptions);
                    })
                    .catch(setFormError);
            }
        }
    }

    function handleClickOldImage() {
        setShowDeletePhotoModal(true);
    }

    function handleCloseDeletePhotoModalClick() {
        setShowDeletePhotoModal(false);
    }

    function handleDeletePhotoClick() {
        deletePhotoFromFoodItem(props.oldFoodItem?.id || "", setAppIsLoading)
            .then(() => setOldPhotoUri(undefined))
            .catch(setFormError)
            .finally(() => {
                setShowDeletePhotoModal(false)
            });
    }

    function handleDeleteButtonClick() {
        setShowDeleteFoodItemModal(true);
    }

    function handleCloseDeleteFoodItemModalClick() {
        setShowDeleteFoodItemModal(false);
    }

    function handleDeleteFoodItemClick() {
        deleteFoodItem(props.oldFoodItem?.id || "", setAppIsLoading)
            .then(() => {
                navigate("/");
            })
            .finally(() => {
                setShowDeleteFoodItemModal(false)
            });
    }

    useEffect(redirectIfNotSignedIn, [redirectIfNotSignedIn]);

    return (
        <form onSubmit={handleFormSubmit}>
            {props.action === "edit" &&
                <>
                    <DeletionWarningScreen itemDescriptor={"photo"} modalIsOpen={showDeletePhotoModal}
                                           closeModal={handleCloseDeletePhotoModalClick}
                                           deleteItem={handleDeletePhotoClick}/>
                    <DeletionWarningScreen itemDescriptor={"food item"} modalIsOpen={showDeleteFoodItemModal}
                                           closeModal={handleCloseDeleteFoodItemModalClick}
                                           deleteItem={handleDeleteFoodItemClick}
                                           itemName={props.oldFoodItem?.title}/>
                </>}
            {formError && <div className={"form-error"}>Error: {formError}</div>}
            <SetCoordinateScreen locationTitle={formData.locationTitle || ""} modalIsOpen={showSetCoordinateModal}
                                 closeModal={handleCloseSetShowCoordinateModalClick}
                                 submit={submitFoodItem} coordinate={foundCoordinate} error={coordinateError}/>
            <section>
                <div className={"input-with-icon"}>
                    <FontAwesomeIcon icon={faQuoteLeft}/>
                    <input type={"text"} name={"title"} placeholder={"Title"} required={true} value={formData.title}
                           onChange={handleInputChange}/>
                </div>
                {oldPhotoUri
                    ? <img className={"image old-item-image"} src={oldPhotoUri} alt={"Click to delete"}
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
                    <input type={"text"} name={"locationTitle"} placeholder={"Location"} required={true}
                           value={formData.locationTitle} onChange={handleInputChange}/>
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
            </section>
            {props.action === "add"
                ? <>
                    <button type={"submit"}>Add Item</button>
                    <Link to={"/"} className={"secondary-button"}>Cancel</Link>
                </>
                : <>
                    <button type={"submit"}>Update Item</button>
                    <button type={"button"} className={"danger-button"} onClick={handleDeleteButtonClick}>Delete Item
                    </button>
                    <Link to={`/food/${props.oldFoodItem?.id}`} className={"secondary-button"}>Cancel</Link>
                </>
            }
        </form>
    );
}
