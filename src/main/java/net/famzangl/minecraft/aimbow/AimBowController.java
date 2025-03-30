package net.famzangl.minecraft.aimbow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
	private boolean initialized;

	// Fixed version removes the GUI replacement and uses proper event registration
	public void initialize() {
		if (!initialized) {
			this.gui = new AimbowGui();
			// Register both the controller and GUI with the event bus
			MinecraftForge.EVENT_BUS.register(this);
			MinecraftForge.EVENT_BUS.register(gui);
			initialized = true;
		}
	}

	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent evt) {
		if (evt.phase != ClientTickEvent.Phase.START
				|| Minecraft.getMinecraft().thePlayer == null) {
			return;
		}

		if (autoAimKey.isPressed()) {
			handleAutoAimToggle();
		}
	}

	private void handleAutoAimToggle() {
		if (TrajectoryState) {
			gui.autoAim = !gui.autoAim;
			sendChatMessage("Autoaim: " + (gui.autoAim ? "On" : "Off"));
		} else {
			sendChatMessage("Enable Trajectory First! /aimbow");
		}
	}

	private void sendChatMessage(String message) {
		Minecraft.getMinecraft().thePlayer.addChatMessage(
				new ChatComponentText(message)
		);
	}

	// Remove all GUI replacement code and position calculation methods
}