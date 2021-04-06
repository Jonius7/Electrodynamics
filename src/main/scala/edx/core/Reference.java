package edx.core;

import java.util.logging.Logger;

public class Reference {

    public static final String id = "EDX";

    /** The official name of the mod */
    public static final String name = "Electrodynamics";
    public static final Logger logger = Logger.getLogger(Reference.name);

    public static final String majorVersion = "@MAJOR@";
    public static final String minorVersion = "@MINOR@";
    public static final String revisionVersion = "@REVIS@";
    public static final String build = "@BUILD@";
    public static final String version = majorVersion + "." + minorVersion + "." + revisionVersion;
    /**
     * Directory Information
     */
    public static final String domain = "edx";
    public static final String prefix = domain + ":";
    public static final String assetDirectory = "/assets/" + domain + "/";
    public static final String textureDirectory = "textures/";
    public static final String guiDirectory = textureDirectory + "gui/";
    public static final String blockTextureDirectory = textureDirectory + "blocks/";
    public static final String itemTextureDirectory = textureDirectory + "items/";
    public static final String modelPath = "models/";
    public static final String modelDirectory = assetDirectory + modelPath;
    public static final String FX_DIRECTORY = textureDirectory + "fx/";
    
}
