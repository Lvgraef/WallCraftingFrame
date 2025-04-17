package io.github.itskilerluc.wallcraftingframe.blockentity;

import io.github.itskilerluc.wallcraftingframe.menu.WallCraftingFrameMenu;
import io.github.itskilerluc.wallcraftingframe.menu.util.DummyCraftingContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WallCraftingFrameBlockEntity extends BlockEntity implements MenuProvider {
    public ResourceLocation recipeId;

    public WallCraftingFrameBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void onCraft(Player player, boolean bulkCraft) {
        if (!level.isClientSide()) {
            level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).forEach(recipe -> {
                if (recipe.getId().equals(recipeId)) {
                    var ingredients = recipe.getIngredients();
                    Map<Ingredient, List<ItemStack>> stacks = new HashMap<>();
                    for (Ingredient ingredient : ingredients) {
                        List<ItemStack> stackList = new ArrayList<>();
                        for (ItemStack item : player.getInventory().items) {
                            if (ingredient.test(item)) {
                                stackList.add(item);
                            }
                        }
                        stacks.put(ingredient, stackList);
                    }
                    ItemStack resultStack;
                    if (bulkCraft) {
                        OptionalInt min = stacks.values().stream().mapToInt(list -> list.stream().mapToInt(ItemStack::getCount).sum()).min();
                        if (min.isEmpty()) return;
                        for (Ingredient ingredient : ingredients) {
                            int count = min.getAsInt();
                            for (ItemStack itemStack : stacks.get(ingredient)) {
                                if (count == 0) break;
                                var remainder = Math.max(count - itemStack.getCount(), 0);
                                itemStack.shrink(count);
                                count = remainder;
                            }
                        }
                        CraftingContainer container = getDummyContainer(ingredients);
                        var result = recipe.assemble(container, level.registryAccess());
                        resultStack = result.copyWithCount(min.getAsInt());
                    } else {
                        for (Ingredient ingredient : ingredients) {
                            for (ItemStack itemStack : stacks.get(ingredient)) {
                                itemStack.shrink(1);
                                break;
                            }
                        }
                        CraftingContainer container = getDummyContainer(ingredients);
                        resultStack = recipe.assemble(container, level.registryAccess());
                    }
                    player.addItem(resultStack);
                }
            });
        }
    }

    private CraftingContainer getDummyContainer(NonNullList<Ingredient> ingredients) {
        return new DummyCraftingContainer(3, 3, NonNullList.of(ItemStack.EMPTY,
                ingredients.get(0).getItems()[0],
                ingredients.get(1).getItems()[0],
                ingredients.get(2).getItems()[0],
                ingredients.get(3).getItems()[0],
                ingredients.get(4).getItems()[0],
                ingredients.get(5).getItems()[0],
                ingredients.get(6).getItems()[0],
                ingredients.get(7).getItems()[0],
                ingredients.get(8).getItems()[0]
        ));}

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

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("recipeId")) {
            recipeId = ResourceLocation.parse(pTag.getString("recipeId"));
        } else {
            recipeId = null;
        }
    }
}
