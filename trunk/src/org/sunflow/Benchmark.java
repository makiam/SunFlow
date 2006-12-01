package org.sunflow;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;

import org.sunflow.core.Display;
import org.sunflow.core.camera.PinholeLens;
import org.sunflow.core.display.FrameDisplay;
import org.sunflow.core.gi.InstantGI;
import org.sunflow.core.light.MeshLight;
import org.sunflow.core.primitive.Mesh;
import org.sunflow.core.primitive.Sphere;
import org.sunflow.core.shader.DiffuseShader;
import org.sunflow.core.shader.GlassShader;
import org.sunflow.core.shader.MirrorShader;
import org.sunflow.image.Color;
import org.sunflow.math.Matrix4;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;
import org.sunflow.system.BenchmarkFramework;
import org.sunflow.system.BenchmarkTest;
import org.sunflow.system.UI;
import org.sunflow.system.UserInterface;
import org.sunflow.system.UI.Module;
import org.sunflow.system.UI.PrintLevel;

public class Benchmark extends SunflowAPI implements BenchmarkTest, UserInterface {
    private String resourcePath;
    private PrintStream stream;
    private boolean showGUI;
    private boolean showOutput;
    private boolean showBenchmarkOutput;
    private int errorThreshold;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-test"))
            new Benchmark("resources/", System.out, 0, 512, true, true, true, 4, true);
        else {
            // this is used to regenerated the reference frames if needed
            new Benchmark("resources/", System.out, 0, 128, false, false, true, 4, true).build();
            new Benchmark("resources/", System.out, 0, 256, false, false, true, 4, true).build();
            new Benchmark("resources/", System.out, 0, 384, false, false, true, 4, true).build();
            new Benchmark("resources/", System.out, 0, 512, false, false, true, 4, true).build();
            new Benchmark("resources/", System.out, 0, 1024, false, false, true, 4, true).build();
        }
    }

    public Benchmark(boolean showGUI) {
        this("resources/", System.out, 0, 512, showGUI, false, true, 6, false);
    }

    public Benchmark(String resourcePath, PrintStream stream, int threads, int resolution, boolean showGUI, boolean showOutput, boolean showBenchmarkOutput, int errorThreshold, boolean generateMissingReference) {
        this.resourcePath = resourcePath;
        this.stream = stream;
        this.showGUI = showGUI;
        this.showOutput = showOutput;
        this.showBenchmarkOutput = showBenchmarkOutput;
        this.errorThreshold = errorThreshold;
        // forward all output through this class (uses the specified stream)
        UI.set(this);
        UI.printInfo(Module.BENCH, "Preparing benchmarking scene ...");
        // settings
        parameter("threads", threads);
        // spawn regular priority threads
        parameter("threads.lowPriority", false);
        parameter("resolutionX", resolution);
        parameter("resolutionY", resolution);
        parameter("aa.min", -3);
        parameter("aa.max", 0);
        parameter("depths.diffuse", 4);
        parameter("depths.reflection", 2);
        parameter("depths.refraction", 2);
        parameter("bucket.order", "hilbert");
        parameter("bucket.size", 32);
        options(SunflowAPI.DEFAULT_OPTIONS);
        accel("bih");
        // geometry
        buildCornellBox();
        String referenceImageFilename = String.format("%sgolden_%04X.png", resourcePath, resolution);
        boolean generatingReference = false;
        if (new File(referenceImageFilename).exists()) {
            // load the reference image
            UI.printInfo(Module.BENCH, "Loading reference image: %s", referenceImageFilename);
            try {
                BufferedImage bi = ImageIO.read(new File(referenceImageFilename));
                if (bi.getWidth() != resolution || bi.getHeight() != resolution)
                    UI.printError(Module.BENCH, "Reference image has invalid resolution! Expected %dx%d found %dx%d", resolution, resolution, bi.getWidth(), bi.getHeight());
                ValidatingDisplay.reference = new int[resolution * resolution];
                for (int y = 0, i = 0; y < resolution; y++)
                    for (int x = 0; x < resolution; x++, i++)
                        ValidatingDisplay.reference[i] = bi.getRGB(x, resolution - 1 - y); // flip
            } catch (IOException e) {
                UI.printError(Module.BENCH, "Reference image could not be opened");
            }
        } else if (generateMissingReference) {
            UI.printWarning(Module.BENCH, "Reference image was not found - it will be generated to: %s", referenceImageFilename);
            generatingReference = true;
        } else {
            UI.printError(Module.BENCH, "Reference image %s was not found - cannot continue benchmarking", referenceImageFilename);
        }
        // this first render generates the reference frame - it is not timed
        // this also caches the acceleration data structures so it won't be
        // included in the kernel timing
        UI.printInfo(Module.BENCH, "Rendering warmup frame ...");
        render(SunflowAPI.DEFAULT_OPTIONS, new ValidatingDisplay(generatingReference, errorThreshold));
        // if the data has been just generated - write it to file for future
        // runs
        if (generatingReference) {
            UI.printInfo(Module.BENCH, "Saving reference image to: %s", referenceImageFilename);
            BufferedImage bi = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_RGB);
            for (int y = 0, i = 0; y < resolution; y++)
                for (int x = 0; x < resolution; x++, i++)
                    bi.setRGB(x, resolution - 1 - y, ValidatingDisplay.reference[i]);
            try {
                ImageIO.write(bi, "png", new File(referenceImageFilename));
            } catch (IOException e) {
                UI.printError(Module.BENCH, "Unabled to save reference image: %s", e.getMessage());
            }
        }
    }

    private void buildCornellBox() {
        // camera
        parameter("eye", new Point3(0, 0, -600));
        parameter("target", new Point3(0, 0, 0));
        parameter("up", new Vector3(0, 1, 0));
        parameter("fov", 45.0f);
        String name = getUniqueName("camera");
        camera(name, new PinholeLens());
        parameter("camera", name);
        options(SunflowAPI.DEFAULT_OPTIONS);
        // cornell box
        Color grey = new Color(0.70f, 0.70f, 0.70f);
        Color blue = new Color(0.25f, 0.25f, 0.80f);
        Color red = new Color(0.80f, 0.25f, 0.25f);
        Color emit = new Color(15, 15, 15);

        float minX = -200;
        float maxX = 200;
        float minY = -160;
        float maxY = minY + 400;
        float minZ = -250;
        float maxZ = 200;

        float[] verts = new float[] { minX, minY, minZ, maxX, minY, minZ, maxX,
                minY, maxZ, minX, minY, maxZ, minX, maxY, minZ, maxX, maxY,
                minZ, maxX, maxY, maxZ, minX, maxY, maxZ, };
        int[] indices = new int[] { 0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4, 1, 2,
                5, 5, 6, 2, 2, 3, 6, 6, 7, 3, 0, 3, 4, 4, 7, 3 };

        parameter("diffuse", grey);
        shader("grey_shader", new DiffuseShader());
        parameter("diffuse", red);
        shader("red_shader", new DiffuseShader());
        parameter("diffuse", blue);
        shader("blue_shader", new DiffuseShader());

        // build walls
        parameter("triangles", indices);
        parameter("points", "point", "vertex", verts);
        parameter("faceshaders", new int[] { 0, 0, 0, 0, 1, 1, 0, 0, 2, 2 });
        geometry("walls", new Mesh());

        // instance walls
        parameter("shaders", new String[] { "grey_shader", "red_shader",
                "blue_shader" });
        instance("walls.instance", "walls");

        // create mesh light
        parameter("points", "point", "vertex", new float[] { -50, maxY - 1,
                -50, 50, maxY - 1, -50, 50, maxY - 1, 50, -50, maxY - 1, 50 });
        parameter("triangles", new int[] { 0, 1, 2, 2, 3, 0 });
        parameter("radiance", emit);
        parameter("samples", 16);
        MeshLight light = new MeshLight();
        light.init("light", this);

        // spheres
        parameter("eta", 1.6f);
        shader("Glass", new GlassShader());
        sphere("glass_sphere", "Glass", -60, minY + 100, -100, 50);
        parameter("color", new Color(0.70f, 0.70f, 0.70f));
        shader("Mirror", new MirrorShader());
        sphere("mirror_sphere", "Mirror", 100, minY + 60, -50, 50);

        // scanned model
        parameter("diffuse", grey);
        shader("ra3shader", new DiffuseShader());
        String ra3file = resourcePath + "maxplanck.ra3";
        if (!parse(ra3file))
            UI.printError(Module.BENCH, "Unable to load %s", ra3file);
        giEngine(new InstantGI(90, 1, 0.00002f, 0));
    }

    private void sphere(String name, String shaderName, float x, float y, float z, float radius) {
        geometry(name, new Sphere());
        parameter("transform", Matrix4.translation(x, y, z).multiply(Matrix4.scale(radius)));
        parameter("shaders", shaderName);
        instance(name + ".instance", name);
    }

    public void execute() {
        if (showGUI)
            render(SunflowAPI.DEFAULT_OPTIONS, new FrameDisplay());
        // prepare the framework
        BenchmarkFramework framework = new BenchmarkFramework(50, 120);
        // run the framework
        framework.execute(this);
    }

    public void kernelBegin() {
        // no per loop setup
    }

    public void kernelMain() {
        render(SunflowAPI.DEFAULT_OPTIONS, new ValidatingDisplay(false, errorThreshold));
    }

    public void kernelEnd() {
        // no per loop cleanup
    }

    private static class ValidatingDisplay implements Display {
        private static int[] reference;
        private int[] pixels;
        private int iw, ih;
        private boolean generateReference;
        private int errorThreshold;

        ValidatingDisplay(boolean generateReference, int errorThreshold) {
            this.generateReference = generateReference;
            this.errorThreshold = errorThreshold;
        }

        public void imageBegin(int w, int h, int bucketSize) {
            pixels = new int[w * h];
            iw = w;
            ih = h;
        }

        public void imagePrepare(int x, int y, int w, int h, int id) {
        }

        public void imageUpdate(int x, int y, int w, int h, Color[] data) {
            for (int j = 0, index = 0; j < h; j++) {
                for (int i = 0; i < w; i++, index++) {
                    int offset = ((x + i) + iw * (ih - 1 - (y + j)));
                    pixels[offset] = data[index].copy().toNonLinear().toRGB();
                }
            }
        }

        public void imageFill(int x, int y, int w, int h, Color c) {
        }

        public void imageEnd() {
            if (generateReference) {
                reference = pixels;
            } else {
                int diff = 0;
                if (reference != null && pixels.length == reference.length) {
                    for (int i = 0; i < pixels.length; i++) {
                        // count absolute RGB differences
                        diff += Math.abs((pixels[i] & 0xFF) - (reference[i] & 0xFF));
                        diff += Math.abs(((pixels[i] >> 8) & 0xFF) - ((reference[i] >> 8) & 0xFF));
                        diff += Math.abs(((pixels[i] >> 16) & 0xFF) - ((reference[i] >> 16) & 0xFF));
                    }
                    if (diff > errorThreshold)
                        UI.printError(Module.BENCH, "Image check failed! - #errors: %d", diff);
                    else
                        UI.printInfo(Module.BENCH, "Image check passed!");
                } else
                    UI.printError(Module.BENCH, "Image check failed! - reference is not comparable");
            }
        }
    }

    public void print(Module m, PrintLevel level, String s) {
        if (stream != null)
            if (showOutput || (showBenchmarkOutput && m == Module.BENCH))
                stream.println(UI.formatOutput(m, level, s));
        if (level == PrintLevel.ERROR)
            throw new RuntimeException(s);
    }

    public void taskStart(String s, int min, int max) {
    }

    public void taskUpdate(int current) {
    }

    public void taskStop() {
    }
}