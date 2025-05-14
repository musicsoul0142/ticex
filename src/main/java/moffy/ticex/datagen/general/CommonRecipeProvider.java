package moffy.ticex.datagen.general;

import java.util.function.Consumer;

import moffy.ticex.TicEX;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

public class CommonRecipeProvider extends RecipeProvider implements ICommonRecipeHelper,IConditionBuilder{

    public CommonRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    public String getModId() {
        return TicEX.MODID;
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TicEXRegistry.RECONSTRUCTION_CORE.get())
            .define('c', TinkerCommons.slimeball.get(SlimeType.SKY))
            .define('a', Items.AMETHYST_SHARD)
            .define('s', Items.SHULKER_SHELL)
            .define('p', Items.BLAZE_POWDER)
            .pattern("asa")
            .pattern("pcp")
            .pattern("asa")
            .unlockedBy("has_item", has(TinkerCommons.slimeball.get(SlimeType.SKY)))
            .save(pWriter,"cores/reconstruction_core");

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE.get())
            .define('c', TicEXRegistry.RECONSTRUCTION_CORE.get())
            .define('s', Items.NETHER_STAR)
            .pattern("ccc")
            .pattern("csc")
            .pattern("ccc")
            .unlockedBy("has_item", has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
            .save(pWriter,"cores/flickering_reconstruction_core");
    }
}
