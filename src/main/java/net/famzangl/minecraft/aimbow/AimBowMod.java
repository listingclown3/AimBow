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

import net.famzangl.minecraft.aimbow.aiming.RayData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.io.File;

import static net.famzangl.minecraft.aimbow.AimbowGui.renderTrajectory;
import static net.famzangl.minecraft.aimbow.aiming.Bow.BowColissionSolver.force;

@Mod(modid="aimbow-mod", name = "AimBow", version = "0.1.0")
public class AimBowMod {

	@Instance(value = "minebot-mod")
	public static AimBowMod instance;

	public static int red;
	public static int green;
	public static int blue;
	public static int alpha;
	public static int width;
	public static boolean crossHairState;
	public static boolean blockDistanceState;
	public static boolean TrajectoryState;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		final net.famzangl.minecraft.aimbow.AimBowController controller = new net.famzangl.minecraft.aimbow.AimBowController();
		controller.initialize();

		Configuration config = new Configuration(new File("config/AimBowColorGui.cfg"));
		config.load();

		ClientCommandHandler.instance.registerCommand(new AimBowCommand());
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new AimbowGui());

		red = config.get("Color", "Red", 255).getInt();
		green = config.get("Color", "Green", 255).getInt();
		blue = config.get("Color", "Blue", 255).getInt();
		alpha = config.get("Color", "Alpha", 255).getInt();
		width = config.get("Color", "Width", 3).getInt();
		crossHairState = config.get("General", "CrossHairState", false).getBoolean();
		blockDistanceState = config.get("General", "HighlightLandingBlockState", false).getBoolean();
		TrajectoryState = config.get("General", "Trajectory", true).getBoolean();

	}

	public static String getVersion() {
		return AimBowMod.class.getAnnotation(Mod.class).version();
	}

	@SubscribeEvent
	public void onRender3D(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().thePlayer != null) {
			drawCollisionBox(event.partialTicks);
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().getRenderViewEntity() == null) {
			return;
		}

		if (TrajectoryState) {
			RayData.trajectory.clear();
		}
	}


	public void drawCollisionBox(float partialTicks) {
		if (!RayData.trajectory.isEmpty()) {
			// Get the last position from the trajectory
			Vec3 lastPos = RayData.trajectory.get(RayData.trajectory.size() - 1);

			// Convert Vec3 to BlockPos
			BlockPos endBlock = new BlockPos(lastPos.xCoord, lastPos.yCoord, lastPos.zCoord);

			// Draw the box around this block
			drawBlockHighlight(endBlock, partialTicks);
		} else {
			return;

		}
	}

	private void drawBlockHighlight(BlockPos pos, float partialTicks) {
		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
		double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
		double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GL11.glLineWidth(2.0f);

		// Set color
		GL11.glColor4f(red, green, blue, alpha);

		// Draw box
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		AxisAlignedBB box = new AxisAlignedBB(
				pos.getX(), pos.getY(), pos.getZ(),
				pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
		).expand(0.002, 0.002, 0.002)
				.offset(-viewerX, -viewerY, -viewerZ);

		// Draw outline
		worldrenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

		// Bottom
		worldrenderer.pos(box.minX, box.minY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
		worldrenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
		worldrenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
		worldrenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
		worldrenderer.pos(box.minX, box.minY, box.minZ).endVertex();

		// Top
		worldrenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
		worldrenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
		worldrenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
		worldrenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
		worldrenderer.pos(box.minX, box.maxY, box.minZ).endVertex();

		// Verticals
		worldrenderer.pos(box.minX, box.minY, box.minZ).endVertex();
		worldrenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
		worldrenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
		worldrenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
		worldrenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
		worldrenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();

		tessellator.draw();

		// Reset GL states
		GL11.glLineWidth(1.0F);
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}


}
