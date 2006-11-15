package org.sunflow.core;

import java.util.HashMap;

import org.sunflow.system.UI;
import org.sunflow.system.UI.Module;

/**
 * Maintains a cache of all loaded texture maps. This is usefull if the same
 * texture might be used more than once in your scene.
 */
public final class TextureCache {
    private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

    private TextureCache() {
    }

    /**
     * Gets a reference to the texture specified by the given filename. If the
     * texture has already been loaded the previous reference is returned,
     * otherwise, a new texture is created.
     * 
     * @param filename image file to load
     * @return texture object
     * @see Texture
     */
    public synchronized static Texture getTexture(String filename) {
        if (textures.containsKey(filename)) {
            UI.printInfo(Module.TEX, "Using cached file \"%s\" ...", filename);
            return textures.get(filename);
        }
        UI.printInfo(Module.TEX, "Loading file \"%s\" ...", filename);
        Texture t = new Texture(filename);
        textures.put(filename, t);
        return t;
    }

    public synchronized static void flush() {
        UI.printInfo(Module.TEX, "Flushing texture cache");
        textures.clear();
    }
}