package net.famzangl.minecraft.aimbow;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AimBowColorCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "aimbowcolor";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/aimbowcolor";
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
        Minecraft.getMinecraft().displayGuiScreen(new AimBowColorGui());
        MinecraftForge.EVENT_BUS.unregister(this);

    }
}
