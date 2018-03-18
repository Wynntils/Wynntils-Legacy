package cf.wynntils.core.framework.rendering.colors;



import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;


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

    public CustomColor(String hex) {
        if(hex.length() != 8 && hex.length() != 6) {
            //TODO decide what to make here
            r = 1; g = 1; b = 1;
            return;
        }
        r = ((float)Integer.parseInt(hex.substring(0,2),16)/255f);
        g = ((float)Integer.parseInt(hex.substring(2,4),16)/255f);
        b = ((float)Integer.parseInt(hex.substring(4,6),16)/255f);
        if(hex.length() == 6) {
            a = 1f;
        } else {
            a = ((float)Integer.parseInt(hex.substring(6,8),16)/255f);
        }
    }

    /** void applyColor()
     * will set the color to OpenGL's active color
     */
    public void applyColor() {
        GlStateManager.color(r,g,b,a);
    }

    public static CustomColor fromHSV(float h, float s, float v, float a) {
        if(v == 0f) {
            return new CustomColor(0.0f,0.0f,0.0f,a);
        } else if(s == 0f) {
            return new CustomColor(v,v,v,a);
        } else {
            h = h % 1f;

            float vh = h * 6;
            if (vh == 6)
                vh = 0;
            int vi = MathHelper.fastFloor((double) vh);
            float v1 = v * (1 - s);
            float v2 = v * (1 - s * (vh - vi));
            float v3 = v * (1 - s * (1 - (vh - vi)));

            switch(vi) {
                case 0:
                    return new CustomColor(v,v3,v1,a);
                case 1:
                    return new CustomColor(v2,v,v1,a);
                case 2:
                    return new CustomColor(v1,v,v3,a);
                case 3:
                    return new CustomColor(v1,v2,v,a);
                case 4:
                    return new CustomColor(v3,v1,v,a);
                default:
                    return new CustomColor(v,v1,v2,a);
            }
        }
    }
}
