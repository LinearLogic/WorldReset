package ss.linearlogic.worldreset;

import ss.linearlogic.worldreset.WorldReset;
import ss.linearlogic.worldreset.util.WRLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class WRWorldManager
{
	private WorldReset plugin;
	public WRWorldManager(WorldReset wr)
	{
		this.plugin = wr;
	}
	
	public void importWorlds()
	{
		File backupDir = new File(this.plugin.getDataFolder(), "backups");
				
		for (File source : backupDir.listFiles())
		{
			if (source.isDirectory()) {
				File target = new File(this.plugin.getServer().getWorldContainer(), source.getName());
				if (target.exists() && target.isDirectory()) { //delete the old world folder
					try
					{
						delete(target);
					} catch(IOException e) {
						e.printStackTrace();
						WRLogger.logSevere("Failed to reset world \"" + source.getName() + "\" - could not delete old world folder.");
						continue;
					}
				}
				
				try
				{
		        	copyDir(source, target); //import the new world folder from the plugin's backup directory
				} catch(IOException e) {
		        	e.printStackTrace();
					WRLogger.logSevere("Failed to reset world \"" + source.getName() + "\" - could not import the world from backup");
					continue;
				}
				WRLogger.logInfo("Import of world \"" + source.getName() + "\" succeeded!");
			}
		}
    }
 
	public void delete(File file)
		throws IOException
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
	
    private static void copyDir(File source, File target)
    	throws IOException
    {
 
    	if(source.isDirectory())
    	{
    		if(!target.exists())
    		{
    		   target.mkdir();
    		}
    		String files[] = source.list();
 
    		for (String file : files) {
    		   File srcFile = new File(source, file);
    		   File destFile = new File(target, file);
    		   copyDir(srcFile, destFile);
    		}
    	}
    	else
    	{
    		InputStream in = new FileInputStream(source);
    	    OutputStream out = new FileOutputStream(target); 
 
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
