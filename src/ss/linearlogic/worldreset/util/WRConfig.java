package ss.linearlogic.worldreset.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ss.linearlogic.worldreset.WorldReset;

public class WRConfig
{

	private WorldReset plugin;
	public File configFile;
	public FileConfiguration config;
	
	public WRConfig(WorldReset plugin) {
		this.plugin = plugin;
	}

	public void setupConfig()
	{
		configFile = new File(plugin.getDataFolder(), "config.yml");
		try {
            firstRunConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
		config = new YamlConfiguration();
	}

	public void firstRunConfiguration() throws Exception
	{
		if(!configFile.exists())
		{
			configFile.getParentFile().mkdirs();
			copy(plugin.getResource("config.yml"), configFile);
		}
	}
	
	public void copy(InputStream in, File file)
	{
		try
		{
			OutputStream out = new FileOutputStream(file);
			byte[] bBuffer = new byte[1024];
			int len;
			while ((len = in.read(bBuffer)) > 0)
			{
				out.write(bBuffer, 0, len);
			}
			out.close();
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadConfig()
	{
		try
		{
			config.load(configFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveConfig()
	{
		try
		{
			config.save(configFile);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

}
