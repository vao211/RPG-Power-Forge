package vao211.rpgpowerforge.item;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BasicAttributeOrbItem extends Item {
    public BasicAttributeOrbItem (Settings settings){
        super(settings);
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.rpgpowerforge.basic_attribute_orb.tooltip").formatted(Formatting.DARK_PURPLE));
    }
}
