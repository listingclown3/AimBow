package net.famzangl.minecraft.aimbow;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AimBowCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "aimbow";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/aimbow";
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft.getMinecraft().displayGuiScreen(new AimBowCommandGui());
        MinecraftForge.EVENT_BUS.unregister(this);

    }
}
