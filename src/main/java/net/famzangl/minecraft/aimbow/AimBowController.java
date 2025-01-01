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

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import org.lwjgl.input.Keyboard;

import static net.famzangl.minecraft.aimbow.AimBowMod.TrajectoryState;

public class AimBowController {
	protected static final KeyBinding autoAimKey = new KeyBinding("Auto aim",
			Keyboard.getKeyIndex("Y"), "AimBow");
	static {
		ClientRegistry.registerKeyBinding(autoAimKey);
	}

	private AimbowGui gui;
	private boolean guiSet;

	/**
	 * Checks if the Bot is active and what it should do.
	 * 
	 * @param evt
	 */
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent evt) {
		if (evt.phase != ClientTickEvent.Phase.START
				|| Minecraft.getMinecraft().thePlayer == null) {
			return;
		}
		if (!guiSet) {
			Minecraft minecraft = Minecraft.getMinecraft();
			minecraft.ingameGUI = gui;
			
			guiSet = true;
		}
		
		if (autoAimKey.isPressed()) {
			if (TrajectoryState) {
				gui.autoAim = !gui.autoAim;

				Minecraft.getMinecraft().thePlayer
						.addChatMessage(new ChatComponentText("Autoaim: "
								+ (gui.autoAim ? "On" : "Off")));
			} else {
				Minecraft.getMinecraft().thePlayer
						.addChatMessage(new ChatComponentText("Enable Trajectory Permissions First! /aimbow"));

			}
		}
	}

	// public Pos2 getPositionOnScreenOld(Minecraft mc, double x, double y,
	// double z, Pre event) {
	//
	// EntityLivingBase entitylivingbase = mc.renderViewEntity;
	// // double playerX = entitylivingbase.lastTickPosX
	// // + (entitylivingbase.posX - entitylivingbase.lastTickPosX)
	// // * partialTicks;
	// // double playerY = entitylivingbase.lastTickPosY
	// // + (entitylivingbase.posY - entitylivingbase.lastTickPosY)
	// // * partialTicks;
	// // double playerZ = entitylivingbase.lastTickPosZ
	// // + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ)
	// // * partialTicks;
	// // entitylivingbase.
	// Vec3 player = entitylivingbase.getPosition(event.partialTicks);
	// Vec3 looking = entitylivingbase.getLook(event.partialTicks);
	//
	// Vec3 pos = Vec3.createVectorHelper(x, y, z);
	// Vec3 marking = pos.addVector(-player.xCoord, -player.yCoord,
	// -player.zCoord).normalize();
	// System.out.println("Current d: " + marking);
	// marking.rotateAroundY((float) (entitylivingbase.rotationYaw / 180 *
	// Math.PI));
	// System.out.println("Current d: " + marking);
	// marking.rotateAroundX((float) (entitylivingbase.rotationPitch / 180 *
	// Math.PI));
	// System.out.println("Current d: " + marking);
	// Vec3 screenEdge = Vec3.createVectorHelper(-0.7235458831104901,
	// 0.407043401367207, 0.5574916588955217);
	//
	// double fovY = 70.0F;
	// fovY += mc.gameSettings.fovSetting * 40.0F;
	// fovY *= mc.thePlayer.getFOVMultiplier();
	// System.out.println("Real FOV: " + fovY);
	// fovY *= Math.PI / 180.0 / 2;
	// double fovX = fovY * mc.displayWidth / mc.displayHeight;
	//
	// Vec3 xz = Vec3.createVectorHelper(marking.xCoord, 0, marking.zCoord)
	// .normalize();
	// Vec3 xy = Vec3.createVectorHelper(0, marking.yCoord, marking.zCoord)
	// .normalize();
	// double angX = Math.asin(xz.xCoord);
	// double angY = Math.asin(xy.yCoord);
	//
	// System.out.println("FOV: " + fovX + "," + fovY + "; mark: " + angX
	// + "," + angY);
	//
	// double screenX = event.resolution.getScaledWidth() * (-angX / fovX + 1)
	// / 2.0;
	// double screenY = event.resolution.getScaledHeight()
	// * (-angY / fovY + 1) / 2.0;
	//
	// System.out.println("On Screen: " + screenX + "," + screenY);
	//
	// return new Pos2((int) screenX, (int) screenY);
	// }

	public void initialize() {
		FMLCommonHandler.instance().bus().register(this);
		Minecraft minecraft = Minecraft.getMinecraft();
		gui = new AimbowGui(minecraft);
	}
}
