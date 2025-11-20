package org.sunflow.system;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sunflow.system.UI.Module;

public class SearchPath {
    private List<String> paths = new ArrayList<>();
    private String type;

    public SearchPath(String type) {
        this.type = type;
    }

    public void resetSearchPath() {
        paths.clear();
    }

    public void addSearchPath(String path) {
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
            try {
                path = f.getCanonicalPath();
                for (String prefix : paths)
                    if (prefix.equals(path))
                        return;
                UI.printInfo(Module.SYS, "Adding %s search path: \"%s\"", type, path);
                paths.add(path);
            } catch (IOException e) {
                UI.printError(Module.SYS, "Invalid %s search path specification: \"%s\" - %s", type, path, e.getMessage());
            }
        } else
            UI.printError(Module.SYS, "Invalid %s search path specification: \"%s\" - invalid directory", type, path);
    }

    public String resolvePath(String filename) {
        // account for relative naming schemes from 3rd party software
        if (filename.startsWith("//"))
            filename = filename.substring(2);
        UI.printDetailed(Module.SYS, "Resolving %s path \"%s\" ...", type, filename);
        File f = new File(filename);
        if (!f.isAbsolute()) {
            for (String prefix : paths) {
                UI.printDetailed(Module.SYS, "  * searching: \"%s\" ...", prefix);
                if (prefix.endsWith(File.separator) || filename.startsWith(File.separator))
                    f = new File(prefix + filename);
                else
                    f = new File(prefix + File.separator + filename);
                if (f.exists()) {
                    // suggested path exists - try it
                    return f.getAbsolutePath();
                }
            }
        }
        // file was not found in the search paths - return the filename itself
        return filename;
    }

    @Override
    public String toString() {
        return type + "search path: " + String.join(":", paths);
    }
}