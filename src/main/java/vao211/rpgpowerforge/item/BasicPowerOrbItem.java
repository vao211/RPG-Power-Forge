package vao211.rpgpowerforge.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BasicPowerOrbItem extends Item {
    public BasicPowerOrbItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.rpgpowerforge.basic_power_orb.tooltip").formatted(Formatting.DARK_PURPLE));
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("item.rpgpowerforge.basic_power_orb.drop_title").formatted(Formatting.GOLD, Formatting.BOLD));
        tooltip.add(Text.translatable("item.rpgpowerforge.basic_power_orb.drop_common").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.rpgpowerforge.basic_power_orb.drop_boss").formatted(Formatting.RED));
    }
}