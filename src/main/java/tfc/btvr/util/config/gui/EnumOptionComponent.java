package tfc.btvr.util.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.SliderElement;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.core.lang.I18n;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumOptionComponent<T extends Enum<?>> extends ButtonComponent {
    Consumer<T> setter;
    Supplier<T> current;
    T[] values;
    T def;
    protected final SliderElement button;
    String valueKey;

    public EnumOptionComponent(String valueKey, String translationKey, Consumer<T> setter, Supplier<T> current, T[] values, T def) {
        super(translationKey);
        this.valueKey = valueKey;
        this.setter = setter;
        this.current = current;
        this.values = values;
        this.def = def;
        this.button = new SliderElement(0, 0, 0, 150, 20, valueKey + current.get().toString().toLowerCase(), current.get().ordinal());
        setText(valueKey + def.toString().toLowerCase());
    }

    public void setText(String text) {
        this.button.displayString = I18n.getInstance().translateKey(text);
    }

    public void setState(int state) {
        this.button.sliderValue = state;
    }


    @Override
    public void resetValue() {
        setter.accept(def);
        setState(current.get().ordinal());
        setText(valueKey + def.toString().toLowerCase());
    }

    @Override
    public boolean isDefault() {
        return current.get() == def;
    }

    protected void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
        setter.accept(values[(int) ((relativeMouseX / (double) button.width) * values.length)]);
        setState(current.get().ordinal());
        setText(valueKey + current.get().toString().toLowerCase());
    }

    protected void renderButton(
            int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY
    ) {
        super.renderButton(x, y, relativeButtonX, relativeButtonY, buttonWidth, buttonHeight, relativeMouseX, relativeMouseY);
        this.button.xPosition = x + relativeButtonX;
        this.button.yPosition = y + relativeButtonY;
        this.button.width = buttonWidth;
        this.button.height = buttonHeight;
        this.button.drawButton(mc, x + relativeMouseX, y + relativeMouseY);
    }

    @Override
    public void init(Minecraft mc) {
        setState(current.get().ordinal());
        setText(valueKey + current.get().toString().toLowerCase());
    }
}
