/*
 * This file is part of Mage Flame.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * Mage Flame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mage Flame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURCoordsE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.client;

import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.awt.*;

/**
 * @author Mark Gottschling on 1/24/2025
 */
public class LifespanBar {
    private static final int HUD_OFFSET_WIDTH = 0;
    private static final int HUD_OFFSET_HEIGHT = 0;

    public static boolean renderLevelBar(GuiGraphics drawContext, final ISummonedEntity summonedEntity) {

        // ensure the entity displays lifespan
        // ie entities with infinte lifespans don't display
        if (((ISummonedEntity)summonedEntity).getLifespan() == Integer.MAX_VALUE) {
            return false;
        }

        Minecraft client = Minecraft.getInstance();
        int clientWidth = client.getWindow().getGuiScaledWidth();
        int clientHeight = client.getWindow().getGuiScaledHeight();

        // middle of the screen
        int centerWidth = clientWidth / 2 - HUD_OFFSET_WIDTH;
        int centerHeight = clientHeight / 2 - HUD_OFFSET_HEIGHT;

//            int xOffset = ClientConfig.hudXOffset;
//            int yOffset = ClientConfig.hudYOffset;

        /*
         * only recalc offsets for integration if the config offsets are still default values
         */
//            int integrationXOffset = 0;
//            int integrationYOffset = 0;
//            if (xOffset == 0 && yOffset == 0) {
//                if (WailaIntegration.isEnabled()) {
//                    integrationXOffset = WAILA_INTEGRATION_XOFFSET;
//                }
//            }

        // update static variable in the event handler
//            HudEventHandler.startX = xOffset + centerWidth + integrationXOffset;
//            HudEventHandler.startY = yOffset + 1 + integrationYOffset;

//			drawContext.getMatrices().push();
////			drawContext.getMatrices().translate(0.0F, 0.0F, 100.0F);
//			int ii = centerWidth - (64 / 2);
//			int jj = centerHeight - (21 / 2);
//			drawContext.drawTexture(ClientConfig.useDarkHud ? HUD_DARK_BG : HUD_BG, ii, jj, 0, 0, 64, 20, 64, 20);
//			drawContext.getMatrices().pop();

        // draw bg texture
        // 0 = startx, 0 = starty, 64 = endx, 20 = endy, 64 = width of image, 20 = height of image
//            drawContext.drawTexture(ClientConfig.useDarkHud ? HUD_DARK_BG : HUD_BG,
//                    xOffset + centerWidth + integrationXOffset, yOffset + centerHeight + integrationYOffset, 0, 0, 64, 20, 64, 20);

        // convert lifespan (ticks) to milliseconds
        int ticks = ((ISummonedEntity)summonedEntity).getLifespan();
        long milliseconds = (ticks / 2) * 100L; // divide by 2 and multiple by 100 instead of 20 & 1000

        // display the level text
        String text = DurationFormatUtils.formatDuration(milliseconds, "mm:ss"); //.getName().getString();
        int textWidth = client.font.width(text);
        int fontHeight = client.font.lineHeight;
        int xPos = centerWidth - textWidth / 2;
        int yPos = centerHeight + fontHeight -3;

        drawContext.drawString(client.font, text,
//                            xPos + xOffset + integrationXOffset,
//                            yPos + yOffset + integrationYOffset,
                        xPos,
                        yPos,
                        Color.WHITE.getRGB());

        return true;
    }
}
