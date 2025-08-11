package net.famzangl.minecraft.aimbow;

import net.famzangl.minecraft.aimbow.aiming.Bow.ReverseBowSolver;
import net.famzangl.minecraft.aimbow.aiming.ColissionData;
import net.famzangl.minecraft.aimbow.aiming.ColissionSolver;
import net.famzangl.minecraft.aimbow.aiming.RayData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.famzangl.minecraft.aimbow.AimBowMod.*;
import static net.famzangl.minecraft.aimbow.aiming.Bow.BowColissionSolver.force;

public class AimbowGui {

    private final FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
    private final IntBuffer viewPort = BufferUtils.createIntBuffer(4);
    private final FloatBuffer win_pos = BufferUtils.createFloatBuffer(3);
    private final Minecraft mc = Minecraft.getMinecraft();
    private float partialTicks;
    public boolean autoAim;

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        partialTicks = event.partialTicks;
        ScaledResolution resolution = new ScaledResolution(mc);

        if (TrajectoryState) {
            if (blockDistanceState) {
                renderDistanceOverlay(resolution);
            }
        }

        renderCustomCrosshair(resolution);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (TrajectoryState) {
            renderTrajectory(event.partialTicks);
        }
        drawCollisionBox(event.partialTicks);
    }

    public static void renderTrajectory(float partialTicks) {
        // Clear trajectory at start of each render cycle
        RayData.trajectory.clear();

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        EntityPlayerSP player = mc.thePlayer;
        ItemStack heldItem = player.getHeldItem();
        ColissionSolver solver = ColissionSolver.forItem(heldItem, mc);

        if (solver == null) return;

        // Compute trajectory for current item
        List<ColissionData> collisions = solver.computeCurrentColissionPoints();

        if (force <= 0.2 || RayData.trajectory.isEmpty()) return;

        GlStateManager.pushMatrix();
        try {
            // Add viewer offset transformation
            GlStateManager.translate(
                    -mc.getRenderManager().viewerPosX,
                    -mc.getRenderManager().viewerPosY,
                    -mc.getRenderManager().viewerPosZ
            );

            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(width);

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();

            worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

            // Draw trajectory points - fixed to prevent extra lines
            for (int i = 0; i < RayData.trajectory.size(); i++) {
                Vec3 point = RayData.trajectory.get(i);

                // Convert color values from 0-255 range to 0.0-1.0 range
                float r = red / 255.0f;
                float g = green / 255.0f;
                float b = blue / 255.0f;
                float a = alpha / 255.0f;

                worldRenderer.pos(point.xCoord, point.yCoord, point.zCoord)
                        .color(r, g, b, a)
                        .endVertex();
            }
            tessellator.draw();
        } finally {
            GL11.glLineWidth(1.0F);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private void renderDistanceOverlay(ScaledResolution resolution) {
        EntityPlayerSP player = mc.thePlayer;
        ItemStack heldItem = player.getHeldItem();
        ColissionSolver solver = ColissionSolver.forItem(heldItem, mc);

        if (solver == null) return;

        List<ColissionData> collisions = solver.computeCurrentColissionPoints();

        if (!collisions.isEmpty()) {
            ColissionData firstHit = collisions.get(0);
            Vec3 playerPos = player.getPositionVector();
            Vec3 hitPos = new Vec3(firstHit.x, firstHit.y, firstHit.z);
            double distance = playerPos.distanceTo(hitPos);

            String text;
            if (firstHit.hitEntity != null) {
                // Hit an entity
                text = String.format("Entity Distance: %.1f", distance);
            } else {
                // Hit a block
                text = String.format("Block Distance: %.1f", distance);
            }

            GlStateManager.pushMatrix();
            mc.fontRendererObj.drawString(
                    text,
                    resolution.getScaledWidth() / 2 + 10,
                    resolution.getScaledHeight() / 2 - 4,
                    0xFFFFFF
            );
            GlStateManager.popMatrix();
        }
    }

    private void renderCustomCrosshair(ScaledResolution resolution) {
        if (!TrajectoryState) return;

        EntityPlayerSP player = mc.thePlayer;
        ItemStack heldItem = player.getHeldItem();
        ColissionSolver solver = ColissionSolver.forItem(heldItem, mc);

        if (solver == null) return;

        List<ColissionData> collisions = solver.computeCurrentColissionPoints();
        boolean drawn = false;

        for (ColissionData p : collisions) {
            Pos2 pos = getScreenPosition(p.x, p.y + player.getEyeHeight(), p.z, resolution);
            boolean hit = p.hitEntity != null;
            drawAimIndicator(pos.x, pos.y, hit);
            if (!drawn && !hit && autoAim && shouldAutoAim(heldItem)) {
                handleAutoAim(pos, resolution, solver);
            }
            drawn = true;
        }

        if (!drawn) {
            drawAimIndicator(resolution.getScaledWidth()/2, resolution.getScaledHeight()/2, false);
        }
    }

    private void drawCollisionBox(float partialTicks) {
        if (RayData.trajectory.isEmpty()) return;

        Vec3 lastPos = RayData.trajectory.get(RayData.trajectory.size() - 1);
        BlockPos endBlock = new BlockPos(lastPos.xCoord, lastPos.yCoord, lastPos.zCoord);
        drawBlockHighlight(endBlock, partialTicks);
    }

    private boolean shouldAutoAim(ItemStack item) {
        return item.getItem() != Items.bow || mc.thePlayer.getItemInUseCount() > 0;
    }

    private void handleAutoAim(Pos2 targetPos, ScaledResolution res, ColissionSolver solver) {
        List<Entity> entities = mc.theWorld.getEntitiesWithinAABB(
                Entity.class,
                mc.thePlayer.getEntityBoundingBox().expand(200, 100, 200)
        );

        ArrayList<CloseEntity> candidates = new ArrayList();
        for (Entity e : entities) {
            if (e.canBeCollidedWith() && e != mc.thePlayer) {
                Pos2 screenPos = getScreenPosition(e.posX, e.posY, e.posZ, res);
                double dist = targetPos.distanceTo(screenPos);
                if (dist < 100) candidates.add(new CloseEntity(e, dist));
            }
        }
        Collections.sort(candidates);

        ReverseBowSolver aimHelper = new ReverseBowSolver(
                solver.getGravity(),
                solver.getVelocity()
        );

        for (CloseEntity candidate : candidates) {
            Vec3 aimVector = aimHelper.getLookForTarget(candidate.entity);
            List<ColissionData> results = solver.computeColissionWithLook(aimVector);
            if (!results.isEmpty() && results.get(0).hitEntity == candidate.entity) {
                adjustPlayerLook(aimVector);
                break;
            }
        }
    }

    private void adjustPlayerLook(Vec3 lookDir) {
        double dx = lookDir.xCoord;
        double dz = lookDir.zCoord;
        double dy = lookDir.yCoord;

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx*dx + dz*dz)));

        float yawDiff = yaw - mc.thePlayer.rotationYaw;
        float pitchDiff = pitch - mc.thePlayer.rotationPitch;

        mc.thePlayer.setAngles(yawDiff/0.15f, -pitchDiff/0.15f);
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

        // Convert color values from 0-255 range to 0.0-1.0 range
        float r = red / 255.0f;
        float g = green / 255.0f;
        float b = blue / 255.0f;
        float a = alpha / 255.0f;

        GL11.glColor4f(r, g, b, a);

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

    private void drawAimIndicator(int x, int y, boolean hit) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        float r = hit ? 1 : 0;
        float g = hit ? 0 : 1;
        drawCustomCrosshair(x - 7, y - 7, r, g, 0);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private Pos2 getScreenPosition(double x, double y, double z, ScaledResolution res) {
        Vec3 eyes = mc.getRenderViewEntity().getPositionEyes(partialTicks);
        viewPort.put(0, 0).put(1, 0).put(2, res.getScaledWidth()).put(3, res.getScaledHeight());

        GLU.gluProject(
                (float)(x - eyes.xCoord),
                (float)(y - eyes.yCoord),
                (float)(z - eyes.zCoord),
                modelBuffer,
                projectionBuffer,
                viewPort,
                win_pos
        );

        return new Pos2(
                (int)win_pos.get(0),
                res.getScaledHeight() - (int)win_pos.get(1)
        );
    }

    private void drawCustomCrosshair(int x, int y, float r, float g, float b) {
        // Bind the Minecraft GUI texture atlas
        mc.getTextureManager().bindTexture(Gui.icons);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        float uScale = 0.00390625f;
        float vScale = 0.00390625f;

        wr.pos(x, y + 16, 0)
                .tex(0 * uScale, 16 * vScale)
                .color(r, g, b, 1f).endVertex();
        wr.pos(x + 16, y + 16, 0)
                .tex(16 * uScale, 16 * vScale)
                .color(r, g, b, 1f).endVertex();
        wr.pos(x + 16, y, 0)
                .tex(16 * uScale, 0 * vScale)
                .color(r, g, b, 1f).endVertex();
        wr.pos(x, y, 0)
                .tex(0 * uScale, 0 * vScale)
                .color(r, g, b, 1f).endVertex();

        Tessellator.getInstance().draw();
    }

    private static class CloseEntity implements Comparable<CloseEntity> {
        final Entity entity;
        final double distance;

        CloseEntity(Entity e, double d) {
            entity = e;
            distance = d;
        }

        @Override
        public int compareTo(CloseEntity o) {
            return Double.compare(distance, o.distance);
        }
    }
}