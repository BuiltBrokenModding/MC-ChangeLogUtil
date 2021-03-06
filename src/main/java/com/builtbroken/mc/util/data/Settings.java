package com.builtbroken.mc.util.data;

import java.io.File;
import java.util.HashMap;

/**
 * Object that contains all settings for the current run
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/9/2016.
 */
public final class Settings
{
    /** Folder to do all actions inside, root folder */
    public File runDirectory;
    /** Folder to do all actions inside, root folder */
    public File logDirectory;
    /** File to read from */
    public File readFile = null;
    /** File to save to */
    public File saveFile = null;
    /** File containing method translations */
    public File mcpMethodsFile = null;
    /** File containing field translations */
    public File mcpFieldsFile = null;
    /** MCP config folder */
    private File mcpConfigDirectory = null;


    /** Do we auto run after getting all needed data? */
    public boolean autoRun = false;
    /** Should we override the save file? */
    public boolean overrideSave = false;

    private boolean hasValidMCPConfig = false;
    private boolean hasValidatedMCPConfig = false;
    private boolean hasMethodsFile = false;
    private boolean hasFieldsFile = false;

    /**
     * Reads the passed in argument map and applies all valid args
     *
     * @param map - argument map
     */
    public void read(HashMap<String, String> map)
    {
        //General settings
        if (map.containsKey("overrideSave"))
        {
            overrideSave = isTrue(map.get("overrideSave"));
        }

        if (map.containsKey("autoRun"))
        {
            autoRun = isTrue(map.get("autoRun"));
        }

        //File path settings
        if (map.containsKey("runPath"))
        {
            runDirectory = parseFile(runDirectory, map.get("runPath"));
            if (!runDirectory.isDirectory())
            {
                throw new IllegalArgumentException("Invalid run direction: " + runDirectory);
            }
        }
        else
        {
            runDirectory = new File(System.getProperty("user.dir"), "minecraftErrorLogUtility");
        }
        logDirectory = new File(runDirectory, "logs");

        if (map.containsKey("loadPath"))
        {
            readFile = parseFile(runDirectory, map.get("loadPath"));
            if (!runDirectory.exists())
            {
                throw new IllegalArgumentException("Invalid read path: " + runDirectory);
            }
        }

        if (map.containsKey("savePath"))
        {
            saveFile = parseFile(runDirectory, map.get("savePath"));
        }
        else if(autoRun)
        {
            String name = readFile.getName();
            saveFile = new File(readFile.getParent(), name.substring(0, name.lastIndexOf(".")) + "-PARSED" + name.substring(name.lastIndexOf("."), name.length()));
        }

        if (map.containsKey("mcpConfigFolder"))
        {
            setMcpConfigDirectory(parseFile(runDirectory, map.get("mcpConfigFolder")));
        }
    }

    /**
     * Parses a string into a file path
     *
     * @param value - file path
     * @return file
     */
    public File parseFile(String value)
    {
        return parseFile(runDirectory, value);
    }

    /**
     * Used to parse a string as a file path
     *
     * @param root  - root folder of the program
     * @param value - string passed into the program
     * @return new file path
     */
    private File parseFile(File root, String value)
    {
        if (value.startsWith("."))
        {
            String nv = value.replaceFirst(".", "");
            return new File(root, nv);
        }
        else
        {
            return new File(value);
        }
    }

    /**
     * Checks if the value is true
     *
     * @param value
     * @return
     */
    private static boolean isTrue(String value)
    {
        if (value != null)
        {
            return value.toLowerCase().equals("true");
        }
        return true;
    }

    public boolean hasRequiredFiles()
    {
        return hasReadFile() && hasValidSaveFile() && hasValidMCPConfigs();
    }

    public boolean shouldAutoParse()
    {
        return hasRequiredFiles() && autoRun;
    }

    public boolean hasReadFile()
    {
        return readFile != null && readFile.exists();
    }

    public boolean hasValidMCPConfigs()
    {
        if (!hasValidatedMCPConfig)
        {
            validateMCPConfigFile();
        }
        return getMcpConfigDirectory() != null && getMcpConfigDirectory().exists() && hasValidMCPConfig;
    }

    public boolean hasValidSaveFile()
    {
        return saveFile != null && (!saveFile.exists() || overrideSave);
    }

    public void validateMCPConfigFile()
    {
        if (mcpConfigDirectory != null && mcpConfigDirectory.exists())
        {
            mcpMethodsFile = new File(mcpConfigDirectory, "methods.csv");
            mcpFieldsFile = new File(mcpConfigDirectory, "fields.csv");

            //TODO do better validation of files
            hasMethodsFile = mcpMethodsFile.exists();
            hasFieldsFile = mcpFieldsFile.exists();
            hasValidMCPConfig = hasMethodsFile && hasFieldsFile;
            hasValidatedMCPConfig = true;
        }
    }

    /** File containing SRG to read-able names */
    public File getMcpConfigDirectory()
    {
        return mcpConfigDirectory;
    }

    /**
     * Sets the MCP config file and resets any data attached to said file
     *
     * @param mcpConfigDirectory - file
     */
    public void setMcpConfigDirectory(File mcpConfigDirectory)
    {
        this.mcpConfigDirectory = mcpConfigDirectory;

        //Reset data that was dependent on that file
        hasValidatedMCPConfig = true;
        hasFieldsFile = false;
        hasMethodsFile = false;
        mcpMethodsFile = null;
        mcpFieldsFile = null;
    }
}
