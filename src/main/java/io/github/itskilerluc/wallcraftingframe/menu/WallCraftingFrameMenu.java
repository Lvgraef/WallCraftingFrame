package io.github.itskilerluc.wallcraftingframe.menu;

import io.github.itskilerluc.wallcraftingframe.blockentity.WallCraftingFrameBlockEntity;
import io.github.itskilerluc.wallcraftingframe.menu.util.slot.PreviewSlot;
import io.github.itskilerluc.wallcraftingframe.menu.util.slot.TemplateSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class WallCraftingFrameMenu extends AbstractContainerMenu {
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final Player player;
    private final ContainerLevelAccess access;
    private final WallCraftingFrameBlockEntity blockEntity;

    public WallCraftingFrameMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf extraData) {
        this(pContainerId, pPlayerInventory, pPlayerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public WallCraftingFrameMenu(int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity) {
        super(MenuType.CRAFTING, pContainerId);
        this.player = pPlayerInventory.player;
        if (blockEntity instanceof WallCraftingFrameBlockEntity) {
            this.blockEntity = (WallCraftingFrameBlockEntity) blockEntity;
        } else {
            throw new IllegalArgumentException("Expected WallCraftingFrameBlockEntity");
        }
        this.access = ContainerLevelAccess.create(pPlayerInventory.player.level(), blockEntity.getBlockPos());
        this.addSlot(new PreviewSlot(pPlayerInventory.player, this.craftSlots, this.resultSlots, 0, 124, 35));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new TemplateSlot(this.craftSlots, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new TemplateSlot(pPlayerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new TemplateSlot(pPlayerInventory, l, 8 + l * 18, 142));
        }

    }

    protected void slotChangedCraftingGrid(Level pLevel, CraftingContainer pContainer) {
        if (!pLevel.isClientSide) {
            Optional<CraftingRecipe> optionalRecipe = pLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, pContainer, pLevel);
            optionalRecipe.ifPresent(recipe -> {
                blockEntity.recipeId = recipe.getId();
                var result = recipe.assemble(pContainer, pLevel.registryAccess());
                resultSlots.setItem(0, result);
                setRemoteSlot(0, result);
                ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, result));
            });
        }
    }

    public void slotsChanged(Container pInventory) {
        this.access.execute((level, pos) -> {
            slotChangedCraftingGrid(level, this.craftSlots);
        });
    }

    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39371_, p_39372_) -> {
            this.clearContainer(pPlayer, this.craftSlots);
        });
    }

    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, Blocks.CRAFTING_TABLE);
    }

    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 0) {
                this.access.execute((p_39378_, p_39379_) -> {
                    itemstack1.getItem().onCraftedBy(itemstack1, p_39378_, pPlayer);
                });
                if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex >= 10 && pIndex < 46) {
                if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
                    if (pIndex < 37) {
                        if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
            if (pIndex == 0) {
                pPlayer.drop(itemstack1, false);
            }
        }

        return itemstack;
    }
}
