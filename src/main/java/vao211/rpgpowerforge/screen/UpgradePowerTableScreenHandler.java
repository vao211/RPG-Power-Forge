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

        // Slot 0: Trang bị cần ép (Tọa độ chuẩn Anvil)
        this.addSlot(new Slot(this.inputInventory, 0, 27, 47));

        // Slot 1: Ngọc Upgrade Power Orb
        this.addSlot(new Slot(this.inputInventory, 1, 76, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof UpgradePowerOrbItem;
            }
        });

        // Slot 2: Kết quả Output
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

        // Thêm túi đồ của Player (ĐÃ SỬA LỖI THIẾU * 18 Ở ĐÂY)
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
            ItemStack equipment = this.inputInventory.getStack(0);
            ItemStack orb = this.inputInventory.getStack(1);

            if (equipment.isEmpty() || orb.isEmpty() || !(orb.getItem() instanceof UpgradePowerOrbItem orbItem)) {
                this.outputInventory.setStack(0, ItemStack.EMPTY);
                this.sendContentUpdates();
                return;
            }

            if (UpgradeUtil.canUpgrade(equipment, orbItem.getOrbLevel())) {
                ItemStack result = UpgradeUtil.performUpgrade(equipment, orbItem.getOrbLevel());
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
        // 3 hàng túi đồ chính (ĐÃ THÊM * 18)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        // 1 hàng Hotbar bên dưới (ĐÃ THÊM * 18)
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
            } else if (invSlot != 0 && invSlot != 1) {
                if (originalStack.getItem() instanceof UpgradePowerOrbItem) {
                    if (!this.insertItem(originalStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(originalStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 3, 39, false)) {
                return ItemStack.EMPTY;
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
        }
        return newStack;
    }
}