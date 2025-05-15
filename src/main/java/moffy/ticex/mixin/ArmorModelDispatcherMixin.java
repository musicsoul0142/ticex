package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;

import moffy.ticex.client.ModifiableItemCustomRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;

@Mixin(ArmorModelManager.ArmorModelDispatcher.class)
public abstract class ArmorModelDispatcherMixin implements IClientItemExtensions{
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return new ModifiableItemCustomRenderer(null, null);
    }
}
