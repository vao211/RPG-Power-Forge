package vao211.rpgpowerforge.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class UpgradePowerOrbItem extends Item {
    private final int orbLevel;

    public UpgradePowerOrbItem(Settings settings, int orbLevel) {
        super(settings);
        this.orbLevel = orbLevel;
    }

    public int getOrbLevel() {
        return this.orbLevel;
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.rpgpowerforge.upgrade_power_orb.tooltip", this.orbLevel)
                .formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.rpgpowerforge.upgrade_power_orb.requirement", this.orbLevel - 1)
                .formatted(Formatting.GRAY));
    }
}