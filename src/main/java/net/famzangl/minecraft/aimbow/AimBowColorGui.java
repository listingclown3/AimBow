package net.famzangl.minecraft.aimbow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.File;

public class AimBowColorGui extends GuiScreen {
    private GuiSlider redSlider;
    private GuiSlider greenSlider;
    private GuiSlider blueSlider;
    private GuiSlider alphaSlider;
    private GuiSlider widthSlider;
    private Configuration config;

    public AimBowColorGui() {
        // Load the configuration file
        config = new Configuration(new File("config/AimBowColorGui.cfg"));
        config.load();
    }

    @Override
    public void initGui() {
        // Create sliders with values from the configuration file
        redSlider = new GuiSlider(0, this.width / 2 - 100, this.height / 2 - 50, "Red: ", 0, 255, config.getInt("Red", "Color", 255, 0, 255, "Red color value"), null);
        greenSlider = new GuiSlider(1, this.width / 2 - 100, this.height / 2 - 25, "Green: ", 0, 255, config.getInt("Green", "Color", 255, 0, 255, "Green color value"), null);
        blueSlider = new GuiSlider(2, this.width / 2 - 100, this.height / 2, "Blue: ", 0, 255, config.getInt("Blue", "Color", 255, 0, 255, "Blue color value"), null);
        alphaSlider = new GuiSlider(3, this.width / 2 - 100, this.height / 2 + 25, "Alpha: ", 0, 255, config.getInt("Alpha", "Color", 255, 0, 255, "Alpha value"), null);
        widthSlider = new GuiSlider(4, this.width / 2 - 100, this.height / 2 + 50, "Width: ", 0, 10, config.getInt("Width", "Color", 3, 0, 10, "Width value"), null);

        // Add sliders to the button list so they get drawn and respond to user input
        this.buttonList.add(redSlider);
        this.buttonList.add(greenSlider);
        this.buttonList.add(blueSlider);
        this.buttonList.add(alphaSlider);
        this.buttonList.add(widthSlider);
    }

@Override
public void onGuiClosed() {

    net.famzangl.minecraft.aimbow.AimBowMod.red = config.get("Color", "Red", 255).getInt();
    net.famzangl.minecraft.aimbow.AimBowMod.green = config.get("Color", "Green", 255).getInt();
    net.famzangl.minecraft.aimbow.AimBowMod.blue = config.get("Color", "Blue", 255).getInt();
    net.famzangl.minecraft.aimbow.AimBowMod.alpha = config.get("Color", "Alpha", 255).getInt();
    net.famzangl.minecraft.aimbow.AimBowMod.width = config.get("Color", "Width", 3).getInt();

    config.get("Color", "Red", 255).set(redSlider.getValueInt());
    config.get("Color", "Green", 255).set(blueSlider.getValueInt());
    config.get("Color", "Blue", 255).set(greenSlider.getValueInt());
    config.get("Color", "Alpha", 255).set(alphaSlider.getValueInt());
    config.get("Color", "Width", 3).set(widthSlider.getValueInt());

    config.save();



}
}
