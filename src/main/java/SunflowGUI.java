import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;
import org.sunflow.core.TextureCache;
import org.sunflow.core.accel.KDTree;
import org.sunflow.core.display.FileDisplay;
import org.sunflow.core.display.FrameDisplay;
import org.sunflow.core.display.ImgPipeDisplay;
import org.sunflow.core.primitive.TriangleMesh;
import org.sunflow.system.ImagePanel;
import org.sunflow.system.Timer;
import org.sunflow.system.UI;
import org.sunflow.system.UI.Module;
import org.sunflow.system.UI.PrintLevel;
import org.sunflow.system.UserInterface;

@SuppressWarnings({"serial", "java:S1220"})
public class SunflowGUI extends javax.swing.JFrame implements UserInterface {
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;



    private JButton renderButton;


    private JMenuItem tileWindowMenuItem;


    private JInternalFrame consoleFrame;
    private JInternalFrame editorFrame;
    private JInternalFrame imagePanelFrame;
    private JDesktopPane desktop;
    private JCheckBoxMenuItem smallTrianglesMenuItem;



    private JMenu imageMenu;
    private ImagePanel imagePanel;
    private JCheckBoxMenuItem clearLogMenuItem;
    private JButton taskCancelButton;
    private JProgressBar taskProgressBar;

    private JCheckBoxMenuItem autoBuildMenuItem;


    private JMenu sceneMenu;
    private RSyntaxTextArea editorTextArea;
    private JTextArea consoleTextArea;


    private JButton iprButton;
    private JButton buildButton;
    private JMenuItem saveAsMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem openFileMenuItem;
    private JMenuItem newFileMenuItem;


    // non-swing items
    private String currentFile;
    private String currentTask;
    private int currentTaskLastP;
    private SunflowAPI api;
    private File lastSaveDirectory;

    @SuppressWarnings("java:S106")
    public static void usage(boolean verbose) {
        System.out.println("Usage: SunflowGUI [options] scenefile");
        if (verbose) {
            System.out.println("Sunflow v" + SunflowAPI.VERSION + " textmode");
            System.out.println("Renders the specified scene file");
            System.out.println("Options:");
            System.out.println("  -o filename      Saves the output as the specified filename (png, hdr, tga)");
            System.out.println("                   #'s get expanded to the current frame number");
            System.out.println("  -nogui           Don't open the frame showing rendering progress");
            System.out.println("  -ipr             Render using progressive algorithm");
            System.out.println("  -sampler type    Render using the specified algorithm");
            System.out.println("  -threads n       Render using n threads");
            System.out.println("  -lopri           Set thread priority to low (default)");
            System.out.println("  -hipri           Set thread priority to high");
            System.out.println("  -smallmesh       Load triangle meshes using triangles optimized for memory use");
            System.out.println("  -dumpkd          Dump KDTree to an obj file for visualization");
            System.out.println("  -buildonly       Do not call render method after loading the scene");
            System.out.println("  -showaa          Display sampling levels per pixel for bucket renderer");
            System.out.println("  -nogi            Disable any global illumination engines in the scene");
            System.out.println("  -nocaustics      Disable any caustic engine in the scene");
            System.out.println("  -pathgi n        Use path tracing with n samples to render global illumination");
            System.out.println("  -quick_ambocc d  Applies ambient occlusion to the scene with specified maximum distance");
            System.out.println("  -quick_uvs       Applies a surface uv visualization shader to the scene");
            System.out.println("  -quick_normals   Applies a surface normal visualization shader to the scene");
            System.out.println("  -quick_id        Renders using a unique color for each instance");
            System.out.println("  -quick_prims     Renders using a unique color for each primitive");
            System.out.println("  -quick_gray      Renders using a plain gray diffuse shader");
            System.out.println("  -quick_wire      Renders using a wireframe shader");
            System.out.println("  -resolution w h  Changes the render resolution to the specified width and height (in pixels)");
            System.out.println("  -aa min max      Overrides the image anti-aliasing depths");
            System.out.println("  -samples n       Overrides the image sample count (affects bucket and multipass samplers)");
            System.out.println("  -bucket n order  Changes the default bucket size to n pixels and the default order");
            System.out.println("  -bake name       Bakes a lightmap for the specified instance");
            System.out.println("  -bakedir dir     Selects the type of lightmap baking: dir=view or ortho");
            System.out.println("  -filter type     Selects the image filter to use");

            System.out.println("  -frame n         Set frame number to the specified value");
            System.out.println("  -anim n1 n2      Render all frames between the two specified values (inclusive)");            
            System.out.println("  -v verbosity     Set the verbosity level: 0=none,1=errors,2=warnings,3=info,4=detailed");
            System.out.println("  -h               Prints this message");
        }
        System.exit(1);
    }

    public static void main(String... args) {
        if (args.length > 0) {
            boolean showFrame = true;
            String sampler = null;
            boolean noRender = false;
            String filename = null;
            String input = null;
            int i = 0;
            int threads = 0;
            boolean lowPriority = true;
            boolean showAA = false;
            boolean noGI = false;
            boolean noCaustics = false;
            int pathGI = 0;
            float maxDist = 0;
            String shaderOverride = null;
            int resolutionW = 0, resolutionH = 0; 
            int aaMin = -5;
            int aaMax = -5;
            int samples = -1;
            int bucketSize = 0;
            String bucketOrder = null;
            String bakingName = null;
            boolean bakeViewdep = false;
            String filterType = null;
            
            int frameStart = 1;
            int frameStop = 1;
            while (i < args.length) {
                switch (args[i]) {
                    case "-o" -> {
                        if (i > args.length - 2)
                            usage(false);
                        filename = args[i + 1];
                        i += 2;
                    }
                    case "-nogui" -> {
                        showFrame = false;
                        i++;
                    }
                    case "-ipr" -> {
                        sampler = "ipr";
                        i++;
                    }
                    case "-threads" -> {
                        if (i > args.length - 2)
                            usage(false);
                        threads = Integer.parseInt(args[i + 1]);
                        i += 2;
                    }
                    case "-lopri" -> {
                        lowPriority = true;
                        i++;
                    }
                    case "-hipri" -> {
                        lowPriority = false;
                        i++;
                    }
                    case "-sampler" -> {
                        if (i > args.length - 2)
                            usage(false);
                        sampler = args[i + 1];
                        i += 2;
                    }
                    case "-smallmesh" -> {
                        TriangleMesh.setSmallTriangles(true);
                        i++;
                    }
                    case "-dumpkd" -> {
                        KDTree.setDumpMode(true, "kdtree");
                        i++;
                    }
                    case "-buildonly" -> {
                        noRender = true;
                        i++;
                    }
                    case "-showaa" -> {
                        showAA = true;
                        i++;
                    }
                    case "-nogi" -> {
                        noGI = true;
                        i++;
                    }
                    case "-nocaustics" -> {
                        noCaustics = true;
                        i++;
                    }
                    case "-pathgi" -> {
                        if (i > args.length - 2)
                            usage(false);
                        pathGI = Integer.parseInt(args[i + 1]);
                        i += 2;
                    }
                    case "-quick_ambocc" -> {
                        if (i > args.length - 2)
                            usage(false);
                        maxDist = Float.parseFloat(args[i + 1]);
                        shaderOverride = "ambient_occlusion"; // new

                        // AmbientOcclusionShader(Color.WHITE, d);
                        i += 2;
                    }
                    case "-quick_uvs" -> {
                        if (i > args.length - 1)
                            usage(false);
                        shaderOverride = "show_uvs";
                        i++;
                    }
                    case "-quick_normals" -> {
                        if (i > args.length - 1)
                            usage(false);
                        shaderOverride = "show_normals";
                        i++;
                    }
                    case "-quick_id" -> {
                        if (i > args.length - 1)
                            usage(false);
                        shaderOverride = "show_instance_id";
                        i++;
                    }
                    case "-quick_prims" -> {
                        if (i > args.length - 1)
                            usage(false);
                        shaderOverride = "show_primitive_id";
                        i++;
                    }
                    case "-quick_gray" -> {
                        if (i > args.length - 1)
                            usage(false);
                        shaderOverride = "quick_gray";
                        i++;
                    }
                    case "-quick_wire" -> {
                        if (i > args.length - 1)
                            usage(false);
                        shaderOverride = "wireframe";
                        i++;
                    }
                    case "-resolution" -> {
                        if (i > args.length - 3)
                            usage(false);
                        resolutionW = Integer.parseInt(args[i + 1]);
                        resolutionH = Integer.parseInt(args[i + 2]);
                        i += 3;
                    }
                    case "-aa" -> {
                        if (i > args.length - 3)
                            usage(false);
                        aaMin = Integer.parseInt(args[i + 1]);
                        aaMax = Integer.parseInt(args[i + 2]);
                        i += 3;
                    }
                    case "-samples" -> {
                        if (i > args.length - 2)
                            usage(false);
                        samples = Integer.parseInt(args[i + 1]);
                        i += 2;
                    }
                    case "-bucket" -> {
                        if (i > args.length - 3)
                            usage(false);
                        bucketSize = Integer.parseInt(args[i + 1]);
                        bucketOrder = args[i + 2];
                        i += 3;
                    }
                    case "-bake" -> {
                        if (i > args.length - 2)
                            usage(false);
                        bakingName = args[i + 1];
                        i += 2;
                    }
                    case "-bakedir" -> {
                        if (i > args.length - 2)
                            usage(false);
                        String baketype = args[i + 1];
                        if (baketype.equals("view"))
                            bakeViewdep = true;
                        else if (baketype.equals("ortho"))
                            bakeViewdep = false;
                        else
                            usage(false);
                        i += 2;
                    }
                    case "-filter" -> {
                        if (i > args.length - 2)
                            usage(false);
                        filterType = args[i + 1];
                        i += 2;
                    }
                    case "-frame" -> {
                        if (i > args.length - 2)
                            usage(false);
                        frameStart = frameStop = Integer.parseInt(args[i + 1]);
                        i += 2;
                    }
                    case "-anim" -> {
                        if (i > args.length - 3)
                            usage(false);
                        frameStart = Integer.parseInt(args[i + 1]);
                        frameStop = Integer.parseInt(args[i + 2]);
                        i += 3;
                    }
                    case "-v" -> {
                        if (i > args.length - 2)
                            usage(false);
                        UI.verbosity(Integer.parseInt(args[i + 1]));
                        i += 2;
                    }
                    case "-h", "-help" -> usage(true);
                    default -> {
                        if (input != null)
                            usage(false);
                        input = args[i];
                        i++;
                    }
                }
            }

            if (input == null)
                usage(false);
            SunflowAPI.runSystemCheck();

            if (frameStart < frameStop && showFrame) {
                UI.printWarning(Module.GUI, "Animations should not be rendered without -nogui - forcing GUI off anyway");
                showFrame = false;
            }
            if (frameStart < frameStop && filename == null) {
                filename = "output.#.png";
                UI.printWarning(Module.GUI, "Animation output was not specified - defaulting to: \"%s\"", filename);
            }
            for (int frameNumber = frameStart; frameNumber <= frameStop; frameNumber++) {
                SunflowAPI api = SunflowAPI.create(input, frameNumber);
                if (api == null)
                    continue;
                if (noRender)
                    continue;
                if (resolutionW > 0 && resolutionH > 0) {
                    api.parameter("resolutionX", resolutionW);
                    api.parameter("resolutionY", resolutionH);
                }
                if (aaMin != -5 || aaMax != -5) {
                    api.parameter("aa.min", aaMin);
                    api.parameter("aa.max", aaMax);
                }
                if (samples >= 0)
                    api.parameter("aa.samples", samples);
                if (bucketSize > 0)
                    api.parameter("bucket.size", bucketSize);
                if (bucketOrder != null)
                    api.parameter("bucket.order", bucketOrder);
                api.parameter("aa.display", showAA);
                api.parameter("threads", threads);
                api.parameter("threads.lowPriority", lowPriority);
                if (bakingName != null) {
                    api.parameter("baking.instance", bakingName);
                    api.parameter("baking.viewdep", bakeViewdep);
                }
                if (filterType != null)
                    api.parameter("filter", filterType);
                if (noGI)
                    api.parameter("gi.engine", "none");
                else if (pathGI > 0) {
                    api.parameter("gi.engine", "path");
                    api.parameter("gi.path.samples", pathGI);
                }
                if (noCaustics)
                    api.parameter("caustics", "none");
                if (sampler != null)
                    api.parameter("sampler", sampler);
                api.options(SunflowAPI.DEFAULT_OPTIONS);
                if (shaderOverride != null) {
                    if (shaderOverride.equals("ambient_occlusion"))
                        api.parameter("maxdist", maxDist);
                    api.shader("cmdline_override", shaderOverride);
                    api.parameter("override.shader", "cmdline_override");
                    api.parameter("override.photons", true);
                    api.options(SunflowAPI.DEFAULT_OPTIONS);
                }
                // create display
                Display display;
                String currentFilename = (filename != null) ? filename.replace("#", String.format("%04d", frameNumber)) : null;
                if (showFrame) {
                    display = new FrameDisplay(currentFilename);
                } else {
                    if (currentFilename != null && currentFilename.equals("imgpipe")) {
                        display = new ImgPipeDisplay();
                    } else
                        display = new FileDisplay(currentFilename);
                }
                api.render(SunflowAPI.DEFAULT_OPTIONS, display);
            }
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            SunflowGUI gui = new SunflowGUI();
            gui.setVisible(true);
            Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
            if (screenRes.getWidth() <= DEFAULT_WIDTH || screenRes.getHeight() <= DEFAULT_HEIGHT)
                gui.setExtendedState(MAXIMIZED_BOTH);
            gui.tileWindowMenuItem.doClick();
            SunflowAPI.runSystemCheck();
        }
    }

    public SunflowGUI() {
        super();
        currentFile = null;
        lastSaveDirectory = null;
        api = null;
        initGUI();
        pack();
        setLocationRelativeTo(null);
        newFileMenuItemActionPerformed(null);
        UI.set(this);
    }

    private void initGUI() {
        setTitle("Sunflow v" + SunflowAPI.VERSION);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        {
            desktop = new JDesktopPane();
            getContentPane().add(desktop, BorderLayout.CENTER);
            Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
            if (screenRes.getWidth() <= DEFAULT_WIDTH || screenRes.getHeight() <= DEFAULT_HEIGHT)
                desktop.setPreferredSize(new java.awt.Dimension(640, 480));
            else
                desktop.setPreferredSize(new java.awt.Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
            {
                imagePanelFrame = new JInternalFrame();
                desktop.add(imagePanelFrame);
                {
                    JPanel jPanel1 = new JPanel();
                    FlowLayout jPanel1Layout = new FlowLayout();
                    jPanel1Layout.setAlignment(FlowLayout.LEFT);
                    jPanel1.setLayout(jPanel1Layout);
                    imagePanelFrame.getContentPane().add(jPanel1, BorderLayout.NORTH);
                    {
                        renderButton = new JButton("Render");
                        jPanel1.add(renderButton);

                        renderButton.addActionListener(this::renderMenuItemActionPerformed);
                    }
                    {
                        iprButton = new JButton("IPR");
                        jPanel1.add(iprButton);

                        iprButton.addActionListener(this::iprMenuItemActionPerformed);
                    }
                }
                {
                    imagePanel = new ImagePanel();
                    imagePanelFrame.getContentPane().add(imagePanel, BorderLayout.CENTER);
                }
                imagePanelFrame.pack();
                imagePanelFrame.setResizable(true);
                imagePanelFrame.setMaximizable(true);
                imagePanelFrame.setVisible(true);
                imagePanelFrame.setTitle("Image");
                imagePanelFrame.setIconifiable(true);
            }
            {
                editorFrame = new JInternalFrame();
                desktop.add(editorFrame);
                editorFrame.setTitle("Script Editor");
                editorFrame.setMaximizable(true);
                editorFrame.setResizable(true);
                editorFrame.setIconifiable(true);
                {
                    editorTextArea = new RSyntaxTextArea(20, 60);
                    editorTextArea.setAnimateBracketMatching(false);
                    editorTextArea.setCodeFoldingEnabled(true);
                    editorTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

                    editorTextArea.setFont(new java.awt.Font("Monospaced", 0, 13));
                    // drag and drop
                    editorTextArea.setTransferHandler(new SceneTransferHandler());
                    editorFrame.getContentPane().add(new RTextScrollPane(editorTextArea), BorderLayout.CENTER);
                }
                {
                    JPanel jPanel3 = new JPanel();
                    editorFrame.getContentPane().add(jPanel3, BorderLayout.SOUTH);
                    FlowLayout jPanel3Layout = new FlowLayout();
                    jPanel3Layout.setAlignment(FlowLayout.RIGHT);
                    jPanel3.setLayout(jPanel3Layout);
                    {
                        buildButton = new JButton("Build Scene");
                        buildButton.addActionListener(this::buildMenuItemActionPerformed);
                        jPanel3.add(buildButton);


                    }
                }
                editorFrame.pack();
                editorFrame.setVisible(true);
            }
            {
                consoleFrame = new JInternalFrame();
                desktop.add(consoleFrame);
                consoleFrame.setIconifiable(true);
                consoleFrame.setMaximizable(true);
                consoleFrame.setResizable(true);
                consoleFrame.setTitle("Console");
                {
                    var jScrollPane2 = new JScrollPane();
                    consoleFrame.getContentPane().add(jScrollPane2, BorderLayout.CENTER);
                    jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    jScrollPane2.setPreferredSize(new java.awt.Dimension(360, 100));
                    {
                        consoleTextArea = new JTextArea();
                        jScrollPane2.setViewportView(consoleTextArea);
                        consoleTextArea.setFont(new java.awt.Font("Monospaced", 0, 12));
                        consoleTextArea.setEditable(false);
                    }
                }
                {
                    JPanel jPanel4 = new JPanel();
                    consoleFrame.getContentPane().add(jPanel4, BorderLayout.SOUTH);
                    BorderLayout jPanel4Layout = new BorderLayout();
                    jPanel4.setLayout(jPanel4Layout);
                    {
                        JPanel jPanel6 = new JPanel();
                        BorderLayout jPanel6Layout = new BorderLayout();
                        jPanel6.setLayout(jPanel6Layout);
                        jPanel4.add(jPanel6, BorderLayout.CENTER);
                        jPanel6.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
                        {
                            taskProgressBar = new JProgressBar();
                            jPanel6.add(taskProgressBar);
                            taskProgressBar.setEnabled(false);
                            taskProgressBar.setString("");
                            taskProgressBar.setStringPainted(true);
                            taskProgressBar.setOpaque(false);
                        }
                    }
                    {
                        JPanel jPanel5 = new JPanel();
                        FlowLayout jPanel5Layout = new FlowLayout();
                        jPanel5Layout.setAlignment(FlowLayout.RIGHT);
                        jPanel5.setLayout(jPanel5Layout);
                        jPanel4.add(jPanel5, BorderLayout.EAST);
                        {
                            taskCancelButton = new JButton("Cancel");
                            jPanel5.add(taskCancelButton);
                            taskCancelButton.setEnabled(false);
                            taskCancelButton.addActionListener(evt -> UI.taskCancel());
                        }
                        {
                            var clearConsoleButton = new JButton("Clear");
                            clearConsoleButton.addActionListener(evt -> clearConsole());
                            jPanel5.add(clearConsoleButton);

                        }
                    }
                }
                consoleFrame.pack();
                consoleFrame.setVisible(true);
            }
        }
        {
            var menuBar = new JMenuBar();
            setJMenuBar(menuBar);
            {
                var fileMenu = new JMenu("File");
                menuBar.add(fileMenu);

                {
                    newFileMenuItem = new JMenuItem("New");
                    newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
                    newFileMenuItem.addActionListener(this::newFileMenuItemActionPerformed);
                    fileMenu.add(newFileMenuItem);

                }
                {
                    openFileMenuItem = new JMenuItem("Open...");
                    openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
                    openFileMenuItem.addActionListener(this::openFileMenuItemActionPerformed);
                    fileMenu.add(openFileMenuItem);

                }
                {
                    saveMenuItem = new JMenuItem("Save");
                    saveMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
                    saveMenuItem.addActionListener(evt -> saveCurrentFile(currentFile));
                    fileMenu.add(saveMenuItem);

                }
                saveAsMenuItem = new JMenuItem("Save As...");
                fileMenu.add(saveAsMenuItem).addActionListener(this::saveAsMenuItemActionPerformed);
                fileMenu.addSeparator();
                fileMenu.add(new JMenuItem("Exit")).addActionListener(evt -> System.exit(0));
            }
            {
                sceneMenu = new JMenu("Scene");
                menuBar.add(sceneMenu);
                {
                    var buildMenuItem = new JMenuItem("Build");
                    sceneMenu.add(buildMenuItem);

                    buildMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl B"));
                    buildMenuItem.addActionListener(evt -> {
                        if (sceneMenu.isEnabled())
                            buildMenuItemActionPerformed(evt);
                    });
                }
                {
                    autoBuildMenuItem = new JCheckBoxMenuItem("Build on open");
                    sceneMenu.add(autoBuildMenuItem);
                    autoBuildMenuItem.setSelected(true);
                }
                sceneMenu.addSeparator();
                sceneMenu.add(new JMenuItem("Render")).addActionListener(this::renderMenuItemActionPerformed);
                sceneMenu.add(new JMenuItem("IPR")).addActionListener(this::iprMenuItemActionPerformed);

                {
                    clearLogMenuItem = new JCheckBoxMenuItem("Auto Clear Log");
                    sceneMenu.add(clearLogMenuItem);

                    clearLogMenuItem.setToolTipText("Clears the console before building or rendering");
                    clearLogMenuItem.setSelected(true);
                }
                sceneMenu.addSeparator();

                sceneMenu.add(new JMenuItem("Clear Texture Cache")).addActionListener(this::textureCacheClearMenuItemActionPerformed);

                {
                    smallTrianglesMenuItem = new JCheckBoxMenuItem("Low Mem Triangles");
                    sceneMenu.add(smallTrianglesMenuItem);

                    smallTrianglesMenuItem.setToolTipText("Load future meshes using a low memory footprint triangle representation");
                    smallTrianglesMenuItem.addActionListener(this::smallTrianglesMenuItemActionPerformed);
                }
            }
            {
                imageMenu = new JMenu("Image");
                menuBar.add(imageMenu);

                imageMenu.add(new JMenuItem("Reset Zoom")).addActionListener(evt -> imagePanel.reset());
                imageMenu.add(new JMenuItem("Fit to Window")).addActionListener(evt -> imagePanel.fit());


                imageMenu.addSeparator();
                {
                    var imgSaveMenuItem = new JMenuItem("Save Image...");
                    imageMenu.add(imgSaveMenuItem);

                    imgSaveMenuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            // imagePanel.image;
                            JFileChooser fc = new JFileChooser(".");
                            fc.setFileFilter(new FileFilter() {
                                @Override
                                public String getDescription() {
                                    return "Image File";
                                }

                                @Override
                                public boolean accept(File f) {
                                    return (f.isDirectory() || f.getName().endsWith(".png") || f.getName().endsWith(".tga"));
                                }
                            });
                            if (fc.showSaveDialog(SunflowGUI.this) == JFileChooser.APPROVE_OPTION) {
                                String filename = fc.getSelectedFile().getAbsolutePath();
                                imagePanel.save(filename);
                            }
                        }
                    });
                }
            }

            var windowMenu = new JMenu("Window");
            menuBar.add(windowMenu);

            var imageWindowMenuItem = new JMenuItem("Image");
            imageWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
            imageWindowMenuItem.addActionListener(evt -> selectFrame(imagePanelFrame));
            windowMenu.add(imageWindowMenuItem);


            var editorWindowMenuItem = new JMenuItem("Script Editor");
            windowMenu.add(editorWindowMenuItem);

            editorWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
            editorWindowMenuItem.addActionListener(evt -> selectFrame(editorFrame));
            {
                var consoleWindowMenuItem = new JMenuItem("Console");
                windowMenu.add(consoleWindowMenuItem);

                consoleWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
                consoleWindowMenuItem.addActionListener(evt -> selectFrame(consoleFrame));
            }
            windowMenu.addSeparator();
            {
                tileWindowMenuItem = new JMenuItem("Tile");
                windowMenu.add(tileWindowMenuItem);

                tileWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl T"));
                tileWindowMenuItem.addActionListener(this::tileWindowMenuItemActionPerformed);
            }
        }
    }

    private void newFileMenuItemActionPerformed(ActionEvent evt) {
        if (evt != null) {
            // check save?
        }
        // put some template code into the editor
        String template = "import org.sunflow.core.*;\nimport org.sunflow.core.accel.*;\nimport org.sunflow.core.camera.*;\nimport org.sunflow.core.primitive.*;\nimport org.sunflow.core.shader.*;\nimport org.sunflow.image.Color;\nimport org.sunflow.math.*;\n\npublic void build() {\n  // your code goes here\n\n}\n";
        editorTextArea.setText(template);
    }

    private void openFileMenuItemActionPerformed(ActionEvent evt) {
        JFileChooser fc = new JFileChooser(".");
        if (lastSaveDirectory != null)
            fc.setCurrentDirectory(lastSaveDirectory);
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Scene File";
            }

            @Override
            public boolean accept(File f) {
                return (f.isDirectory() || f.getName().endsWith(".sc") || f.getName().endsWith(".java"));
            }
        });

        if (fc.showOpenDialog(SunflowGUI.this) == JFileChooser.APPROVE_OPTION) {
            final String f = fc.getSelectedFile().getAbsolutePath();
            openFile(f);
            lastSaveDirectory = fc.getSelectedFile().getParentFile();
        }
    }

    private void buildMenuItemActionPerformed(ActionEvent evt) {
        new Thread() {
            @Override
            public void run() {
                setEnableInterface(false);
                if (clearLogMenuItem.isSelected())
                    clearConsole();
                Timer t = new Timer();
                t.start();
                try {
                    api = SunflowAPI.compile(editorTextArea.getText());
                } catch (NoClassDefFoundError e) {
                    UI.printError(Module.GUI, "Janino library not found. Please check command line.");
                    api = null;
                }
                if (api != null) {
                    try {
                        if (currentFile != null) {
                            String dir = new File(currentFile).getAbsoluteFile().getParent();
                            api.searchpath("texture", dir);
                            api.searchpath("include", dir);
                        }
                        api.build();
                    } catch (Exception e) {
                        UI.printError(Module.GUI, "Build terminated abnormally: %s", e.getMessage());
                        for (StackTraceElement elt : e.getStackTrace()) {
                            UI.printInfo(Module.GUI, "       at %s", elt.toString());
                        }
                        e.printStackTrace();
                    }
                    t.end();
                    UI.printInfo(Module.GUI, "Build time: %s", t.toString());
                }
                setEnableInterface(true);
            }
        }.start();
    }

    private void clearConsole() {
        consoleTextArea.setText(null);
    }

    private void println(final String s) {
        SwingUtilities.invokeLater(() -> consoleTextArea.append(s + "\n"));
    }

    private void setEnableInterface(boolean enabled) {
        // lock or unlock options which are unsafe during builds or renders
        newFileMenuItem.setEnabled(enabled);
        openFileMenuItem.setEnabled(enabled);
        saveMenuItem.setEnabled(enabled);
        saveAsMenuItem.setEnabled(enabled);
        sceneMenu.setEnabled(enabled);
        buildButton.setEnabled(enabled);
        renderButton.setEnabled(enabled);
        iprButton.setEnabled(enabled);
    }

    @Override
    public void print(Module m, PrintLevel level, String s) {
        if (level == PrintLevel.ERROR)
            JOptionPane.showMessageDialog(SunflowGUI.this, s, String.format("Error - %s", m.name()), JOptionPane.ERROR_MESSAGE);
        println(UI.formatOutput(m, level, s));
    }

    @Override
    public void taskStart(String s, int min, int max) {
        currentTask = s;
        currentTaskLastP = -1;
        final int taskMin = min;
        final int taskMax = max;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                taskProgressBar.setEnabled(true);
                taskCancelButton.setEnabled(true);
                taskProgressBar.setMinimum(taskMin);
                taskProgressBar.setMaximum(taskMax);
                taskProgressBar.setValue(taskMin);
                taskProgressBar.setString(currentTask);
            }
        });
    }

    @Override
    public void taskUpdate(int current) {
        final int taskCurrent = current;
        final String taskString = currentTask;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                taskProgressBar.setValue(taskCurrent);
                int p = (int) (100.0 * taskProgressBar.getPercentComplete());
                if (p > currentTaskLastP) {
                    taskProgressBar.setString(taskString + " [" + p + "%]");
                    currentTaskLastP = p;
                }
            }
        });
    }

    @Override
    public void taskStop() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                taskProgressBar.setValue(taskProgressBar.getMinimum());
                taskProgressBar.setString("");
                taskProgressBar.setEnabled(false);
                taskCancelButton.setEnabled(false);
            }
        });
    }

    private void renderMenuItemActionPerformed(ActionEvent evt) {
        new Thread() {
            @Override
            public void run() {
                setEnableInterface(false);
                if (clearLogMenuItem.isSelected())
                    clearConsole();
                if (api != null) {
                    api.parameter("sampler", "bucket");
                    api.options(SunflowAPI.DEFAULT_OPTIONS);
                    api.render(SunflowAPI.DEFAULT_OPTIONS, imagePanel);
                } else
                    UI.printError(Module.GUI, "Nothing to render!");
                setEnableInterface(true);
            }
        }.start();
    }

    private void iprMenuItemActionPerformed(ActionEvent evt) {
        new Thread() {
            @Override
            public void run() {
                setEnableInterface(false);
                if (clearLogMenuItem.isSelected())
                    clearConsole();
                if (api != null) {
                    api.parameter("sampler", "ipr");
                    api.options(SunflowAPI.DEFAULT_OPTIONS);
                    api.render(SunflowAPI.DEFAULT_OPTIONS, imagePanel);
                } else
                    UI.printError(Module.GUI, "Nothing to IPR!");
                setEnableInterface(true);
            }
        }.start();
    }

    private void textureCacheClearMenuItemActionPerformed(ActionEvent evt) {
        TextureCache.flush();
    }

    private void smallTrianglesMenuItemActionPerformed(ActionEvent evt) {
        TriangleMesh.setSmallTriangles(smallTrianglesMenuItem.isSelected());
    }

    private void saveAsMenuItemActionPerformed(ActionEvent evt) {
        JFileChooser fc = new JFileChooser(".");
        if (lastSaveDirectory != null)
            fc.setCurrentDirectory(lastSaveDirectory);
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Scene File";
            }

            @Override
            public boolean accept(File f) {
                return (f.isDirectory() || f.getName().endsWith(".java"));
            }
        });

        if (fc.showSaveDialog(SunflowGUI.this) == JFileChooser.APPROVE_OPTION) {
            String f = fc.getSelectedFile().getAbsolutePath();
            if (!f.endsWith(".java"))
                f += ".java";
            File file = new File(f);
            if (!file.exists() || JOptionPane.showConfirmDialog(SunflowGUI.this, "This file already exists.\nOverwrite?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // save file
                saveCurrentFile(f);
                lastSaveDirectory = file.getParentFile();
            }
        }
    }

    private void saveCurrentFile(String filename) {
        if (filename == null) {
            // no filename was picked, go to save as dialog
            saveAsMenuItemActionPerformed(null);
            return;
        }

        try(var file = new FileWriter(filename)) {

            // get text from editor pane
            file.write(editorTextArea.getText());

            // update current filename
            currentFile = filename;
            UI.printInfo(Module.GUI, "Saved current script to \"%s\"", filename);
        } catch (IOException e) {
            UI.printError(Module.GUI, "Unable to save: \"%s\"", filename);
            e.printStackTrace();
        }
    }

    private void selectFrame(JInternalFrame frame) {
        try {
            frame.setSelected(true);
            frame.setIcon(false);
        } catch (PropertyVetoException e) {
            // this should never happen
            e.printStackTrace();
        }
    }

    private void tileWindowMenuItemActionPerformed(ActionEvent evt) {
        try {
            if (imagePanelFrame.isIcon())
                imagePanelFrame.setIcon(false);
            if (editorFrame.isIcon())
                editorFrame.setIcon(false);
            if (consoleFrame.isIcon())
                consoleFrame.setIcon(false);

            int width = desktop.getWidth();
            int height = desktop.getHeight();
            int widthLeft = width * 7 / 12;
            int widthRight = width - widthLeft;
            int pad = 2;
            int pad2 = pad + pad;

            imagePanelFrame.reshape(pad, pad, widthLeft - pad2, height - pad2);
            editorFrame.reshape(pad + widthLeft, pad, widthRight - pad2, height / 2 - pad2);
            consoleFrame.reshape(pad + widthLeft, pad + height / 2, widthRight - pad2, height / 2 - pad2);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void openFile(String filename) {
        if (filename.endsWith(".java")) {

            String code = "";

            try {
                code = Files.readString(Paths.get(filename));

                editorTextArea.setText(code);
            } catch (FileNotFoundException e) {
                UI.printError(Module.GUI, "Unable to load: \"%s\"", filename);
                return;
            } catch (IOException e) {
                UI.printError(Module.GUI, "Unable to load: \"%s\"", filename);
                return;
            }
            // load went ok, use filename as current
            currentFile = filename;
            editorFrame.setTitle("Script: "+ filename);
            UI.printInfo(Module.GUI, "Loaded script: \"%s\"", filename);
        } else if (filename.endsWith(".sc")) {
            String template = "import org.sunflow.core.*;\nimport org.sunflow.core.accel.*;\nimport org.sunflow.core.camera.*;\nimport org.sunflow.core.primitive.*;\nimport org.sunflow.core.shader.*;\nimport org.sunflow.image.Color;\nimport org.sunflow.math.*;\n\npublic void build() {\n  include(\"" + filename.replace("\\", "\\\\") + "\");\n}\n";
            editorTextArea.setText(template);
            // no java file associated
            currentFile = null;
            UI.printInfo(Module.GUI, "Created template for \"%s\"", filename);
        } else {
            UI.printError(Module.GUI, "Unknown file format selected");
            return;
        }
        editorTextArea.setCaretPosition(0);
        if (autoBuildMenuItem.isSelected()) {
            // try to compile the code we just loaded
            buildMenuItemActionPerformed(null);
        }


    }

    private class SceneTransferHandler extends TransferHandler {
        @Override
        public boolean importData(JComponent c, Transferable t) {
            if (!sceneMenu.isEnabled())
                return false;
            // can I import it?
            if (!canImport(c, t.getTransferDataFlavors())) {
                return false;
            }
            try {
                // get a List of Files
                List<File> files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                for (var file: files) {

                    String filename = file.getAbsolutePath();
                    // check extension
                    if (filename.endsWith(".sc") || filename.endsWith(".java")) {
                        openFile(filename);
                        // load only one file at a time, stop here
                        break;
                    }
                }
            } catch (Exception exp) {
                // debug
                exp.printStackTrace();
            }

            return false;
        }

        @Override
        public boolean canImport(JComponent c, DataFlavor[] flavors) {
            // Just a quick check to see if a file can be accepted at this time
            // Are there any files around?
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].isFlavorJavaFileListType())
                    return true;
            }

            // guess not
            return false;
        }
    }
}
