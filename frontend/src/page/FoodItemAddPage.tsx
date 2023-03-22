import FoodItemForm from "../component/FoodItemForm";

export default function FoodItemAddPage() {
    return (
        <main className={"form"}>
            <h1>Add a new Item</h1>
            <FoodItemForm action={"add"}/>
        </main>
    );
}