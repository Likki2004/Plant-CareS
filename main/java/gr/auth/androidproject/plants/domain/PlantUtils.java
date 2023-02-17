package gr.auth.androidproject.plants.domain;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Collection of methods that calculate values for a plant
 */
public class PlantUtils {

    /**
     * Calculates the time until the plant needs to be watered again
     *
     * @param plant the plant
     * @return time to the next watering
     */
    public static Duration timeToNextWatering(Plant plant) {
        // get time elapsed since last watering
        Duration timeSinceLast = Duration.between(plant.getLastWatered(), LocalDateTime.now());

        // and subtract the watering interval from it
        return plant.getWateringInterval().minus(timeSinceLast);
    }


    /**
     * Calculates the plant's age using current date/time and the plant's birthday
     *
     * @param plant the plant
     * @return the age of the plant or null if the plant does not have a birthday
     */
    public static Duration calculateAge(Plant plant) {
        return plant.getBirthday()
                .map((birthday -> Duration.between(birthday, LocalDateTime.now())))
                .orElse(null);
    }
}
