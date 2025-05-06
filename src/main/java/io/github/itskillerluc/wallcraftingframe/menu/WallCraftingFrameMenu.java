package io.github.itskillerluc.wallcraftingframe.menu;

import io.github.itskillerluc.wallcraftingframe.blockentity.WallCraftingFrameBlockEntity;
import io.github.itskillerluc.wallcraftingframe.init.BlockRegistry;
import io.github.itskillerluc.wallcraftingframe.menu.util.slot.PreviewSlot;
import io.github.itskillerluc.wallcraftingframe.menu.util.slot.TemplateSlot;
import io.github.itskillerluc.wallcraftingframe.mixin.SlotMixin;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
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
                this.addSlot(new Slot(pPlayerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(pPlayerInventory, l, 8 + l * 18, 142));
        }

    }

    @Override
    public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        try {
            this.doClick(pSlotId, pButton, pClickType, pPlayer);
        } catch (Exception exception) {
            CrashReport crashreport = CrashReport.forThrowable(exception, "Container click");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Click info");
            crashreportcategory.setDetail("Menu Type", () -> {
                return this.menuType != null ? BuiltInRegistries.MENU.getKey(this.menuType).toString() : "<no type>";
            });
            crashreportcategory.setDetail("Menu Class", () -> {
                return this.getClass().getCanonicalName();
            });
            crashreportcategory.setDetail("Slot Count", this.slots.size());
            crashreportcategory.setDetail("Slot", pSlotId);
            crashreportcategory.setDetail("Button", pButton);
            crashreportcategory.setDetail("Type", pClickType);
            throw new ReportedException(crashreport);
        }
    }


    private void doClick(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        Inventory inventory = pPlayer.getInventory();
        if (pClickType == ClickType.QUICK_CRAFT) {
            int i = this.quickcraftStatus;
            this.quickcraftStatus = getQuickcraftHeader(pButton);
            if ((i != 1 || this.quickcraftStatus != 2) && i != this.quickcraftStatus) {
                this.resetQuickCraft();
            } else if (this.getCarried().isEmpty()) {
                this.resetQuickCraft();
            } else if (this.quickcraftStatus == 0) {
                this.quickcraftType = getQuickcraftType(pButton);
                if (isValidQuickcraftType(this.quickcraftType, pPlayer)) {
                    this.quickcraftStatus = 1;
                    this.quickcraftSlots.clear();
                } else {
                    this.resetQuickCraft();
                }
            } else if (this.quickcraftStatus == 1) {
                Slot slot = this.slots.get(pSlotId);
                ItemStack itemstack = this.getCarried();
                if (canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack) && (this.quickcraftType == 2 || itemstack.getCount() > this.quickcraftSlots.size()) && this.canDragTo(slot)) {
                    this.quickcraftSlots.add(slot);
                }
            } else if (this.quickcraftStatus == 2) {
                if (!this.quickcraftSlots.isEmpty()) {
                    if (this.quickcraftSlots.size() == 1) {
                        int i1 = (this.quickcraftSlots.iterator().next()).index;
                        this.resetQuickCraft();
                        this.doClick(i1, this.quickcraftType, ClickType.PICKUP, pPlayer);
                        return;
                    }

                    ItemStack itemstack2 = this.getCarried().copy();
                    if (itemstack2.isEmpty()) {
                        this.resetQuickCraft();
                        return;
                    }

                    int k1 = this.getCarried().getCount();

                    var actualQuickCraftSlots = new ArrayList<>(quickcraftSlots);

                    for(var slot : this.quickcraftSlots) {
                        if (slot instanceof TemplateSlot) {
                            slot.setByPlayer(itemstack2.copyWithCount(1));
                            actualQuickCraftSlots.remove(slot);
                        }
                    }

                    for(Slot slot1 : actualQuickCraftSlots) {
                        ItemStack itemstack1 = this.getCarried();
                        if (slot1 != null && canItemQuickReplace(slot1, itemstack1, true) && slot1.mayPlace(itemstack1) && (this.quickcraftType == 2 || itemstack1.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(slot1)) {
                            int j = slot1.hasItem() ? slot1.getItem().getCount() : 0;
                            int k = Math.min(itemstack2.getMaxStackSize(), slot1.getMaxStackSize(itemstack2));
                            int l = Math.min(getQuickCraftPlaceCount(this.quickcraftSlots, this.quickcraftType, itemstack2) + j, k);
                            k1 -= l - j;
                            slot1.setByPlayer(itemstack2.copyWithCount(l));
                        }
                    }

                    itemstack2.setCount(k1);
                    this.setCarried(itemstack2);
                }

                this.resetQuickCraft();
            } else {
                this.resetQuickCraft();
            }
        } else if (this.quickcraftStatus != 0) {
            this.resetQuickCraft();
        } else if ((pClickType == ClickType.PICKUP || pClickType == ClickType.QUICK_MOVE) && (pButton == 0 || pButton == 1)) {
            ClickAction clickaction = pButton == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
            if (pSlotId == -999) {
                if (!this.getCarried().isEmpty()) {
                    if (clickaction == ClickAction.PRIMARY) {
                        pPlayer.drop(this.getCarried(), true);
                        this.setCarried(ItemStack.EMPTY);
                    } else {
                        pPlayer.drop(this.getCarried().split(1), true);
                    }
                }
            } else if (pClickType == ClickType.QUICK_MOVE) {
                if (pSlotId < 0) {
                    return;
                }

                Slot slot6 = this.slots.get(pSlotId);
                if (!slot6.mayPickup(pPlayer)) {
                    return;
                }

                for(ItemStack itemstack8 = this.quickMoveStack(pPlayer, pSlotId); !itemstack8.isEmpty() && ItemStack.isSameItem(slot6.getItem(), itemstack8); itemstack8 = this.quickMoveStack(pPlayer, pSlotId)) {
                }
            } else {
                if (pSlotId < 0) {
                    return;
                }

                Slot slot7 = this.slots.get(pSlotId);
                ItemStack itemstack9 = slot7.getItem();
                ItemStack itemstack10 = this.getCarried();
                pPlayer.updateTutorialInventoryAction(itemstack10, slot7.getItem(), clickaction);
                if (!this.tryItemClickBehaviourOverride(pPlayer, clickaction, slot7, itemstack9, itemstack10)) {
                    if (!net.minecraftforge.common.ForgeHooks.onItemStackedOn(itemstack9, itemstack10, slot7, clickaction, pPlayer, createCarriedSlotAccess()))
                        if (itemstack9.isEmpty()) {
                            if (!itemstack10.isEmpty()) {
                                int i3 = clickaction == ClickAction.PRIMARY ? itemstack10.getCount() : 1;
                                this.setCarried(slot7.safeInsert(itemstack10, i3));
                            }
                        } else if (slot7.mayPickup(pPlayer)) {
                            if (itemstack10.isEmpty()) {
                                int j3 = clickaction == ClickAction.PRIMARY ? itemstack9.getCount() : (itemstack9.getCount() + 1) / 2;
                                Optional<ItemStack> optional1 = slot7.tryRemove(j3, Integer.MAX_VALUE, pPlayer);
                                optional1.ifPresent((p_150421_) -> {
                                    this.setCarried(p_150421_);
                                    slot7.onTake(pPlayer, p_150421_);
                                });
                            } else if (slot7.mayPlace(itemstack10)) {
                                if (ItemStack.isSameItemSameTags(itemstack9, itemstack10)) {
                                    int k3 = clickaction == ClickAction.PRIMARY ? itemstack10.getCount() : 1;
                                    this.setCarried(slot7.safeInsert(itemstack10, k3));
                                } else if (itemstack10.getCount() <= slot7.getMaxStackSize(itemstack10)) {
                                    this.setCarried(itemstack9);
                                    slot7.setByPlayer(itemstack10);
                                }
                            } else if (ItemStack.isSameItemSameTags(itemstack9, itemstack10)) {
                                Optional<ItemStack> optional = slot7.tryRemove(itemstack9.getCount(), itemstack10.getMaxStackSize() - itemstack10.getCount(), pPlayer);
                                optional.ifPresent((p_150428_) -> {
                                    itemstack10.grow(p_150428_.getCount());
                                    slot7.onTake(pPlayer, p_150428_);
                                });
                            }
                        }
                }

                slot7.setChanged();
            }
        } else if (pClickType == ClickType.SWAP) {
            Slot slot2 = this.slots.get(pSlotId);
            ItemStack itemstack3 = inventory.getItem(pButton);
            ItemStack itemstack6 = slot2.getItem();
            if (!itemstack3.isEmpty() || !itemstack6.isEmpty()) {
                if (itemstack3.isEmpty()) {
                    if (slot2.mayPickup(pPlayer)) {
                        inventory.setItem(pButton, itemstack6);
                        ((SlotMixin)(slot2)).invokeOnSwapCraft(itemstack6.getCount());
                        slot2.setByPlayer(ItemStack.EMPTY);
                        slot2.onTake(pPlayer, itemstack6);
                    }
                } else if (itemstack6.isEmpty()) {
                    if (slot2.mayPlace(itemstack3)) {
                        int i2 = slot2.getMaxStackSize(itemstack3);
                        if (itemstack3.getCount() > i2) {
                            slot2.setByPlayer(itemstack3.split(i2));
                        } else {
                            inventory.setItem(pButton, ItemStack.EMPTY);
                            slot2.setByPlayer(itemstack3);
                        }
                    }
                } else if (slot2.mayPickup(pPlayer) && slot2.mayPlace(itemstack3)) {
                    int j2 = slot2.getMaxStackSize(itemstack3);
                    if (itemstack3.getCount() > j2) {
                        slot2.setByPlayer(itemstack3.split(j2));
                        slot2.onTake(pPlayer, itemstack6);
                        if (!inventory.add(itemstack6)) {
                            pPlayer.drop(itemstack6, true);
                        }
                    } else {
                        inventory.setItem(pButton, itemstack6);
                        slot2.setByPlayer(itemstack3);
                        slot2.onTake(pPlayer, itemstack6);
                    }
                }
            }
        } else if (pClickType == ClickType.CLONE && pPlayer.getAbilities().instabuild && this.getCarried().isEmpty() && pSlotId >= 0) {
            Slot slot5 = this.slots.get(pSlotId);
            if (slot5.hasItem()) {
                ItemStack itemstack5 = slot5.getItem();
                this.setCarried(itemstack5.copyWithCount(itemstack5.getMaxStackSize()));
            }
        } else if (pClickType == ClickType.THROW && this.getCarried().isEmpty() && pSlotId >= 0) {
            Slot slot4 = this.slots.get(pSlotId);
            int j1 = pButton == 0 ? 1 : slot4.getItem().getCount();
            ItemStack itemstack7 = slot4.safeTake(j1, Integer.MAX_VALUE, pPlayer);
            pPlayer.drop(itemstack7, true);
        } else if (pClickType == ClickType.PICKUP_ALL && pSlotId >= 0) {
            Slot slot3 = this.slots.get(pSlotId);
            ItemStack itemstack4 = this.getCarried();
            if (!itemstack4.isEmpty() && (!slot3.hasItem() || !slot3.mayPickup(pPlayer))) {
                int l1 = pButton == 0 ? 0 : this.slots.size() - 1;
                int k2 = pButton == 0 ? 1 : -1;

                for(int l2 = 0; l2 < 2; ++l2) {
                    for(int l3 = l1; l3 >= 0 && l3 < this.slots.size() && itemstack4.getCount() < itemstack4.getMaxStackSize(); l3 += k2) {
                        Slot slot8 = this.slots.get(l3);
                        if (slot8.hasItem() && canItemQuickReplace(slot8, itemstack4, true) && slot8.mayPickup(pPlayer) && this.canTakeItemForPickAll(itemstack4, slot8)) {
                            ItemStack itemstack11 = slot8.getItem();
                            if (l2 != 0 || itemstack11.getCount() != itemstack11.getMaxStackSize()) {
                                ItemStack itemstack12 = slot8.safeTake(itemstack11.getCount(), itemstack4.getMaxStackSize() - itemstack4.getCount(), pPlayer);
                                itemstack4.grow(itemstack12.getCount());
                            }
                        }
                    }
                }
            }
        }

    }



    protected void slotChangedCraftingGrid(Level pLevel, CraftingContainer pContainer) {
        if (!pLevel.isClientSide) {
            Optional<CraftingRecipe> optionalRecipe = pLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, pContainer, pLevel);
            if (optionalRecipe.isPresent()) {
                blockEntity.recipeId = optionalRecipe.get().getId();
                var result = optionalRecipe.get().assemble(pContainer, pLevel.registryAccess());
                blockEntity.result = result;
                resultSlots.setItem(0, result);
                setRemoteSlot(0, result);
                ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, result));
                var state = pLevel.getBlockState(blockEntity.getBlockPos());
                pLevel.sendBlockUpdated(blockEntity.getBlockPos(), state, state, Block.UPDATE_ALL);
            } else {
                resultSlots.setItem(0, ItemStack.EMPTY);
                setRemoteSlot(0, ItemStack.EMPTY);
                ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, ItemStack.EMPTY));
            }
        }
    }

    public void slotsChanged(Container pInventory) {
        this.access.execute((level, pos) -> {
            slotChangedCraftingGrid(level, this.craftSlots);
        });
    }

    public void removed(Player pPlayer) {
    }

    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, BlockRegistry.WALL_CRAFTING_FRAME.get());
    }

    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
