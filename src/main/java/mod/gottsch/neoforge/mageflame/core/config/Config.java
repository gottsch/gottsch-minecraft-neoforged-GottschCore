/*
 * This file is part of  Mage Flame.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * Mage Flame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mage Flame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.config;


import mod.gottsch.neo.gottschcore.config.AbstractConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 
 * @author Mark Gottschling Jan 19, 2023
 *
 */
public class Config extends AbstractConfig {
	public static final String CATEGORY_DIV = "##############################";
	public static final String UNDERLINE_DIV = "------------------------------";

	// TODO change to the new Echelons style of config setup
	protected static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
	protected static final ModConfigSpec.Builder	CLIENT_BUILDER = new ModConfigSpec.Builder();
	protected static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
	
	public static ModConfigSpec COMMON_CONFIG;
	public static ModConfigSpec CLIENT_CONFIG;
	public static ModConfigSpec SERVER_CONFIG;
	
	public static final Logging LOGGING;
	public static final ClientConfig CLIENT;
	public static final ServerConfig SERVER;
	public static Config instance = new Config();
	
	static {
		LOGGING = new Logging(COMMON_BUILDER);
		COMMON_CONFIG = COMMON_BUILDER.build();

		CLIENT = new ClientConfig(CLIENT_BUILDER);
		CLIENT_CONFIG = CLIENT_BUILDER.build();
		SERVER = new ServerConfig(SERVER_BUILDER);
		SERVER_CONFIG = SERVER_BUILDER.build();
	}

	public static class ClientConfig {
		public ModConfigSpec.BooleanValue enableLifespanDisplay;
		public ClientConfig(ModConfigSpec.Builder builder) {

			builder.comment(CATEGORY_DIV, "Client / GUI Properties", CATEGORY_DIV)
					.push("gui");

			enableLifespanDisplay = builder
					.comment(" Enable / disable gui display of light entity lifespan.")
					.define("enableLifespanDisplay", true);

			builder.pop();
		}
	}

	/*
	 * 
	 */
	public static class ServerConfig {
		public ModConfigSpec.IntValue mageFlameLifespan;
		public ModConfigSpec.IntValue lesserRevelationLifespan;
		public ModConfigSpec.IntValue greaterRevelationLifespan;
		public ModConfigSpec.BooleanValue isEmberHoundLifespanInfinite;
		public ModConfigSpec.IntValue bubbleFlameLifespan;
		public ModConfigSpec.IntValue emberHoundLifespan;
		public ModConfigSpec.IntValue glowglobLifespan;
		public ModConfigSpec.IntValue maxSummonedEntitiesPerPlayer;
		@Deprecated
		public ModConfigSpec.IntValue updateLightTicks;

		public ServerConfig(ModConfigSpec.Builder builder) {

			builder.comment(CATEGORY_DIV, "Flame / Torch Entity Properties", CATEGORY_DIV)
			.push("entities");
			
			mageFlameLifespan = builder
					.comment(" The lifespan of a Mage Flame spell/entity in ticks.", 
							"Ex. 20 ticks * 60 seconds * 5 = 6000 = 5 minutes.")
					.defineInRange("mageFlameLifespan", 12000, 1200, 72000);

			lesserRevelationLifespan = builder
					.comment(" The lifespan of a Lesser Revelation spell/entity in ticks.")
					.defineInRange("lesserRevelationLifespan", 18000, 1200, 72000);
			
			greaterRevelationLifespan = builder
					.comment(" The lifespan of a Greater Revelation spell/entity in ticks.")
					.defineInRange("greaterRevelationLifespan", 36000, 1200, 72000);

			bubbleFlameLifespan = builder
					.comment(" The lifespan of a Bubble Flame spell/entity in ticks.",
							"Ex. 20 ticks * 60 seconds * 5 = 6000 = 5 minutes.")
					.defineInRange("mageFlameLifespan", 12000, 1200, 72000);

			glowglobLifespan = builder
					.comment(" The lifespan of a Glowglob spell/entity in ticks.")
					.defineInRange("glowglobLifespan", 12000, 1200, 72000);

			isEmberHoundLifespanInfinite = builder
					.comment(" Enable infinite lifespan for the Ember Hound entity.")
					.define("isEmberHoundLifespanInfinite", true);

			emberHoundLifespan = builder
					.comment(" The lifespan of a Ember Hound spell/entity in ticks.",
							" Note - this is only activated if infinite lifespan = false.")
					.defineInRange("emberHoundLifespan", 72000, 1200, 72000);

			maxSummonedEntitiesPerPlayer = builder
					.comment(" The max number of concurrent summoned entities per player.")
					.defineInRange("maxSummonedEntitiesPerPlayer", 1, 1, 10);

			updateLightTicks = builder
					.comment(" The number of ticks before the next light update.")
					.defineInRange("updateLightTicks", 2, 1, 20);

			builder.pop();
		}
	}
	
	@Override
	public String getLogsFolder() {
		return Config.LOGGING.folder.get();
	}
	
	public void setLogsFolder(String folder) {
		Config.LOGGING.folder.set(folder);
	}
	
	@Override
	public String getLoggingLevel() {
		return Config.LOGGING.level.get();
	}
}
