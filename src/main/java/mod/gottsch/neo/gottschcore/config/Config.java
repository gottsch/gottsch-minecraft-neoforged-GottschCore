package mod.gottsch.neo.gottschcore.config;


import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 
 * @author Mark Gottschling on Oct 19, 2022
 *
 */
public class Config extends AbstractConfig {
	// setup as a singleton
	public static Config instance = new Config();
	
	private Config() {}
	
	/**
	 * 
	 */
	public static void register() {
		registerCommonConfigs();
		// perform any initializations on data
		Config.init();
	}
	
	public static class CommonConfig {
		public static Logging LOGGING;
	}
	
	/**
	 * 
	 */
	private static void registerCommonConfigs() {
		ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
		CommonConfig.LOGGING = new Logging(COMMON_BUILDER);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
	}
	
	private static void init() {
		
	}
}
