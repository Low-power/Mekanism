package mekanism.client.gui.element.gauge;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import mekanism.api.chemical.gas.IMekanismGasHandler;
import mekanism.api.chemical.infuse.IMekanismInfusionHandler;
import mekanism.api.chemical.pigment.IMekanismPigmentHandler;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.transmitters.TransmissionType;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.jei.IJEIIngredientHelper;
import mekanism.common.capabilities.MergedTank;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.ITextComponent;

public class GuiMergedTankGauge<HANDLER extends IMekanismFluidHandler & IMekanismGasHandler & IMekanismInfusionHandler & IMekanismPigmentHandler> extends GuiGauge<Void>
      implements IJEIIngredientHelper {

    private final Supplier<MergedTank> mergedTankSupplier;
    private final Supplier<HANDLER> handlerSupplier;

    private final GuiFluidGauge fluidGauge;
    private final GuiGasGauge gasGauge;
    private final GuiInfusionGauge infusionGauge;
    private final GuiPigmentGauge pigmentGauge;

    private ITextComponent label;

    public GuiMergedTankGauge(Supplier<MergedTank> mergedTankSupplier, Supplier<HANDLER> handlerSupplier, GaugeType type, IGuiWrapper gui, int x, int y, int width,
          int height) {
        super(type, gui, x, y, width, height);
        this.mergedTankSupplier = mergedTankSupplier;
        this.handlerSupplier = handlerSupplier;
        fluidGauge = new GuiFluidGauge(() -> this.mergedTankSupplier.get().getFluidTank(), () -> this.handlerSupplier.get().getFluidTanks(null), type, gui, x, y, width, height);
        gasGauge = new GuiGasGauge(() -> this.mergedTankSupplier.get().getGasTank(), () -> this.handlerSupplier.get().getGasTanks(null), type, gui, x, y, width, height);
        infusionGauge = new GuiInfusionGauge(() -> this.mergedTankSupplier.get().getInfusionTank(), () -> this.handlerSupplier.get().getInfusionTanks(null), type, gui, x, y, width, height);
        pigmentGauge = new GuiPigmentGauge(() -> this.mergedTankSupplier.get().getPigmentTank(), () -> this.handlerSupplier.get().getPigmentTanks(null), type, gui, x, y, width, height);
    }

    public GuiMergedTankGauge<HANDLER> setLabel(ITextComponent label) {
        this.label = label;
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        GuiTankGauge<?, ?> currentGaugeNoFallback = getCurrentGaugeNoFallback();
        if (currentGaugeNoFallback == null) {
            //If all the tanks are currently empty, pass the click event to all of them;
            // if multiple types are somehow stored in the dropper, insertion checks should prevent them from being inserted at the same time
            fluidGauge.mouseClicked(mouseX, mouseY, button);
            gasGauge.mouseClicked(mouseX, mouseY, button);
            infusionGauge.mouseClicked(mouseX, mouseY, button);
            pigmentGauge.mouseClicked(mouseX, mouseY, button);
        } else {
            //Otherwise just send the click event to the corresponding gauge
            currentGaugeNoFallback.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void applyRenderColor() {
        GuiTankGauge<?, ?> currentGaugeNoFallback = getCurrentGaugeNoFallback();
        if (currentGaugeNoFallback != null) {
            currentGaugeNoFallback.applyRenderColor();
        }
    }

    @Nullable
    @Override
    public Object getIngredient() {
        return getCurrentGauge().getIngredient();
    }

    @Override
    public int getScaledLevel() {
        return getCurrentGauge().getScaledLevel();
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return getCurrentGauge().getIcon();
    }

    @Override
    public List<ITextComponent> getTooltipText() {
        return getCurrentGauge().getTooltipText();
    }

    @Override
    public ITextComponent getLabel() {
        return label;
    }

    @Override
    public TransmissionType getTransmission() {
        return getCurrentGauge().getTransmission();
    }

    private GuiTankGauge<?, ?> getCurrentGauge() {
        GuiTankGauge<?, ?> currentGauge = getCurrentGaugeNoFallback();
        //Fallback to the fluid gauge
        return currentGauge == null ? fluidGauge : currentGauge;
    }

    @Nullable
    private GuiTankGauge<?, ?> getCurrentGaugeNoFallback() {
        MergedTank mergedTank = mergedTankSupplier.get();
        switch (mergedTank.getCurrentType()) {
            case FLUID:
                return fluidGauge;
            case GAS:
                return gasGauge;
            case INFUSION:
                return infusionGauge;
            case PIGMENT:
                return pigmentGauge;
        }
        return null;
    }
}