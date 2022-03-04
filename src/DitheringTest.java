import static org.junit.Assert.*;

public class DitheringTest {

    @org.junit.Test
    public void closestColor() {

        Dithering.RGBPixel[] palette = new Dithering.RGBPixel[]{
                new Dithering.RGBPixel(0, 0, 0),
                new Dithering.RGBPixel(128, 128, 128),
                new Dithering.RGBPixel(255, 255, 255)
        };

        // Test 1, black should be black
        Dithering.RGBPixel resultPixel;
        Dithering.RGBPixel blackPixel = new Dithering.RGBPixel(0, 0, 0);
        resultPixel = Dithering.closestColor(blackPixel, palette);
        assertEquals(resultPixel, blackPixel);

        // White pixel should return in a white pixel
        Dithering.RGBPixel whitePixel = new Dithering.RGBPixel(255, 255, 255);
        resultPixel = Dithering.closestColor(whitePixel, palette);
        assertEquals(resultPixel, whitePixel);

        // light grey pixel should return in a white pixel
        Dithering.RGBPixel lightgrey = new Dithering.RGBPixel(200, 200, 200);
        resultPixel = Dithering.closestColor(lightgrey, palette);
        assertEquals(resultPixel, whitePixel);

        // Y value of 127
        Dithering.RGBPixel y127 = new Dithering.RGBPixel(127, 128, 128);
        resultPixel = Dithering.closestColor(y127, palette);
        assertEquals(resultPixel, new Dithering.RGBPixel(128, 128, 128));
    }

    @org.junit.Test
    public void closestColorBW() {

        // Test 1, black should be black
        Dithering.RGBPixel resultPixel;
        Dithering.RGBPixel blackPixel = new Dithering.RGBPixel(0, 0, 0);
        resultPixel = Dithering.closestColorBW(blackPixel);
        assertEquals(resultPixel, blackPixel);

        // White pixel should return in a white pixel
        Dithering.RGBPixel whitePixel = new Dithering.RGBPixel(255, 255, 255);
        resultPixel = Dithering.closestColorBW(whitePixel);
        assertEquals(resultPixel, whitePixel);

        // light grey pixel should return in a white pixel
        Dithering.RGBPixel lightgrey = new Dithering.RGBPixel(200, 200, 200);
        resultPixel = Dithering.closestColorBW(lightgrey);
        assertEquals(resultPixel, whitePixel);

        // Y value of 127
        Dithering.RGBPixel y127 = new Dithering.RGBPixel(127, 128, 128);
        resultPixel = Dithering.closestColorBW(y127);
        assertEquals(resultPixel, blackPixel);

        // Y value of 128
        Dithering.RGBPixel y128 = new Dithering.RGBPixel(129, 128, 128);
        resultPixel = Dithering.closestColorBW(y128);
        assertEquals(resultPixel, whitePixel);

    }

    @org.junit.Test
    public void RGBPixelAdd() {

        Dithering.RGBPixel px127 = new Dithering.RGBPixel(127, 127, 127);
        Dithering.RGBPixel px128 = new Dithering.RGBPixel(128, 128, 128);
        Dithering.RGBPixel px1 = new Dithering.RGBPixel(1, 1, 1);

        Dithering.RGBPixel resultPixel;

        resultPixel = px127.add(px1);
        assertEquals(px128, resultPixel);
        // px127 and px1 must not change
        assertEquals(px127, new Dithering.RGBPixel(127, 127, 127));
        assertEquals(px1, new Dithering.RGBPixel(1, 1, 1));


        resultPixel = px128.add(px128);
        assertEquals(resultPixel, new Dithering.RGBPixel(256, 256, 256));
    }

    @org.junit.Test
    public void RGBPixelSub() {

        Dithering.RGBPixel px127 = new Dithering.RGBPixel(127, 127, 127);
        Dithering.RGBPixel px128 = new Dithering.RGBPixel(128, 128, 128);
        Dithering.RGBPixel px1 = new Dithering.RGBPixel(1, 1, 1);

        Dithering.RGBPixel resultPixel;

        resultPixel = px128.sub(px1);
        assertEquals(px127, resultPixel);
        // px128 and px1 must not change
        assertEquals(px128, new Dithering.RGBPixel(128, 128, 128));
        assertEquals(px1, new Dithering.RGBPixel(1, 1, 1));


        resultPixel = px127.sub(px128);
        assertEquals(resultPixel, new Dithering.RGBPixel(-1, -1, -1));
    }

    @org.junit.Test
    public void RGBPixelDiff() {

        Dithering.RGBPixel px127 = new Dithering.RGBPixel(127, 127, 127);
        Dithering.RGBPixel px128 = new Dithering.RGBPixel(128, 128, 128);

        int result = px127.diff(px128);
        assertEquals(result, 3);

        result = px127.diff(new Dithering.RGBPixel(0, 0, 0));
        assertEquals(result, 48387);
    }
}