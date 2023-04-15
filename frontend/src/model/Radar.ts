import {Coordinate} from "./Coordinate";
import {FoodItem} from "./FoodItem";

export type Radar = {
    center: Coordinate;
    radiusInMeters: number;
    foodItems?: FoodItem[];
}
