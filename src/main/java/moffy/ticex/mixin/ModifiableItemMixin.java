package moffy.ticex.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;

import moffy.ticex.client.ModifiableItemCustomRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

@Mixin(ModifiableItem.class)
public abstract class ModifiableItemMixin extends Item{

    public ModifiableItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new ModifiableItemCustomRenderer(null, null);
            }
        });
    }
    
}
