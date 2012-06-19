package markehme.factionsplus;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import markehme.factionsplus.extras.DCListener;
import markehme.factionsplus.extras.MDListener;
import markehme.factionsplus.extras.Metrics;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/*
	- fixed onEntityDeath

TODO: LIST OF STUFF TO DO
- made maxWarps configuration option work

*/

public class FactionsPlus extends JavaPlugin {
	public static FactionsPlus plugin;
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	Factions factions;
	FPlayers fplayers;
	Faction faction;
	
    public static Permission permission = null;
    public static Economy economy = null;
    
	public static File templatesFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "templates.yml");
	public static File configFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "config.yml");
	
	public static FileConfiguration wconfig;
	public static FileConfiguration config;
	public static FileConfiguration templates;

	public static boolean isMobDisguiseEnabled = false;
	public static boolean isDisguiseCraftEnabled = false;
	public static boolean isWorldEditEnabled = false;
	public static boolean isWorldGuardEnabled = false;
	
	public final FactionsPlusListener FPListener = new FactionsPlusListener();
	
	public final DCListener DCListener = new DCListener();
	public final MDListener MDListener = new MDListener();

	public static WorldEditPlugin worldEditPlugin = null;
	public static WorldGuardPlugin worldGuardPlugin = null;
	
	public static String version;
	public static String FactionsVersion;
	public static Boolean isOnePointSix;

	public void onEnable() { 
		
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(this.FPListener, this);
		
		FactionsPlusJail.server = getServer();
		
		FactionsVersion = (this.getServer().getPluginManager().getPlugin("Factions").getDescription().getVersion());
		if(FactionsVersion.startsWith("1.6")) {
			isOnePointSix = true;
		} else {
			isOnePointSix = false;
		}
		log.info("[FactionsPlus] Factions version " + FactionsVersion + " - " + isOnePointSix.toString());
		
		try {
			if(!new File("plugins" + File.separator + "FactionsPlus" + File.separator).exists()) {
				log.info("[FactionsPlus] Added directory: plugins" + File.separator + "FactionsPlus" + File.separator);
				new File("plugins" + File.separator + "FactionsPlus" + File.separator).mkdir();
			}
		
			if(!new File("plugins" + File.separator + "FactionsPlus" + File.separator + "warps" + File.separator).exists()) {
				new File("plugins" + File.separator + "FactionsPlus" + File.separator + "warps" + File.separator).mkdir();
				log.info("[FactionsPlus] Added directory: plugins" + File.separator + "FactionsPlus" + File.separator + "warps" + File.separator);
			}
			
			if(!new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator).exists()) {
				new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator).mkdir();
				log.info("[FactionsPlus] Added directory: plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator);
			}
		
			if(!new File("plugins" + File.separator + "FactionsPlus" + File.separator + "announcements" + File.separator).exists()) {
				new File("plugins" + File.separator + "FactionsPlus" + File.separator + "announcements" + File.separator).mkdir();
				log.info("[FactionsPlus] Added directory: plugins" + File.separator + "FactionsPlus" + File.separator + "announcements" + File.separator);
			}
			
			if(!new File("plugins" + File.separator + "FactionsPlus" + File.separator + "frules" + File.separator).exists()) {
				new File("plugins" + File.separator + "FactionsPlus" + File.separator + "frules" + File.separator).mkdir();
				log.info("[FactionsPlus] Added directory: plugins" + File.separator + "FactionsPlus" + File.separator + "frules" + File.separator);
			}
			
			if(!new File("plugins" + File.separator + "FactionsPlus" + File.separator + "fbans" + File.separator).exists()) {
				new File("plugins" + File.separator + "FactionsPlus" + File.separator + "fbans" + File.separator).mkdir();
				log.info("[FactionsPlus] Added directory: plugins" + File.separator + "FactionsPlus" + File.separator + "fbans" + File.separator);
			}
			
			if(!new File("plugins" + File.separator + "FactionsPlus" + File.separator + "disabled_in_warzone.txt").exists()) {
				new File("plugins" + File.separator + "FactionsPlus" + File.separator + "disabled_in_warzone.txt").createNewFile();
				log.info("[FactionsPlus] Created file: plugins" + File.separator + "FactionsPlus" + File.separator + "disabled_in_warzone.txt");
			}
			

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!FactionsPlus.configFile.exists()) {
			try {
				FactionsPlus.configFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			wconfig = YamlConfiguration.loadConfiguration(configFile);
			
			configFile.delete();
			configFile.createNewFile();
			
			config = YamlConfiguration.loadConfiguration(configFile);
			
			if(wconfig.isSet("disableUpdateCheck")) {
				config.set("disableUpdateCheck", wconfig.getBoolean("disableUpdateCheck"));
			} else config.set("disableUpdateCheck", false);
			
			if(wconfig.isSet("unDisguiseIfInOwnTerritory")) {
				config.set("unDisguiseIfInOwnTerritory", wconfig.getBoolean("unDisguiseIfInOwnTerritory"));
			} else config.set("unDisguiseIfInOwnTerritory", Boolean.valueOf(false));
			
			if(wconfig.isSet("unDisguiseIfInEnemyTerritory")) {
				config.set("unDisguiseIfInEnemyTerritory", wconfig.getBoolean("unDisguiseIfInEnemyTerritory"));
			} else config.set("unDisguiseIfInEnemyTerritory", Boolean.valueOf(false));
			
			if(wconfig.isSet("leadersCanSetWarps")) {
				config.set("leadersCanSetWarps", wconfig.getBoolean("leadersCanSetWarps"));
			} else config.set("leadersCanSetWarps", true);
			
			if(wconfig.isSet("officersCanSetWarps")) {
				config.set("officersCanSetWarps", wconfig.getBoolean("officersCanSetWarps"));
			} else config.set("officersCanSetWarps", true);
			
			if(wconfig.isSet("membersCanSetWarps")) {
				config.set("membersCanSetWarps", wconfig.getBoolean("membersCanSetWarps"));
			} else config.set("membersCanSetWarps", false);
			
			if(wconfig.isSet("warpSetting")) {
				config.set("warpSetting", wconfig.getInt("warpSetting"));
			} else config.set("warpSetting", Integer.valueOf(1));
			
			if(wconfig.isSet("maxWarps")) {
				config.set("maxWarps", wconfig.getInt("maxWarps"));
			} else config.set("maxWarps", Integer.valueOf(5));
			
			if(wconfig.isSet("mustBeInOwnTerritoryToCreate")) {
				config.set("mustBeInOwnTerritoryToCreate", wconfig.getBoolean("mustBeInOwnTerritoryToCreate"));
			} else config.set("mustBeInOwnTerritoryToCreate", true);
			
			if(wconfig.isSet("smokeEffectOnWarp")) {
				config.set("smokeEffectOnWarp", wconfig.getBoolean("smokeEffectOnWarp"));
			} else config.set("smokeEffectOnWarp", true);
			
			if(wconfig.isSet("powerBoostIfPeaceful")) {
				config.set("powerBoostIfPeaceful", wconfig.getInt("powerBoostIfPeaceful"));
			} else config.set("powerBoostIfPeaceful", Integer.valueOf(0));
			
			if(wconfig.isSet("leadersCanSetJails")) {
				config.set("leadersCanSetJails", wconfig.getBoolean("leadersCanSetJails"));
			} else config.set("leadersCanSetJails", true);
			
			if(wconfig.isSet("officersCanSetJails")) {
				config.set("officersCanSetJails", wconfig.getBoolean("officersCanSetJails"));
			} else config.set("officersCanSetJails", true);
			
			if(wconfig.isSet("leadersCanSetRules")) {
				config.set("leadersCanSetRules", wconfig.getBoolean("leadersCanSetRules"));
			} else config.set("leadersCanSetRules", true);
			
			if(wconfig.isSet("officersCanSetRules")) {
				config.set("officersCanSetRules", wconfig.getBoolean("officersCanSetRules"));
			} else config.set("officersCanSetRules", true);
			
			if(wconfig.isSet("maxRulesPerFaction")) {
				config.set("maxRulesPerFaction", wconfig.getInt("maxRulesPerFaction"));
			} else config.set("maxRulesPerFaction", Integer.valueOf(12));
			
			if(wconfig.isSet("membersCanSetJails")) {
				config.set("membersCanSetJails", wconfig.getBoolean("membersCanSetJails"));
			} else config.set("membersCanSetJails", false);
			
			if(wconfig.isSet("leadersCanJail")) {
				config.set("leadersCanJail", wconfig.getBoolean("leadersCanJail"));
			} else config.set("leadersCanJail", true);
			
			if(wconfig.isSet("officersCanJail")) {
				config.set("officersCanJail", wconfig.getBoolean("officersCanJail"));
			} else config.set("officersCanJail", true);
			
			if(wconfig.isSet("leadersCanAnnounce")) {
				config.set("leadersCanAnnounce", wconfig.getBoolean("leadersCanAnnounce"));
			} else config.set("leadersCanAnnounce", true);
			
			if(wconfig.isSet("officersCanAnnounce")) {
				config.set("officersCanAnnounce", wconfig.getBoolean("officersCanAnnounce"));
			} else config.set("officersCanAnnounce", true);
			
			if(wconfig.isSet("showLastAnnounceOnLogin")) {
				config.set("showLastAnnounceOnLogin", wconfig.getBoolean("showLastAnnounceOnLogin"));
			} else config.set("showLastAnnounceOnLogin", true);
			
			if(wconfig.isSet("showLastAnnounceOnLandEnter")) {
				config.set("showLastAnnounceOnLandEnter", wconfig.getBoolean("showLastAnnounceOnLandEnter"));
			} else config.set("showLastAnnounceOnLandEnter", true);
			
			if(wconfig.isSet("leadersCanFactionBan")) {
				config.set("leadersCanFactionBan", wconfig.getBoolean("leadersCanFactionBan"));
			} else config.set("leadersCanFactionBan", true);
				
			if(wconfig.isSet("officersCanFactionBan")) {
				config.set("officersCanFactionBan", wconfig.getBoolean("officersCanFactionBan"));
			} else config.set("officersCanFactionBan", true);
				
			if(wconfig.isSet("leaderCanNotBeBanned")) {
				config.set("leaderCanNotBeBanned", wconfig.getBoolean("leaderCanNotBeBanned"));
			} else config.set("leaderCanNotBeBanned", true);
			
			if(wconfig.isSet("leadersCanToggleState")) {
				config.set("leadersCanToggleState", wconfig.getBoolean("leadersCanToggleState"));
			} else config.set("leadersCanToggleState", false);
			
			if(wconfig.isSet("officersCanToggleState")) {
				config.set("officersCanToggleState", wconfig.getBoolean("officersCanToggleState"));
			} else config.set("officersCanToggleState", false);
			
			if(wconfig.isSet("membersCanToggleState")) {
				config.set("membersCanToggleState", wconfig.getBoolean("membersCanToggleState"));
			} else config.set("membersCanToggleState", false);
			
			if(wconfig.isSet("extraPowerLossIfDeathByOther")) {
				config.set("extraPowerLossIfDeathByOther", wconfig.getDouble("extraPowerLossIfDeathByOther"));
			} else config.set("extraPowerLossIfDeathByOther", Double.valueOf(0));
			
			if(wconfig.isSet("extraPowerWhenKillPlayer")) {
				config.set("extraPowerWhenKillPlayer", wconfig.getDouble("extraPowerWhenKillPlayer"));
			} else config.set("extraPowerWhenKillPlayer", Double.valueOf(0));
			
			if(wconfig.isSet("extraPowerLossIfDeathBySuicide")) {
				config.set("extraPowerLossIfDeathBySuicide", wconfig.getDouble("extraPowerLossIfDeathBySuicide"));
			} else config.set("extraPowerLossIfDeathBySuicide", Double.valueOf(0));
			
			if(wconfig.isSet("extraPowerLossIfDeathByPVP")) {
				config.set("extraPowerLossIfDeathByPVP", wconfig.getDouble("extraPowerLossIfDeathByPVP"));
			} else config.set("extraPowerLossIfDeathByPVP", Double.valueOf(0));
			
			if(wconfig.isSet("extraPowerLossIfDeathByMob")) {
				config.set("extraPowerLossIfDeathByMob", wconfig.getDouble("extraPowerLossIfDeathByMob"));
			} else config.set("extraPowerLossIfDeathByMob", Double.valueOf(0));
			
			if(wconfig.isSet("extraPowerLossIfDeathByCactus")) {
				config.set("extraPowerLossIfDeathByCactus", wconfig.getDouble("extraPowerLossIfDeathByCactus"));
			} else config.set("extraPowerLossIfDeathByCactus", Double.valueOf(0));
			
			if(wconfig.isSet("extraPowerLossIfDeathByTNT")) {
				config.set("extraPowerLossIfDeathByTNT", wconfig.getDouble("extraPowerLossIfDeathByTNT"));
			} else config.set("extraPowerLossIfDeathByTNT", Double.valueOf(0));
			
			if(wconfig.isSet("extraPowerLossIfDeathByFire")) {
				config.set("extraPowerLossIfDeathByFire", wconfig.getDouble("extraPowerLossIfDeathByFire"));
			} else config.set("extraPowerLossIfDeathByFire", Double.valueOf(0));
			
			if(wconfig.isSet("extraPowerLossIfDeathByPotion")) {
				config.set("extraPowerLossIfDeathByPotion", wconfig.getDouble("extraPowerLossIfDeathByPotion"));
			} else config.set("extraPowerLossIfDeathByPotion", Double.valueOf(0));
			
			if(wconfig.isSet("enablePermissionGroups")) {
				config.set("enablePermissionGroups", wconfig.getBoolean("enablePermissionGroups"));
			} else config.set("enablePermissionGroups", Boolean.valueOf(false));
			
			if(wconfig.isSet("economy_enable")) {
				config.set("economy_enable", wconfig.getBoolean("economy_enable"));
			} else config.set("economy_enable", Boolean.valueOf(false));
				
			if(wconfig.isSet("economy_costToWarp")) {
				config.set("economy_costToWarp", wconfig.getInt("economy_costToWarp"));
			} else config.set("economy_costToWarp", Integer.valueOf(0));
			
			if(wconfig.isSet("economy_costToCreateWarp")) {
				config.set("economy_costToCreateWarp", wconfig.getInt("economy_costToCreateWarp"));
			} else config.set("economy_costToCreateWarp", Integer.valueOf(0));
			
			if(wconfig.isSet("economy_costToDeleteWarp")) {
				config.set("economy_costToDeleteWarp", wconfig.getInt("economy_costToDeleteWarp"));
			} else config.set("economy_costToDeleteWarp", Integer.valueOf(0));
			
			if(wconfig.isSet("economy_costToAnnounce")) {
				config.set("economy_costToAnnounce", wconfig.getInt("economy_costToAnnounce"));
			} else config.set("economy_costToAnnounce", Integer.valueOf(0));
			
			if(wconfig.isSet("economy_costToJail")) {
				config.set("economy_costToJail", wconfig.getInt("economy_costToJail"));
			} else config.set("economy_costToJail", Integer.valueOf(0));
			
			if(wconfig.isSet("economy_costToSetJail")) {
				config.set("economy_costToSetJail", wconfig.getInt("economy_costToSetJail"));
			} else config.set("economy_costToSetJail", Integer.valueOf(0));
			
			if(wconfig.isSet("economy_costToUnJail")) {
				config.set("economy_costToUnJail", wconfig.getInt("economy_costToUnJail"));
			} else config.set("economy_costToUnJail", Integer.valueOf(0));
				
			if(wconfig.isSet("economy_costToToggleUpPeaceful")) {
				config.set("economy_costToToggleUpPeaceful", wconfig.getInt("economy_costToToggleUpPeaceful"));
			} else config.set("economy_costToToggleUpPeaceful", Integer.valueOf(0));
			
			if(wconfig.isSet("economy_costToToggleDownPeaceful")) {
				config.set("economy_costToToggleDownPeaceful", wconfig.getInt("economy_costToToggleDownPeaceful"));
			} else config.set("economy_costToToggleDownPeaceful", Integer.valueOf(0));
			
			config.set("DoNotChangeMe", Integer.valueOf(8));
			
			config.save(configFile);
			
			saveConfig();
		} catch(Exception e) {
		   	e.printStackTrace();
		   	log.info("[FactionsPlus] An error occured while managing the configuration file (-18)");
		   	getPluginLoader().disablePlugin(this);
		}		
		
		if (!templatesFile.exists()) {
			
			FactionsPlusTemplates.createTemplatesFile();
		    
		} 
		
		templates = YamlConfiguration.loadConfiguration(templatesFile);	
		config = YamlConfiguration.loadConfiguration(configFile);
		
		FactionsPlusCommandManager.setup();
		FactionsPlusHelpModifier.modify();
		
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        
        
        if(config.getBoolean("economy_enable")) {
        	RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        	
        	if (economyProvider != null) {
            	economy = economyProvider.getProvider();
        	}
        }
        
        if(getServer().getPluginManager().isPluginEnabled("DisguiseCraft")) {
        	pm.registerEvents(this.DCListener, this);
        	log.info("[FactionsPlus] Hooked into DisguiseCraft!");
        	isDisguiseCraftEnabled = true;
        }
        
        if(getServer().getPluginManager().isPluginEnabled("MobDisguise")) {
        	pm.registerEvents(this.MDListener, this);
        	log.info("[FactionsPlus] Hooked into MobDisguise!");
        	isMobDisguiseEnabled = true;
        }
        
        if(getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
        	worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        	log.info("[FactionsPlus] Hooked into WorldEdit!");
        	isWorldEditEnabled = true;
        }
        
        if(getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
        	worldGuardPlugin = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        	log.info("[FactionsPlus] Hooked into WorldGuard!");
        	isWorldGuardEnabled = true;
        }
        
        FactionsPlus.config = YamlConfiguration.loadConfiguration(FactionsPlus.configFile);
        
        version = getDescription().getVersion();
        
        FactionsPlusUpdate.checkUpdates();
        
		log.info("[FactionsPlus] Ready.");
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    log.info("[FactionsPlus] Waah! Couldn't metrics-up! :'(");
		}
	}
	

	public void onDisable() {
		log.info("[FactionsPlus] Disabled.");
	}
	
}