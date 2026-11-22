package vao211.rpgpowerforge.util;

import net.dungeon_difficulty.DungeonDifficulty;
import net.dungeon_difficulty.config.Config;
import net.dungeon_difficulty.logic.ItemScaling;
import net.dungeon_difficulty.logic.MathHelper;
import net.dungeon_difficulty.logic.PatternMatching;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpgradeUtil {

    public static boolean isUpgradeableEquipment(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.getItem() instanceof ToolItem
                || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem;
    }

    public static boolean canUpgrade(ItemStack equipment, int orbLevel) {
        if (!isUpgradeableEquipment(equipment)) return false;
        if (orbLevel <= 0) return false;

        int currentLevel = ItemScaling.getScaleFactor(equipment);
        return currentLevel == (orbLevel - 1);
    }

    public static ItemStack performUpgrade(ItemStack equipment, int orbLevel) {
        if (!canUpgrade(equipment, orbLevel)) {
            return ItemStack.EMPTY;
        }

        ItemStack upgradedStack = equipment.copy();
        upgradedStack.setCount(1);

        // Xóa scale cũ bằng hàm chuẩn của DD 3.9.2 để khôi phục đúng attribute gốc
        if (ItemScaling.isScaled(upgradedStack)) {
            ItemScaling.removeScaling(upgradedStack);
        }

        // Áp dụng scale cố định (không random)
        scaleDeterministic(upgradedStack, orbLevel);

        return upgradedStack;
    }

    private static void scaleDeterministic(ItemStack itemStack, int level) {
        if (level <= 0) return;

        var itemEntry = itemStack.getRegistryEntry();
        var rarity = itemStack.getRarity().toString();
        var lootTableId = Identifier.ofVanilla("none");
        var scaling = DungeonDifficulty.config.value.loot_scaling;

        if (itemStack.getItem() instanceof ToolItem || itemStack.getItem() instanceof RangedWeaponItem) {
            var itemData = new PatternMatching.ItemData(PatternMatching.ItemKind.WEAPONS, lootTableId, itemEntry, rarity);
            var result = PatternMatching.getItemScaleResult(itemData, scaling, level);

            boolean hasHandModifiers = false;
            var attributes = getBaseAttributeModifiers(itemStack);
            if (attributes != null) {
                hasHandModifiers = attributes.modifiers().stream()
                        .anyMatch(modifier -> modifier.slot() == AttributeModifierSlot.HAND);
            }

            applyDeterministicModifiers(
                    List.of(hasHandModifiers ? AttributeModifierSlot.HAND : AttributeModifierSlot.MAINHAND),
                    itemStack,
                    result.modifiers(),
                    result.level()
            );
        } else if (itemStack.getItem() instanceof ArmorItem armor) {
            var itemData = new PatternMatching.ItemData(PatternMatching.ItemKind.ARMOR, lootTableId, itemEntry, rarity);
            var result = PatternMatching.getItemScaleResult(itemData, scaling, level);

            applyDeterministicModifiers(
                    List.of(AttributeModifierSlot.forEquipmentSlot(armor.getSlotType())),
                    itemStack,
                    result.modifiers(),
                    result.level()
            );
        } else if (itemStack.getItem() instanceof ShieldItem) {
            var itemData = new PatternMatching.ItemData(PatternMatching.ItemKind.ARMOR, lootTableId, itemEntry, rarity);
            var result = PatternMatching.getItemScaleResult(itemData, scaling, level);

            applyDeterministicModifiers(
                    List.of(AttributeModifierSlot.HAND),
                    itemStack,
                    result.modifiers(),
                    result.level()
            );
        }
    }

    private static AttributeModifiersComponent getBaseAttributeModifiers(ItemStack itemStack) {
        var attributesComponents = itemStack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attributesComponents == null || attributesComponents.modifiers().isEmpty()) {
            attributesComponents = itemStack.getItem().getAttributeModifiers();
            if (attributesComponents == null || attributesComponents.modifiers().isEmpty()) {
                attributesComponents = itemStack.getItem().getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            }
            if (attributesComponents == null) {
                attributesComponents = itemStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            }
        }
        return attributesComponents;
    }

    private record ModifierSummary(float add, float multiplyBase) {
        public ModifierSummary add(float value) {
            return new ModifierSummary(add + value, multiplyBase);
        }

        public ModifierSummary multiplyBase(float value) {
            return new ModifierSummary(add, multiplyBase + value);
        }

        public boolean isEmpty() {
            return add == 0 && multiplyBase == 0;
        }

        public float apply(float value) {
            return (value + add) * (1F + multiplyBase);
        }
    }

    private static double getBaseAttributeValue(AttributeModifiersComponent component, AttributeModifierSlot slot, RegistryEntry<EntityAttribute> givenAttribute) {
        var mutableValue = new MutableDouble(0);
        component.applyModifiers(slot, (attribute, modifier) -> {
            if (attribute.equals(givenAttribute) && modifier.operation() == EntityAttributeModifier.Operation.ADD_VALUE) {
                mutableValue.add(modifier.value());
            }
        });
        return mutableValue.doubleValue();
    }

    private static void applyDeterministicModifiers(
            List<AttributeModifierSlot> slots,
            ItemStack itemStack,
            List<Config.AttributeModifier> modifiers,
            int level
    ) {
        if (modifiers.isEmpty() || level <= 0) {
            return;
        }

        var config = DungeonDifficulty.config.value;
        Double roundingUnit = (config.meta != null) ? config.meta.rounding_unit : null;
        boolean useAdditiveModifiers = (config.meta != null) && !config.meta.merge_item_modifiers;

        var attributesComponents = getBaseAttributeModifiers(itemStack);

        // Tính tổng modifier KHÔNG dùng randomizedValue() mà dùng giá trị cố định: offset + value * level
        var summary = new LinkedHashMap<String, ModifierSummary>();
        for (var modifier : modifiers) {
            var element = summary.getOrDefault(modifier.attribute, new ModifierSummary(0, 0));
            float deterministicValue = modifier.offset + (modifier.value * level);

            switch (modifier.operation) {
                case ADDITION -> element = element.add(deterministicValue);
                case MULTIPLY_BASE -> element = element.multiplyBase(deterministicValue);
            }
            if (!element.isEmpty()) {
                summary.put(modifier.attribute, element);
            }
        }

        Map<AttributeModifierSlot, Map<RegistryEntry<EntityAttribute>, Double>> results = new LinkedHashMap<>();
        for (var slot : slots) {
            results.put(slot, new LinkedHashMap<>());
            for (var attributeBoost : summary.entrySet()) {
                List<RegistryEntry<EntityAttribute>> affectedAttributes;
                var attributePattern = attributeBoost.getKey();
                var exactMatch = Registries.ATTRIBUTE.getEntry(Identifier.of(attributePattern)).orElse(null);

                if (exactMatch != null) {
                    affectedAttributes = List.of(exactMatch);
                } else {
                    var matchedList = new ArrayList<RegistryEntry<EntityAttribute>>();
                    attributesComponents.applyModifiers(slot, (attribute, modifier) -> {
                        if (attribute.getKey().isPresent()
                                && PatternMatching.regexMatches(attribute.getKey().get().getValue().toString(), attributePattern)
                                && !matchedList.contains(attribute)) {
                            matchedList.add(attribute);
                        }
                    });
                    affectedAttributes = matchedList;
                }

                for (var attribute : affectedAttributes) {
                    double baseValue = getBaseAttributeValue(attributesComponents, slot, attribute);

                    if (useAdditiveModifiers) {
                        double boostedValue = attributeBoost.getValue().apply((float) baseValue);
                        double boostAmount = boostedValue - baseValue;
                        if (roundingUnit != null && roundingUnit > 0) {
                            boostAmount = MathHelper.round(boostAmount, roundingUnit);
                        }
                        if (boostAmount != 0) {
                            results.get(slot).put(attribute, boostAmount);
                        }
                    } else {
                        double value = attributeBoost.getValue().apply((float) baseValue);
                        if (roundingUnit != null && roundingUnit > 0) {
                            value = MathHelper.round(value, roundingUnit);
                        }
                        results.get(slot).put(attribute, value);
                    }
                }
            }
        }

        var newAttributeComponent = AttributeModifiersComponent.builder();

        for (var slot : slots) {
            Map<RegistryEntry<EntityAttribute>, Double> slotResults = results.computeIfAbsent(slot, k -> new LinkedHashMap<>());

            attributesComponents.applyModifiers(slot, (attribute, modifier) -> {
                Double resultValue = slotResults.get(attribute);
                boolean replacing = !useAdditiveModifiers
                        && resultValue != null
                        && modifier.operation() == EntityAttributeModifier.Operation.ADD_VALUE;

                if (replacing) {
                    newAttributeComponent.add(
                            attribute,
                            new EntityAttributeModifier(modifier.id(), resultValue, EntityAttributeModifier.Operation.ADD_VALUE),
                            slot
                    );
                    slotResults.remove(attribute);
                } else {
                    newAttributeComponent.add(attribute, modifier, slot);
                }
            });

            for (var entry : slotResults.entrySet()) {
                var id = Identifier.of(DungeonDifficulty.MODID, "power_boost_" + slot.asString());
                newAttributeComponent.add(
                        entry.getKey(),
                        new EntityAttributeModifier(id, entry.getValue(), EntityAttributeModifier.Operation.ADD_VALUE),
                        slot
                );
            }
        }

        itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, newAttributeComponent.build());
        ItemScaling.markAsScaled(itemStack, level);
    }
}