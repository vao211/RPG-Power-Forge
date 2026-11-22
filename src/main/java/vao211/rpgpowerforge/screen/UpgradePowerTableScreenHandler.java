package vao211.rpgpowerforge.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import vao211.rpgpowerforge.item.UpgradePowerOrbItem;
import vao211.rpgpowerforge.registry.ModRegistry;
import vao211.rpgpowerforge.util.UpgradeUtil;

public class UpgradePowerTableScreenHandler extends ScreenHandler {
    private final Inventory inputInventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            onContentChanged(this);
        }
    };
    private final Inventory outputInventory = new SimpleInventory(1);
    private final ScreenHandlerContext context;

    public UpgradePowerTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public UpgradePowerTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModRegistry.UPGRADE_POWER_TABLE_SCREEN_HANDLER, syncId);
        this.context = context;

        this.addSlot(new Slot(this.inputInventory, 0, 27, 47));

        this.addSlot(new Slot(this.inputInventory, 1, 76, 47) {
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof UpgradePowerOrbItem
                        || UpgradeUtil.isUpgradeableEquipment(stack);
            }
        });

        this.addSlot(new Slot(this.outputInventory, 0, 134, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                inputInventory.removeStack(0, 1);
                inputInventory.removeStack(1, 1);
                super.onTakeItem(player, stack);
            }
        });

        addPlayerInventory(playerInventory);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.inputInventory) {
            updateResult();
        }
    }

    private void updateResult() {
        this.context.run((world, pos) -> {
            ItemStack first = this.inputInventory.getStack(0);
            ItemStack second = this.inputInventory.getStack(1);

            if (first.isEmpty() || second.isEmpty()) {
                this.outputInventory.setStack(0, ItemStack.EMPTY);
                this.sendContentUpdates();
                return;
            }

            if (second.getItem() instanceof UpgradePowerOrbItem orbItem
                    && UpgradeUtil.canUpgrade(first, orbItem.getOrbLevel())) {
                ItemStack result = UpgradeUtil.performUpgrade(first, orbItem.getOrbLevel());
                this.outputInventory.setStack(0, result);
            }
            else if (UpgradeUtil.canMergeUpgrade(first, second)) {
                ItemStack result = UpgradeUtil.performMergeUpgrade(first, second);
                this.outputInventory.setStack(0, result);
            } else {
                this.outputInventory.setStack(0, ItemStack.EMPTY);
            }

            this.sendContentUpdates();
        });
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inputInventory));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModRegistry.UPGRADE_POWER_TABLE_BLOCK);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot == 2) {
                if (!this.insertItem(originalStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(originalStack, newStack);
            }
            else if (invSlot == 0 || invSlot == 1) {
                if (!this.insertItem(originalStack, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (originalStack.getItem() instanceof UpgradePowerOrbItem) {
                    if (!this.insertItem(originalStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (UpgradeUtil.isUpgradeableEquipment(originalStack)) {
                    if (!this.insertItem(originalStack, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (originalStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, originalStack);

            if (invSlot == 0 || invSlot == 1) {
                this.onContentChanged(this.inputInventory);
            }
        }

        return newStack;
    }
}