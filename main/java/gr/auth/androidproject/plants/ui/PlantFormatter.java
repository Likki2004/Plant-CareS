package gr.auth.androidproject.plants.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import gr.auth.androidproject.plants.R;
import gr.auth.androidproject.plants.domain.Plant;
import gr.auth.androidproject.plants.domain.PlantUtils;

/**
 * <p>
 * Read only wrapper for a plant that formats its content to be human readable <br>
 * </p>
 * <p>
 * It formats all the fields related to a plant and also calculates the time to next watering
 * </p>
 * <p>
 */
public class PlantFormatter {

    private static Bitmap DEFAULT_PHOTO_BITMAP = null;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT); // 5/14/21, 5:59 PM
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.SHORT); // 5/14/21

    private final Resources resources;
    /**
     * The {@link Plant} object being wrapped
     */
    private final Plant plant;

    public PlantFormatter(Context context, Plant plant) {
        this.resources = Objects.requireNonNull(context).getResources();
        this.plant = plant;

        if (Objects.isNull(DEFAULT_PHOTO_BITMAP)) {
            DEFAULT_PHOTO_BITMAP = BitmapFactory.decodeResource(resources, R.drawable.default_plant_image);
        }
    }

    /**
     * Checks if time elapsed between now and last watering is greater than the watering interval
     * and returns the appropriate message
     *
     * @return string message informing the user of when to water the plant
     */
    public String timeToNextWatering() {
        Duration timeToNext = PlantUtils.timeToNextWatering(plant);

        if (timeToNext.isNegative() || timeToNext.isZero()) {
            return resources.getString(R.string.formatter_water_now_message);
        } else if (timeToNext.minus(Duration.ofMinutes(1)).isNegative()) {
            return resources.getString(R.string.time_to_next_under_minute);
        }

        return formattedDuration(timeToNext);
    }

    /**
     * Returns a formatted string displaying the plant's age
     *
     * @return the plants age in a human readable format
     * @implSpec this implementation displays the plant's age with precision of days
     */
    public String age() {
        Duration age = PlantUtils.calculateAge(plant);
        if (Objects.isNull(age)) {
            // return appropriate message if no age
            return resources.getString(R.string.plant_no_age_message);

        }

        if (age.minusDays(1).minusSeconds(1).isNegative()) {
            // plant is less than one day old
            return resources.getString(R.string.plant_very_young_message);
        }

        // don't care about age in hours or less
        return formattedDuration(age, TimespanUnits.DAYS);
    }

    public long id() {
        return plant.getId();
    }

    public String name() {
        return plant.getName();
    }

    public String birthday() {
        if (plant.getBirthday().isPresent()) {
            return formattedDateTime(plant.getBirthday().get());
        }
        return resources.getString(R.string.plant_no_age_message);
    }

    public String lastWatered() {
        return formattedDateTime(plant.getLastWatered());
    }

    public String wateringInterval() {
        return formattedDuration(plant.getWateringInterval());
    }

    public Bitmap photo() {
        if (plant.getPhoto().isPresent()) {
            byte[] blob = plant.getPhoto().get();
            Bitmap nullableBitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            if (Objects.nonNull(nullableBitmap)) { // no parse error
                return nullableBitmap;
            }
        }
        return PlantFormatter.DEFAULT_PHOTO_BITMAP;
    }

    private String formattedDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private String formattedDate(LocalDateTime date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formats the Duration object to an app specific standard
     *
     * @param duration the duration to be formatted
     * @return string formatted representation of the duration
     * @implSpec This implementation formats the duration as ".. years .. months .. days .. hours .. minutes", omitting
     * any zeros
     */
    private String formattedDuration(Duration duration, TimespanUnits minUnit) {
        StringBuilder result = new StringBuilder();

        // sort the time durations in descending order (YEAR, MONTH etc)
        final List<TimespanUnits> timeUnits = Arrays.stream(TimespanUnits.values())
                .filter(unit -> unit.minutesInThis >= minUnit.minutesInThis) // discard lower than min
                .sorted(TimespanUnits.descendingOrder) // sort descending
                .collect(Collectors.toCollection(ArrayList::new));

        // get their display string resources
        final String[] labelsSingular = resources.getStringArray(R.array.duration_formatter_YMDhm_labels_singular);
        final String[] labelsPlural = resources.getStringArray(R.array.duration_formatter_YMDhm_labels_plural);

        if (labelsSingular.length != labelsPlural.length || labelsSingular.length < timeUnits.size()) {
            // resource singular-plural matching problem or there are not enough labels
            throw new IllegalStateException("Label/TimespanUnit matching problem");
        }

        Duration durationLeft = Duration.from(duration);
        boolean addLeadingSpace = false; // to separate next from previous value

        for (int i = 0; i < timeUnits.size(); i++) {
            TimespanUnits currentTimespanUnit = timeUnits.get(i);
            long durationLeftMinutes = durationLeft.toMinutes();
            long currentUnitsLeft = currentTimespanUnit.fromMinutes(durationLeftMinutes);
            if (currentUnitsLeft == 0) continue; // nothing to add for this unit, continue

            // add the current time unit number and caption
            if (addLeadingSpace) result.append(' ');
            result.append(currentUnitsLeft);
            result.append(' ');
            result.append(currentUnitsLeft == 1 ? labelsSingular[i] : labelsPlural[i]);

            // update the duration left
            durationLeft = durationLeft.minusMinutes(
                    currentTimespanUnit.toMinutes(currentUnitsLeft));

            if (durationLeft.toMinutes() <= 0) {
                break;
            }
            addLeadingSpace = true;
        }

        return result.toString();
    }

    /**
     * Version of {@link #formattedDuration(Duration, TimespanUnits)} with no lower bound
     */
    private String formattedDuration(Duration duration) {
        return formattedDuration(duration, TimespanUnits.MINUTES);
    }

    /**
     * Handles representation and conversion of time units
     * <p>
     * Contains YEARS and MONTHS that are not included in {@link Duration} but not ms, ns etc
     */
    private enum TimespanUnits {

        YEARS(525_600), MONTHS(43_805), // assuming 30.42 day months
        DAYS(1440), HOURS(60), MINUTES(1);
        static final Comparator<TimespanUnits> descendingOrder = Comparator
                .comparingLong(TimespanUnits::getMinutesInThis)
                .reversed();

        /**
         * Number of minutes in one unit of the respective time unit
         */
        private final int minutesInThis;

        TimespanUnits(int numberOfMinutes) {
            minutesInThis = numberOfMinutes;
        }

        long fromMinutes(long minutes) {
            return minutes / minutesInThis;
        }

        /**
         * <p>
         * Converts a value from the scale of {@code this} to minutes.<br>
         * </p>
         * <p>
         * e.g. YEARS.toMinutes(2) will produce the number of minutes in 2 years
         * </p>
         *
         * @param scaledValue value in the scale of this object
         * @return equivalent value in minutes
         */
        long toMinutes(long scaledValue) {
            return scaledValue * minutesInThis;
        }

        int getMinutesInThis() {
            return minutesInThis;
        }
    }

    /**
     * Helper class that declares plant bitmap encoding format/quality
     */
    public static class BitmapEncoding {
        public static final Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        public static final int quality = 100;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID = ");
        builder.append(id());

        builder.append(", name = ");
        builder.append(name());


        builder.append(", birthday = ");
        if (plant.getBirthday().isPresent()) {
            builder.append(birthday());
        } else {
            builder.append("null");
        }

        builder.append(", last_watered = ");
        builder.append(lastWatered());

        builder.append(", watering_interval = ");
        builder.append(wateringInterval());


        builder.append(", has_photo = ");
        builder.append(plant.getPhoto().isPresent());

        return builder.toString();
    }
}
