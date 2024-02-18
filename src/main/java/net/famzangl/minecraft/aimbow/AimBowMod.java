/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.aimbow;

import net.famzangl.minecraft.aimbow.aiming.BowRayData;
import net.famzangl.minecraft.aimbow.aiming.ColissionSolver;
import net.famzangl.minecraft.aimbow.aiming.RayData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@Mod(modid="aimbow-mod", name = "AimBow", version = "0.1.0")
public class AimBowMod {

	@Instance(value = "minebot-mod")
	public static AimBowMod instance;

	public static void chat(String message) {
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		final net.famzangl.minecraft.aimbow.AimBowController controller = new net.famzangl.minecraft.aimbow.AimBowController();
		controller.initialize();
		MinecraftForge.EVENT_BUS.register(this);

	}

	public static String getVersion() {
		return AimBowMod.class.getAnnotation(Mod.class).version();
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().getRenderViewEntity() == null) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glTranslated(-Minecraft.getMinecraft().getRenderManager().viewerPosX,
				-Minecraft.getMinecraft().getRenderManager().viewerPosY,
				-Minecraft.getMinecraft().getRenderManager().viewerPosZ);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();

		worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		// if (Minecraft.getMinecraft().thePlayer.getHeldItem() instanceof BowItem)

		for (Vec3 point : RayData.trajectory) {
			worldRenderer.pos(point.xCoord, point.yCoord, point.zCoord).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
		}

		tessellator.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glPopMatrix();
		BowRayData.trajectory.clear();
		RayData.trajectory.clear();



	}


}
