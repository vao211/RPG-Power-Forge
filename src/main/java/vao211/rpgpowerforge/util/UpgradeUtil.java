package vao211.rpgpowerforge.util;

import net.dungeon_difficulty.logic.ItemScaling;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import vao211.rpgpowerforge.item.AttributeOrbItem;
import vao211.rpgpowerforge.registry.ModRegistry;

import java.util.ArrayList;
import java.util.List;

public class UpgradeUtil {
    public static final String APPLIED_ORBS_KEY = "rpgpowerforge.orbs";

    public static boolean isUpgradeableEquipment(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof ToolItem
                || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem;
    }

    public static boolean canUpgrade(ItemStack equipment, int orbLevel) {
        if (!isUpgradeableEquipment(equipment)) return false;

        int currentLevel = ItemScaling.getScaleFactor(equipment);
        return currentLevel == (orbLevel - 1);
    }

    public static ItemStack performUpgrade(ItemStack equipment, int orbLevel) {
        if (!canUpgrade(equipment, orbLevel)) {
            return ItemStack.EMPTY;
        }

        ItemStack upgradedStack = equipment.copy();
        upgradedStack.setCount(1);

        //API Dungeon Difficulty để xóa scale cũ và áp dụng stat mới
        ItemScaling.rescale(upgradedStack, orbLevel);

        return upgradedStack;
    }

    // --- SOCKETING ---
    public static boolean canApplyAttributeOrb(ItemStack equipment) {
        if (!isUpgradeableEquipment(equipment)) return false;
        NbtCompound nbt = equipment.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.contains(APPLIED_ORBS_KEY)) {
            NbtList list = nbt.getList(APPLIED_ORBS_KEY, NbtElement.STRING_TYPE);
            return list.size() < 2; // Tối đa 2 viên
        }
        return true;
    }

    public static ItemStack applyAttributeOrb(ItemStack equipment, AttributeOrbItem orb) {
        ItemStack result = equipment.copy();
        result.setCount(1);

        NbtComponent customData = result.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = customData.copyNbt();
        NbtList list = nbt.contains(APPLIED_ORBS_KEY) ? nbt.getList(APPLIED_ORBS_KEY, NbtElement.STRING_TYPE) : new NbtList();
        list.add(NbtString.of(Registries.ITEM.getId(orb).toString()));
        nbt.put(APPLIED_ORBS_KEY, list);
        result.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        //Ép chỉ số
        AttributeModifiersComponent modifiers = result.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        var attrEntry = Registries.ATTRIBUTE.getEntry(orb.getAttributeId()).orElse(null);
        if (attrEntry != null) {
            AttributeModifierSlot slot = (result.getItem() instanceof ArmorItem armor)
                    ? AttributeModifierSlot.forEquipmentSlot(armor.getSlotType())
                    : AttributeModifierSlot.MAINHAND;

            Identifier modId = Identifier.of(ModRegistry.MOD_ID, "orb_boost_" + System.nanoTime());
            modifiers = modifiers.with(attrEntry, new EntityAttributeModifier(modId, orb.getValue(), orb.getOperation()), slot);
            result.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, modifiers);
        }
        return result;
    }

    // ---CLEARING ---
    public static boolean hasOrbsToClear(ItemStack equipment) {
        NbtCompound nbt = equipment.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        return nbt.contains(APPLIED_ORBS_KEY) && !nbt.getList(APPLIED_ORBS_KEY, NbtElement.STRING_TYPE).isEmpty();
    }

    public static List<String> getAppliedOrbIds(ItemStack equipment) {
        List<String> orbIds = new ArrayList<>();
        NbtCompound nbt = equipment.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.contains(APPLIED_ORBS_KEY)) {
            NbtList list = nbt.getList(APPLIED_ORBS_KEY, NbtElement.STRING_TYPE);
            for (int i = 0; i < list.size(); i++) {
                orbIds.add(list.getString(i));
            }
        }
        return orbIds;
    }

    public static ItemStack clearOrbs(ItemStack equipment) {
        ItemStack result = equipment.copy();

        NbtComponent customData = result.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = customData.copyNbt();
        nbt.remove(APPLIED_ORBS_KEY);
        result.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        //Lọc bỏ các Modifier do ngọc tạo ra (để giữ nguyên modifier của Dungeon Difficulty)
        AttributeModifiersComponent modifiers = result.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        for (var entry : modifiers.modifiers()) {
            var id = entry.modifier().id();
            //Nếu không phải là modifier của ngọc, thì giữ lại
            if (!(id.getNamespace().equals(ModRegistry.MOD_ID) && id.getPath().startsWith("orb_boost_"))) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
        }
        result.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        return result;
    }

}