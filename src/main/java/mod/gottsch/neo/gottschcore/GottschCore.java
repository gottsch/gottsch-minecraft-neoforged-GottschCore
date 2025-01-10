/*
 * This file is part of  GottschCore.
 * Copyright (c) 2020 Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neo.gottschcore;

import mod.gottsch.neo.gottschcore.config.Config;
import mod.gottsch.neo.gottschcore.setup.CommonSetup;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Mark Gottschling on Feb 26, 2020
 *
 */
@Mod(value = GottschCore.MODID)
public class GottschCore {
	// logger
	public static final Logger LOGGER = LogManager.getLogger(GottschCore.class.getSimpleName());

	// constants
	public static final String MODID = "gottschcore";
	
	public static GottschCore instance;

	public GottschCore(IEventBus modEventBus, ModContainer modContainer) {
		GottschCore.instance = this;

		// register config
		Config.register(modContainer);

		// register 'ModSetup::init' to be called at mod setup time (server and client)
		modEventBus.addListener(this::setup);
	}

	/**
	 * ie. preint
	 * 
	 * @param event
	 */
	private void setup(final FMLCommonSetupEvent event) {
		CommonSetup.init(MODID, Config.instance, event);
	}

}
