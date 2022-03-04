import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class Dithering {
    /**
     * palette will be used only for exercise 4.2
     */
    public static final RGBPixel[] palette = new RGBPixel[]
            {
                    new RGBPixel(0, 0, 0),
                    new RGBPixel(0, 0, 255),
                    new RGBPixel(0, 255, 0),
                    new RGBPixel(0, 255, 255),
                    new RGBPixel(255, 0, 0),
                    new RGBPixel(255, 0, 255),
                    new RGBPixel(255, 255, 0),
                    new RGBPixel(255, 255, 255)
            };

    public static final RGBPixel[] palette2 = new RGBPixel[]
            {
                    new RGBPixel(0, 0, 0),
                    new RGBPixel(255, 255, 255)
            };

    public static void main(String args[]) {
        BufferedImage image = readImg("lena_512x512.png"); // TODO edit to your needs
        BufferedImage image_bw = deepCopy(image);
        BufferedImage image_color = deepCopy(image);

        image_bw = bw_dither(image_bw);
        image_color = color_dither(image_color);

        writeImg(image_bw, "png", "dither_bw.png"); // TODO edit to your needs
        writeImg(image_color, "png", "dither_color.png"); // TODO edit to your needs
    }

    /**
     * @param image the input image
     * @return the dithered black and white image
     */
    public static BufferedImage bw_dither(BufferedImage image) {
        for (int y = 0; y < image.getHeight() ; y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                RGBPixel oldpixel = new RGBPixel(image.getRGB(x,y));
                RGBPixel newPixel = closestColorBW(oldpixel);
                image.setRGB(x,y,newPixel.toRGB());
                RGBPixel quanterror = oldpixel.sub(newPixel);
                /*Floyd-Steinberg algorithm says that we should add 7/16 of error to the right neighbour,
                * 5/16 to bottom, 3/16 to the bottom left and 1/16 to bottom right neighbour.
                * Read more: https://en.wikipedia.org/wiki/Floyd-Steinberg_dithering*/
                if(x+1<image.getWidth())
                    image.setRGB(x+1,y,quanterror.mul(7.0/16.0).add(new RGBPixel(image.getRGB(x+1,y))).toRGB());

                if(y+1<image.getHeight())
                    image.setRGB(x,y+1,quanterror.mul(5.0/16.0).add(new RGBPixel(image.getRGB(x,y+1))).toRGB());

                if(y+1<image.getHeight()&&x-1>=0)
                    image.setRGB(x-1,y+1,quanterror.mul(3.0/16.0).add(new RGBPixel(image.getRGB(x-1,y+1))).toRGB());

                if(y+1<image.getHeight()&&x+1<image.getWidth())
                    image.setRGB(x+1,y+1,quanterror.mul(1.0/16.0).add(new RGBPixel(image.getRGB(x+1,y+1))).toRGB());

            }
        }
        return image;
    }

    /**
     * @param image the input image
     * @return the dithered 8bit color image using the static palette
     */
    public static BufferedImage color_dither(BufferedImage image) {
        for (int y = 0; y < image.getHeight() ; y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                RGBPixel oldpixel = new RGBPixel(image.getRGB(x,y));
                RGBPixel newPixel = closestColor(oldpixel,palette);
                image.setRGB(x,y,newPixel.toRGB());
                RGBPixel quanterror = oldpixel.sub(newPixel);
                /*Floyd-Steinberg algorithm says that we should add 7/16 of error to the right neighbour,
                 * 5/16 to bottom, 3/16 to the bottom left and 1/16 to bottom right neighbour.
                 * Read more: https://en.wikipedia.org/wiki/Floyd-Steinberg_dithering*/
                if(x+1<image.getWidth())
                    image.setRGB(x+1,y,quanterror.mul(7.0/16.0).add(new RGBPixel(image.getRGB(x+1,y))).toRGB());

                if(y+1<image.getHeight())
                    image.setRGB(x,y+1,quanterror.mul(5.0/16.0).add(new RGBPixel(image.getRGB(x,y+1))).toRGB());

                if(y+1<image.getHeight()&&x-1>=0)
                    image.setRGB(x-1,y+1,quanterror.mul(3.0/16.0).add(new RGBPixel(image.getRGB(x-1,y+1))).toRGB());

                if(y+1<image.getHeight()&&x+1<image.getWidth())
                    image.setRGB(x+1,y+1,quanterror.mul(1.0/16.0).add(new RGBPixel(image.getRGB(x+1,y+1))).toRGB());

            }
        }
        return image;
    }

    /**
     * @param color input color
     * @return the closest outputcolor. (Can only be black or white!)
     */
    public static RGBPixel closestColorBW(RGBPixel color) {
        double y = (0.299*color.r+0.587*color.g+0.114*color.b);
        // Der Zielwert kann nur Schwarz oder Weiß sein.
        if(y<128)
            return new RGBPixel(0,0,0);
        else
            return new RGBPixel(255,255,255);
    }

    /**
     * @param c       the input color
     * @param palette the palette to use
     * @return the closest color of the palette compared to c
     */
    public static RGBPixel closestColor(RGBPixel c, RGBPixel[] palette) {
       RGBPixel wert = palette[0];

        for (RGBPixel px : palette) {
            // Für die Bestimmung der Differenz zweier Farbwerte benutzen wir die diff Funktion
            if(px.diff(c) < wert.diff(c)){
                wert = px;
            }
        }
       return wert;
    }

    /**
     * The Class RGBPixel is a helper class to ease the calculation with colors.
     */
    static class RGBPixel {
        int r, g, b;

        public RGBPixel(int c) {
            Color color = new Color(c);
            this.r = color.getRed();
            this.g = color.getGreen();
            this.b = color.getBlue();
        }

        public RGBPixel(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public RGBPixel add(RGBPixel o) {
            return new RGBPixel(this.r+o.r,this.g+o.g,this.b+o.b);
        }

        public RGBPixel sub(RGBPixel o) {
            return new RGBPixel(this.r-o.r,this.g-o.g,this.b-o.b);
        }

        public RGBPixel mul(double d) {
            return new RGBPixel((int) (d * r), (int) (d * g), (int) (d * b));
        }

        public int toRGB() {
            return toColor().getRGB();
        }

        public Color toColor() {
            return new Color(clamp(r), clamp(g), clamp(b));
        }

        public int clamp(int c) {
            return Math.max(0, Math.min(255, c));
        }

        public int diff(RGBPixel o) {
           int Rdiff = o.r-this.r;
           int Gdiff = o.g-this.g;
           int Bdiff = o.b-this.b;
           int quad = Rdiff*Rdiff + Gdiff*Gdiff + Bdiff*Bdiff;

           return quad;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RGBPixel)) {
                return false;
            }
            return this.r == ((RGBPixel) obj).r && this.g == ((RGBPixel) obj).g && this.b == ((RGBPixel) obj).b;
        }
    }

    private static BufferedImage readImg(String filePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.out.println("Could not load Image\n");
            System.exit(-1);
        }
        return image;
    }

    private static void writeImg(BufferedImage image, String format, String path) {
        try {
            ImageIO.write(image, format, new File(path));
        } catch (IOException e) {
            System.out.println("Could not save Image\n");
            System.exit(-1);
        }
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}