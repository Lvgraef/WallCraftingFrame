package io.github.itskillerluc.wallcraftingframe.blockentity;

import io.github.itskillerluc.wallcraftingframe.init.BlockEntityRegistry;
import io.github.itskillerluc.wallcraftingframe.menu.WallCraftingFrameMenu;
import io.github.itskillerluc.wallcraftingframe.menu.util.DummyCraftingContainer;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WallCraftingFrameBlockEntity extends BlockEntity implements MenuProvider {
    public ItemStack result;
    public ResourceLocation recipeId;

    public WallCraftingFrameBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.WALL_CRAFTING_FRAME.get(), pPos, pBlockState);
    }

    public boolean onCraft(Player player, boolean bulkCraft) {
        if (!level.isClientSide()) {
            for (CraftingRecipe recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
                if (recipe.getId().equals(recipeId)) {
                    var ingredients = recipe.getIngredients();
                    IntList slots = new IntArrayList();
                    int resultCount = 0;
                    outer:
                    do {
                        NonNullList<ItemStack> items = player.getInventory().items;
                        IntList innerSlots = new IntArrayList();
                        for (Ingredient ingredient : ingredients) {
                            boolean found = false;
                            for (int i = 0; i < items.size(); i++) {
                                ItemStack item = items.get(i);
                                if (ingredient.test(item)) {
                                    int finalI = i;
                                    if (slots.intStream().filter(num -> num == finalI).count() >= item.getCount())
                                        continue;
                                    innerSlots.add(i);
                                    found = true;
                                }
                            }
                            if (!found) {
                                bulkCraft = false;
                                break outer;
                            }
                        }
                        slots.addAll(innerSlots);
                        resultCount++;
                    } while (bulkCraft);

                    if (slots.isEmpty()) {
                        player.displayClientMessage(Component.translatable("guid.wallcraftingframe.no_ingredients"), true);
                        return false;
                    }

                    for (int slot : slots) {
                        player.getInventory().getItem(slot).shrink(1);
                    }

                    CraftingContainer container = getDummyContainer(ingredients);
                    var result = recipe.assemble(container, level.registryAccess());
                    player.addItem(result.copyWithCount(resultCount));
                }
            }
        }
        return true;
    }

    private CraftingContainer getDummyContainer(NonNullList<Ingredient> ingredients) {
        NonNullList<ItemStack> ingredientList = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ingredientList.set(i, ingredient.getItems()[0]);
        }
        return new DummyCraftingContainer(3, 3, ingredientList);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("guid.wallcraftingframe.wallcraftingframe");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WallCraftingFrameMenu(pContainerId, pPlayerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (recipeId != null) {
            pTag.putString("recipeId", recipeId.toString());
        }
        if (result != null) {
            var tag = new CompoundTag();
            result.save(tag);
            pTag.put("result", tag);
        }

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("recipeId")) {
            recipeId = ResourceLocation.parse(pTag.getString("recipeId"));
        } else {
            recipeId = null;
        }
        if (pTag.contains("result")) {
            result = ItemStack.of(pTag.getCompound("result"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        var tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
