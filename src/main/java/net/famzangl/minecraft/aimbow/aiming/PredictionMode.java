package net.famzangl.minecraft.aimbow.aiming;

public enum PredictionMode {
    PHYSICS,    // Airborne - gravity simulation
    PATH,       // Bridge - constrained movement
    VECTOR,     // Open field - linear extrapolation
    STATIONARY  // Fallback - current position
}
