package moffy.ticex.datagen.modifier;

import java.util.function.Consumer;

import dan200.computercraft.shared.ModRegistry;
import moffy.addonapi.AddonAPI;
import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

public class ModifierRecipeProvider extends RecipeProvider implements IConditionBuilder, IRecipeHelper {

    String upgradeFolder = "tools/modifiers/upgrade/";
    String abilityFolder = "tools/modifiers/ability/";
    String slotlessFolder = "tools/modifiers/slotless/";
    String upgradeSalvage = "tools/modifiers/salvage/upgrade/";
    String abilitySalvage = "tools/modifiers/salvage/ability/";
    String defenseFolder = "tools/modifiers/defense/";
    String defenseSalvage = "tools/modifiers/salvage/defense/";
    String compatFolder = "tools/modifiers/compat/";
    String compatSalvage = "tools/modifiers/salvage/compat/";
    String worktableFolder = "tools/modifiers/worktable/";

    public ModifierRecipeProvider(PackOutput generator) {
        super(generator);
        CraftingHelper.register(new ModsAvailableCondition.Serializer());
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> topConsumer = withCondition(consumer, modsAvailable(new ResourceLocation(TicEX.MODID, "computercraft_compat")));
        
        if(TicEXRegistry.MODEM_MODIFIER != null){
            ModifierRecipeBuilder.modifier(TicEXRegistry.MODEM_MODIFIER)
                .setTools(TinkerTags.Items.CHESTPLATES)
                .addInput(ModRegistry.Items.WIRELESS_MODEM_ADVANCED.get())
                .setSlots(SlotType.UPGRADE, 1)
                .setMaxLevel(1).checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXRegistry.MODEM_MODIFIER.getId(), upgradeSalvage))
                .save(topConsumer, prefix(TicEXRegistry.MODEM_MODIFIER.getId(), upgradeFolder));
        }
    }
    
    public ICondition modsAvailable(ResourceLocation rl){
        return new ModsAvailableCondition(new ResourceLocation(AddonAPI.MODID, "mods_available"), rl);
    }

    @Override
    public String getModId() {
        return TicEX.MODID;
    }
}
