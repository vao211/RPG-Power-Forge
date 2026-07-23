package vao211.rpgpowerforge.util;

import net.dungeon_difficulty.logic.ItemScaling;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;

public class UpgradeUtil {

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
}