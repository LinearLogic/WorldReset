package ss.linearlogic.worldreset;

import ss.linearlogic.worldreset.WorldReset;
import ss.linearlogic.worldreset.WRLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;

public class WRWorldManager
{
	private WorldReset plugin;
	public WRWorldManager(WorldReset wr)
	{
		this.plugin = wr;
	}
	
	public void delete(File file)
	{
		if (file.isDirectory())
		{
			for (File subfile : file.listFiles())
				delete(subfile);
		}
		if (!file.delete())
		{
			WRLogger.logSevere("While attempting world reset, failed to delete: " + file);
		}
	}
	
	public void importWorld()
	{
    	File source = new File(this.plugin.getDataFolder(), "world");
    	File target = new File(Bukkit.getServer().getWorldContainer(), "world");
    	if(!source.exists())
    	{
    		WRLogger.logSevere("World reset failed!");
    		WRLogger.logSevere("Could not find a source directory for the world import.");
        }
    	else
    	{
           try
           {
        	copyDir(source,target);
           } catch(IOException e) {
        	e.printStackTrace();
			WRLogger.logSevere("World reset failed!");
           }
        }
    }
 
    private static void copyDir(File src, File trg)
    	throws IOException
    {
 
    	if(src.isDirectory())
    	{
    		if(!trg.exists())
    		{
    		   trg.mkdir();
    		}
    		String files[] = src.list();
 
    		for (String file : files) {
    		   File srcFile = new File(src, file);
    		   File destFile = new File(trg, file);
    		   copyDir(srcFile,destFile);
    		}
    	}
    	else
    	{
    		InputStream in = new FileInputStream(src);
    	    OutputStream out = new FileOutputStream(trg); 
 
    	    byte[] buffer = new byte[1024];
 
	        int length;
	        //copy the file content in bytes 
	        while ((length = in.read(buffer)) > 0)
	        {
	        	out.write(buffer, 0, length);
    	    }
 
	        in.close();
	        out.close();
    	}
    }
}
