package jFacehard;

// these ugly routines are Java versions of BASIC routines for input and output
// they are not tested or complete, and are intended only to allow FACEHARD.java to
// become (eventually) a roughly backward compatible app to Nathan Okun's FACEHARD 0.55
public class Util {

    // TODO: test
    // the function should round f to the nearest toWhat
    // e.g.: if toWhat is 0.01 ... f should be rounded to 2 places after the decimal
    public static float round(float f, float toWhat) {
        assert toWhat > 0;

        float inverse = 1f / toWhat;
                
        return ((int)(f * inverse)) / inverse;
    }

    public static float abs(float a) {
        return Math.abs(a);
    }

    public static float pow(float a, float x) {
        return (float)Math.pow(a, x);
    }

    public static float toRadians(float degs) {
        return degs / (float) (180.0 / Math.PI);
    }
    public static float toDegrees(float rads) {
        return (float) (180.0 / Math.PI) / rads;
    }

    public static float sin(float degs) {
        return (float)Math.sin(toRadians(degs));
    }
    public static float cos(float degs) {
        return (float)Math.cos(toRadians(degs));
    }
    public static float tan(float degs) {
        return (float)Math.tan(toRadians(degs));
    }
    public static float atan(float r) {
        return toDegrees((float)Math.atan(r));
    }
    public static float sqr(float a) { return pow(a, .5f); }
}
