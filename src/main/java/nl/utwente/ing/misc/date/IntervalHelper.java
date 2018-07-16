package nl.utwente.ing.misc.date;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * The IntervalHelper class.
 * Used to generate intervals and compare dates.
 *
 * @author Daan Kooij
 */
public class IntervalHelper {

    /**
     * Method used to generate a certain number of intervals with certain size.
     * The generation of intervals is done by taking the current datetime and and putting the resulting edges of the
     * intervals in an array in the form of LocalDateTime objects.
     * <p>
     * Example: suppose the current date is 13 May 2018 and you want to generate five intervals with sizes of one day
     * each. Then you specify intervalPeriod to be IntervalPeriod.DAY and amount to be 5. The returned array will then
     * be: [8 May 2018, 9 May 2018, 10 May 2018, 11 May 2018, 12 May 2018, 13 May 2018].
     * <p>
     * Note that the size of the array is equal to the amount of intervals plus one.
     *
     * @param intervalPeriod The size of the intervals to be generated.
     * @param amount         The amount of intervals to be generated.
     * @return An array containing LocalDateTime objects representing the requested intervals.
     */
    public static LocalDateTime[] getIntervals(IntervalPeriod intervalPeriod, int amount) {
        LocalDateTime[] intervals = new LocalDateTime[amount + 1];
        intervals[amount] = LocalDateTime.now(ZoneOffset.UTC);

        if (intervalPeriod == IntervalPeriod.YEAR) {
            for (int i = amount - 1; i >= 0; i--) {
                intervals[i] = intervals[i + 1].minusYears(1);
            }
        } else if (intervalPeriod == IntervalPeriod.MONTH) {
            for (int i = amount - 1; i >= 0; i--) {
                intervals[i] = intervals[i + 1].minusMonths(1);
            }
        } else if (intervalPeriod == IntervalPeriod.WEEK) {
            for (int i = amount - 1; i >= 0; i--) {
                intervals[i] = intervals[i + 1].minusWeeks(1);
            }
        } else if (intervalPeriod == IntervalPeriod.DAY) {
            for (int i = amount - 1; i >= 0; i--) {
                intervals[i] = intervals[i + 1].minusDays(1);
            }
        } else {
            for (int i = amount - 1; i >= 0; i--) {
                intervals[i] = intervals[i + 1].minusHours(1);
            }
        }

        return intervals;
    }

    /**
     * Method used to convert a LocalDateTime object to a String, in the format that the DPA uses.
     *
     * @param localDateTime The LocalDateTime object that should be converted to a String.
     * @return A String object in the format that the DPA uses that reflects the converted LocalDateTime object.
     */
    public static String dateToString(LocalDateTime localDateTime) {
        return localDateTime.toString() + "Z";
    }

    /**
     * Method used to check whether the date contained in a certain LocalDateTime object is smaller than the date
     * contained in a certain String object.
     *
     * @param date1 The LocalDateTime object for which it will be checked if the contained date is smaller than the
     *              contained date in s.
     * @param s     The String object for which it will be checked whether the contained date is bigger than or equal
     *              to the contained date in date1.
     * @return A boolean indicating whether the date contained in date1 is smaller than the date contained in s.
     */
    public static boolean isSmallerThan(LocalDateTime date1, String s) {
        LocalDateTime date2 = LocalDateTime.parse(s.split("Z")[0]);
        return date1.compareTo(date2) < 0;
    }

}
