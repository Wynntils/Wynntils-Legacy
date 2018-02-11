package cf.wynntils.core.framework.rendering.colors;

import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Function;

/** ComplexColors
 * houses any colors with complexity,
 * each complex color takes up a formula
 * that takes a long and generates the
 * color based on that long
 */
public enum ComplexColors {
    Rainbow((l)->{ //Rainbow spectrum
        throw new NotImplementedException("Need to create the formula -SHCM");//TODO
    }),
    Fire((l)->{ //Fire colors spectrum
        throw new NotImplementedException("Need to create the formula -SHCM");//TODO
    });

    private Function<Long,CustomColor> colorFunction;
    ComplexColors(Function<Long,CustomColor> colorFunction) {
        this.colorFunction = colorFunction;
    }

    /** CustomColor createColor(long colorValue)
     * Generates the color based on it's formula using
     * colorValue as the formula's input value
     *
     * @param colorValue input value for the formula
     * @return formula's output color
     */
    public CustomColor createColor(long colorValue) {
        return this.colorFunction.apply(colorValue);
    }

    /** CustomColor createColor(float multiplier)
     * Generates the color based on it's formula using
     * [multiplier * system's currentTimeMillis] as the
     * formula's input value
     *
     * @param multiplier multiplier for the current system time millis
     * @param offset offset for the value that's inserted to the function
     * @return formula's output color
     */
    public CustomColor createColor(float multiplier, long offset) {
        return createColor((long)(multiplier*System.currentTimeMillis()) + offset);
    }
}