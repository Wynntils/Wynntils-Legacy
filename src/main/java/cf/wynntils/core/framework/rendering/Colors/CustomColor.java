package cf.wynntils.core.framework.rendering.Colors;



import com.wynndevs.ModCore;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Function;


/** CustomColor
 * will represent color or complex colors
 * in a more efficient way than awt's Color or minecraft's color ints.
 */
public class CustomColor {
    public float
            r, // The RED   value of the color(0.0f -> 1.0f)
            g, // The GREEN value of the color(0.0f -> 1.0f)
            b, // The BLUE  value of the color(0.0f -> 1.0f)
            a; // The ALPHA value of the color(0.0f -> 1.0f)

    public CustomColor(float r, float g, float b) { this(r,g,b,1.0f); }
    public CustomColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public CustomColor(String hex) throws Exception {
        if(hex.length() != 8 && hex.length() != 6) throw new Exception("String length is neither 8 or 6, Cannot proceed.");
        r = ((float)Integer.parseInt(hex.substring(0,2),16)/255f);
        g = ((float)Integer.parseInt(hex.substring(2,4),16)/255f);
        b = ((float)Integer.parseInt(hex.substring(4,6),16)/255f);
        if(hex.length() == 6) {
            a = 1f;
        } else {
            a = ((float)Integer.parseInt(hex.substring(6,8),16)/255f);
        }
    }

    /** void ApplyColor()
     * will set the color to OpenGL's active color
     */
    public void ApplyColor() {
        GlStateManager.color(r,g,b,a);
    }


    @Deprecated
    public int getRGB() {return new java.awt.Color(r,g,b,a).getRGB();}
}
