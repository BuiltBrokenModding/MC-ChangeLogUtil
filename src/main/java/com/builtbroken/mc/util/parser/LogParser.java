package com.builtbroken.mc.util.parser;

import com.builtbroken.mc.util.data.MCPData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/9/2016.
 */
public class LogParser
{
    //File to read
    private final File readFile;

    //Lines read from file
    private List<String> lines = new ArrayList();

    private List<String> unknownEntries = new ArrayList();

    public int stringsReplaced = 0;
    public int linesEdited = 0;

    /**
     * Creates a new parsing object
     *
     * @param file - file to parse
     */
    public LogParser(File file)
    {
        readFile = file;
    }

    /**
     * Called to read the file from disk
     */
    public void readFile()
    {
        lines.clear();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(readFile));

            String line;
            while ((line = br.readLine()) != null)
            {
                lines.add(line);
            }
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Replaces the lines srg values
     *
     * @param data - MPC data map
     */
    public void replaceSRG(MCPData data)
    {
        stringsReplaced = 0;
        linesEdited = 0;

        ListIterator<String> it = lines.listIterator();
        while (it.hasNext())
        {
            String line = it.next();
            int index = line.indexOf("func_");
            if (index > -1)
            {
                String editedLine = line;

                String entry = line.substring(index, line.indexOf("("));
                String convert = data.convertSrgMethod(entry);
                if(!entry.equals(convert))
                {
                    stringsReplaced++;
                    editedLine = editedLine.replace(entry, convert);
                }
                else if(!unknownEntries.contains(line))
                {
                    unknownEntries.add(line);
                }
                //TODO more editing?
                if (!editedLine.equals(line))
                {
                    linesEdited++;
                    it.set(editedLine);
                }
            }
        }
    }

    public void save(File saveFile) throws IOException
    {
        Path file = Paths.get(saveFile.toURI());
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    public void saveLog(File saveFile) throws IOException
    {
        Path file = Paths.get(saveFile.toURI());
        Files.write(file, unknownEntries, Charset.forName("UTF-8"));
    }
}
