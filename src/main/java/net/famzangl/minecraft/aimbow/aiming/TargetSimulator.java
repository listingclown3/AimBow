package net.famzangl.minecraft.aimbow.aiming;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class TargetSimulator {
    private static final float GRAVITY = 0.08f;
    private static final float DRAG = 0.98f;
    
    public static Vec3 simulateJump(Entity target, int ticks) {
        double x = target.posX;
        double y = target.posY;
        double z = target.posZ;
        double mx = target.motionX;
        double my = target.motionY;
        double mz = target.motionZ;
        
        for (int i = 0; i < ticks; i++) {
            x += mx;
            y += my;
            z += mz;
            mx *= DRAG;
            my = (my - GRAVITY) * DRAG;
            mz *= DRAG;
        }
        
        return new Vec3(x, y, z);
    }
    
    public static Vec3 simulatePath(Entity target, int ticks) {
        Minecraft mc = Minecraft.getMinecraft();
        double x = target.posX;
        double z = target.posZ;
        
        float yaw = target.rotationYaw * 0.017453292f;
        double dx = -MathHelper.sin(yaw) * 0.2;
        double dz = MathHelper.cos(yaw) * 0.2;
        
        for (int i = 0; i < ticks; i++) {
            BlockPos next = new BlockPos(x + dx, target.posY - 1, z + dz);
            Block block = mc.theWorld.getBlockState(next).getBlock();
            
            if (block == Blocks.air) {
                BlockPos left = new BlockPos(x - dz * 0.5, target.posY - 1, z + dx * 0.5);
                BlockPos right = new BlockPos(x + dz * 0.5, target.posY - 1, z - dx * 0.5);
                
                if (mc.theWorld.getBlockState(left).getBlock() != Blocks.air) {
                    dx = -dz * 0.5;
                    dz = dx * 0.5;
                } else if (mc.theWorld.getBlockState(right).getBlock() != Blocks.air) {
                    dx = dz * 0.5;
                    dz = -dx * 0.5;
                } else {
                    break;
                }
            }
            
            x += dx;
            z += dz;
        }
        
        return new Vec3(x, target.posY, z);
    }
    
    public static Vec3 simulateVector(Entity target, int ticks) {
        return new Vec3(
            target.posX + target.motionX * ticks,
            target.posY + target.motionY * ticks,
            target.posZ + target.motionZ * ticks
        );
    }
    
    public static boolean isBridge(Entity target) {
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos below = new BlockPos(target.posX, target.posY - 1, target.posZ);
        Block block = mc.theWorld.getBlockState(below).getBlock();
        
        if (block == Blocks.air) return false;
        
        int solidCount = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos check = below.add(dx, 0, dz);
                if (mc.theWorld.getBlockState(check).getBlock() != Blocks.air) {
                    solidCount++;
                }
            }
        }
        
        return solidCount <= 3;
    }
}
