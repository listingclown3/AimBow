package net.famzangl.minecraft.aimbow.aiming;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import static net.famzangl.minecraft.aimbow.SmartAimConfig.*;

public class SmartTargeting {
    
    public static class PredictionResult {
        public Vec3 position;
        public PredictionMode mode;
        public float confidence;
        
        public PredictionResult(Vec3 pos, PredictionMode mode, float confidence) {
            this.position = pos;
            this.mode = mode;
            this.confidence = confidence;
        }
    }
    
    public static PredictionResult[] predictPositions(Entity target, double arrowSpeed) {
        Vec3 playerToTarget = new Vec3(target.posX, target.posY, target.posZ);
        double distance = Math.sqrt(
            target.posX * target.posX + 
            target.posY * target.posY + 
            target.posZ * target.posZ
        );
        
        int ticks = (int)(distance / arrowSpeed);
        ticks = Math.max(1, Math.min(ticks, 60));
        
        PredictionResult[] results = new PredictionResult[3];
        
        // Physics prediction
        Vec3 physicsPos = TargetSimulator.simulateJump(target, ticks);
        results[0] = new PredictionResult(physicsPos, PredictionMode.PHYSICS, 
            !target.onGround ? 0.9f : 0.3f);
        
        // Path prediction
        Vec3 pathPos = TargetSimulator.simulatePath(target, ticks);
        results[1] = new PredictionResult(pathPos, PredictionMode.PATH,
            TargetSimulator.isBridge(target) ? 0.85f : 0.2f);
        
        // Vector prediction
        Vec3 vectorPos = TargetSimulator.simulateVector(target, ticks);
        results[2] = new PredictionResult(vectorPos, PredictionMode.VECTOR,
            target.onGround && !TargetSimulator.isBridge(target) ? 0.7f : 0.4f);
        
        return results;
    }
    
    public static PredictionResult selectBestPrediction(PredictionResult[] predictions, Vec3 playerGaze) {
        PredictionResult best = predictions[0];
        double bestScore = -1;
        
        for (PredictionResult pred : predictions) {
            Vec3 toTarget = pred.position.subtract(playerGaze).normalize();
            double angle = Math.acos(toTarget.dotProduct(playerGaze.normalize()));
            double score = pred.confidence / (1 + angle);
            
            if (score > bestScore) {
                bestScore = score;
                best = pred;
            }
        }
        
        return best;
    }
    
    public static Vec3 applyHitboxBias(Vec3 predicted, Entity target) {
        double mx = target.motionX;
        double mz = target.motionZ;
        double speed = Math.sqrt(mx * mx + mz * mz);
        
        if (speed > 0.01) {
            return predicted.addVector(
                (mx / speed) * HITBOX_BIAS,
                0,
                (mz / speed) * HITBOX_BIAS
            );
        }
        
        return predicted;
    }
}
