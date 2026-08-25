package vao211.rpgpowerforge.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import vao211.rpgpowerforge.RpgPowerForge;
import vao211.rpgpowerforge.block.UpgradePowerTableBlock;
import vao211.rpgpowerforge.item.*;
import vao211.rpgpowerforge.screen.UpgradePowerTableScreenHandler;

public class ModRegistry {
    public static final String MOD_ID = RpgPowerForge.MOD_ID;

    public static final Item BASIC_POWER_ORB = registerItem("basic_power_orb", new BasicPowerOrbItem(new Item.Settings()));

    public static final Item UPGRADE_POWER_ORB_1 = registerItem("upgrade_power_orb_1", new UpgradePowerOrbItem(new Item.Settings(), 1));
    public static final Item UPGRADE_POWER_ORB_2 = registerItem("upgrade_power_orb_2", new UpgradePowerOrbItem(new Item.Settings(), 2));
    public static final Item UPGRADE_POWER_ORB_3 = registerItem("upgrade_power_orb_3", new UpgradePowerOrbItem(new Item.Settings(), 3));
    public static final Item UPGRADE_POWER_ORB_4 = registerItem("upgrade_power_orb_4", new UpgradePowerOrbItem(new Item.Settings(), 4));
    public static final Item UPGRADE_POWER_ORB_5 = registerItem("upgrade_power_orb_5", new UpgradePowerOrbItem(new Item.Settings(), 5));
    public static final Item BASIC_ATTRIBUTE_ORB = registerItem("basic_attribute_orb", new BasicAttributeOrbItem(new Item.Settings()));
    public static final Item CLEAR_ORB = registerItem("clear_orb", new ClearOrbItem(new Item.Settings()));
    public static final Item CRIT_ORB = registerItem("critical_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("critical_strike", "chance"),
            0.1,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
    ));
    public static final Item CRIT_POWER_ORB = registerItem("critical_power_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("critical_strike", "damage"),
            0.1,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
    ));
    public static final Item FLAME_ORB =registerItem("flame_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "fire"),
            3,
            EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item ARCANE_ORB = registerItem("arcane_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "arcane"),
            3,
            EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item FROST_ORB = registerItem("frost_orb", new AttributeOrbItem(
       new Item.Settings(),
       Identifier.of("spell_power", "frost"),
            3,
        EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item HEAL_ORB = registerItem("heal_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "healing"),
            3,
            EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item LIGHTNING_ORB = registerItem("lightning_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "lightning"),
            3,
            EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item SOUL_ORB = registerItem("soul_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "soul"),
            3,
            EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item AQUA_ORB = registerItem("aqua_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "water"),
            3,
            EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item AIR_ORB = registerItem("air_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "air"),
            3,
            EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item EARTH_ORB = registerItem("earth_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "earth"),
            3,
            EntityAttributeModifier.Operation.ADD_VALUE
    ));
    public static final Item NATURE_ORB = registerItem("nature_orb", new AttributeOrbItem(
            new Item.Settings(),
            Identifier.of("spell_power", "nature"),
            3.0, EntityAttributeModifier.Operation.ADD_VALUE
    ));

    public static final Block UPGRADE_POWER_TABLE_BLOCK = registerBlock("upgrade_power_table", new UpgradePowerTableBlock(AbstractBlock.Settings.copy(Blocks.SMITHING_TABLE)));    public static final ScreenHandlerType<UpgradePowerTableScreenHandler> UPGRADE_POWER_TABLE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(MOD_ID, "upgrade_power_table"),
                    new ScreenHandlerType<>(UpgradePowerTableScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, name), item);
    }

    private static Block registerBlock(String name, Block block) {
        registerItem(name, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, name), block);
    }

    public static void registerAll() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(BASIC_POWER_ORB);
            entries.add(UPGRADE_POWER_ORB_1);
            entries.add(UPGRADE_POWER_ORB_2);
            entries.add(UPGRADE_POWER_ORB_3);
            entries.add(UPGRADE_POWER_ORB_4);
            entries.add(UPGRADE_POWER_ORB_5);
            entries.add(BASIC_ATTRIBUTE_ORB);
            entries.add(CLEAR_ORB);
            entries.add(CRIT_ORB);
            entries.add(CRIT_POWER_ORB);
            entries.add(FLAME_ORB);
            entries.add(ARCANE_ORB);
            entries.add(FROST_ORB);
            entries.add(HEAL_ORB);
            entries.add(LIGHTNING_ORB);
            entries.add(SOUL_ORB);
            entries.add(AQUA_ORB);

            entries.add(AIR_ORB);
            entries.add(EARTH_ORB);
            entries.add(NATURE_ORB);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(UPGRADE_POWER_TABLE_BLOCK);
        });
    }
}