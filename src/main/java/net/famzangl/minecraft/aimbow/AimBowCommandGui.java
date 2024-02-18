package net.famzangl.minecraft.aimbow;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.File;

public class AimBowCommandGui extends GuiScreen {
    private GuiSlider redSlider;
    private GuiSlider greenSlider;
    private GuiSlider blueSlider;
    private GuiSlider alphaSlider;
    private GuiSlider widthSlider;
    private GuiButton crossHairButton;
    private GuiButton blockDistanceButton;
    private GuiButton TrajectoryButton;
    private boolean buttonState;
    private boolean blockDistanceState;
    private boolean TrajectoryState;
    private Configuration config;

    public AimBowCommandGui() {
        // Load the configuration file
        config = new Configuration(new File("config/AimBowColorGui.cfg"));
        config.load();

        // Fixes loading issues with each initialization of the button state
        buttonState = config.get("General", "CrossHairState", false).getBoolean();
        blockDistanceState = config.get("General", "BlockDistance", false).getBoolean();
        TrajectoryState = config.get("General", "Trajectory", true).getBoolean();
    }

    @Override
    public void initGui() {
        // Create sliders with values from the configuration file
        TrajectoryButton = new GuiButton(7, this.width / 2 - 125, this.height / 2 - 100, "Trajectory: " + (TrajectoryState ? "On" : "Off"));
        redSlider = new GuiSlider(0, this.width / 2 - 100, this.height / 2 - 75, "Red: ", 0, 255, config.getInt("Red", "Color", 255, 0, 255, "Red color value"), null);
        greenSlider = new GuiSlider(1, this.width / 2 - 100, this.height / 2 - 50, "Green: ", 0, 255, config.getInt("Green", "Color", 255, 0, 255, "Green color value"), null);
        blueSlider = new GuiSlider(2, this.width / 2 - 100, this.height / 2 - 25, "Blue: ", 0, 255, config.getInt("Blue", "Color", 255, 0, 255, "Blue color value"), null);
        alphaSlider = new GuiSlider(3, this.width / 2 - 100, this.height / 2, "Alpha: ", 0, 255, config.getInt("Alpha", "Color", 255, 0, 255, "Alpha value"), null);
        widthSlider = new GuiSlider(4, this.width / 2 - 100, this.height / 2 + 25, "Width: ", 0, 10, config.getInt("Width", "Color", 3, 0, 10, "Width value"), null);
        crossHairButton = new GuiButton(5, this.width / 2 - 125, this.height / 2 + 50, "Crosshair changing: " + (buttonState ? "On" : "Off"));
        blockDistanceButton = new GuiButton(6, this.width / 2 - 125, this.height / 2 + 75, "Block Distance: " + (blockDistanceState ? "On" : "Off"));

        // Add sliders to the button list so they get drawn and respond to user input
        this.buttonList.add(TrajectoryButton);
        this.buttonList.add(redSlider);
        this.buttonList.add(greenSlider);
        this.buttonList.add(blueSlider);
        this.buttonList.add(alphaSlider);
        this.buttonList.add(widthSlider);
        this.buttonList.add(crossHairButton);
        this.buttonList.add(blockDistanceButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {

        AimBowMod.red = redSlider.getValueInt();
        AimBowMod.green = greenSlider.getValueInt();
        AimBowMod.blue = blueSlider.getValueInt();
        AimBowMod.alpha = alphaSlider.getValueInt();
        AimBowMod.width = widthSlider.getValueInt();

        if (button == TrajectoryButton) {
            // Toggle the crosshair boolean and update the button text when the highLightLandingBlock button is clicked
            TrajectoryState = !TrajectoryState;
            TrajectoryButton.displayString = "Trajectory: " + (TrajectoryState ? "On" : "Off");
        }

        if (button == crossHairButton) {
            // Toggle the crosshair boolean and update the button text when the Crosshair button is clicked
            buttonState = !buttonState;
            crossHairButton.displayString = "Crosshair changing: " + (buttonState ? "On" : "Off");
        }

        if (button == blockDistanceButton) {
            // Toggle the crosshair boolean and update the button text when the highLightLandingBlock button is clicked
            blockDistanceState = !blockDistanceState;
            blockDistanceButton.displayString = "Block Distance: " + (blockDistanceState ? "On" : "Off");
        }
    }

@Override
public void onGuiClosed() {

    AimBowMod.red = redSlider.getValueInt();
    AimBowMod.green = greenSlider.getValueInt();
    AimBowMod.blue = blueSlider.getValueInt();
    AimBowMod.alpha = alphaSlider.getValueInt();
    AimBowMod.width = widthSlider.getValueInt();
    AimBowMod.crossHairState = buttonState;
    AimBowMod.blockDistanceState = blockDistanceState;
    AimBowMod.TrajectoryState = TrajectoryState;

    config.get("Color", "Red", 255).set(redSlider.getValueInt());
    config.get("Color", "Green", 255).set(blueSlider.getValueInt());
    config.get("Color", "Blue", 255).set(greenSlider.getValueInt());
    config.get("Color", "Alpha", 255).set(alphaSlider.getValueInt());
    config.get("Color", "Width", 3).set(widthSlider.getValueInt());
    config.get("General", "CrossHairState", false).set(buttonState);
    config.get("General", "BlockDistance", false).set(blockDistanceState);
    config.get("General", "Trajectory", true).set(TrajectoryState);

    config.save();

}
}
