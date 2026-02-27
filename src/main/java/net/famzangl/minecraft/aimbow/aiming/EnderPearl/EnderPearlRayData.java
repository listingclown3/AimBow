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
package net.famzangl.minecraft.aimbow.aiming.EnderPearl;

import net.famzangl.minecraft.aimbow.aiming.RayData;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * 
 * Ender Pearls have fixed initial velocity (1.5F) affected by gravity (0.03F)
 * and drag (0.99F)
 *
 */
public class EnderPearlRayData extends RayData {

	private static final boolean USE_RANDOM = false;
	private static final float ENDER_PEARL_VELOCITY = 1.5F;
	private static final float ENDER_PEARL_GRAVITY = 0.03F;

	@Override
	public void moveTick() {
		super.moveTick();
		trajectory.add(new Vec3(this.posX, this.posY, this.posZ));
	}

	@Override
	protected float getGravity() {
		return ENDER_PEARL_GRAVITY;
	}

	@Override
	public void shoot() {
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        float f = 0.4F;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionY = (double)(-MathHelper.sin((this.rotationPitch + this.getInaccuracy()) / 180.0F * (float)Math.PI) * f);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.getVelocity(), USE_RANDOM ? 1.0F : 0);
	}

	private double getVelocity() {
		return ENDER_PEARL_VELOCITY;
	}

	private float getInaccuracy() {
		return 0;
	}

}
