package vao211.rpgpowerforge.item;

import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class AttributeOrbItem extends Item {
    private final Identifier attributeId;
    private final double value;
    private final Operation operation;

    public AttributeOrbItem(Settings settings, Identifier attributeId, double value, Operation operation){
        super(settings);
        this.attributeId = attributeId;
        this.value = value;
        this.operation = operation;
    }

    public Identifier getAttributeId(){return attributeId;}
    public double getValue() { return value; }
    public Operation getOperation() { return operation; }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.rpgpowerforge.attribute_orb.tooltip").formatted(Formatting.AQUA));
    }
}
