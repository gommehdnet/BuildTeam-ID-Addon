package net.gommehd.buildteam.addon.mixin;

import net.gommehd.buildteam.addon.BuildTeamAddon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixin to inject the item id into the extended tooltip info (e.g. {@code minecraft:stone})
 */
@Mixin(ItemStack.class)
public class CreativeInventoryMixin {

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    private void injectTooltip(PlayerEntity playerIn, ITooltipFlag flag, CallbackInfoReturnable<List<ITextComponent>> cir) {
        // Get the current Instance of the ItemStack
        ItemStack itemStack = ((ItemStack) (Object) this);

        // If nothing is returned by the original getTooltip, just ignore
        if (cir.getReturnValue() == null) return;

        List<ITextComponent> data = cir.getReturnValue();

        // If advanced tooltips (F3 + H) are not enabled, don't do anything with the tooltip
        if (!flag.isAdvanced()) {
            cir.setReturnValue(data);
            return;
        }

        // Loop through all generated tooltips
        for (int i = 0; i < data.size(); i++) {
            ITextComponent component = data.get(i);
            if(component == null) {
                continue;
            }
            // If the raw line equals the minecraft key, append the ID to this line
            if (component.getString().equals(Registry.ITEM.getKey(itemStack.getItem()).toString())) {
                // Get the numeric id of the current item, check if an ID exists in the file, otherwise don't do anything
                String id = BuildTeamAddon.getAddon().getItemRegistry().getId(itemStack.getItem());
                if (id == null)
                    break;

                // Append the numeric ID to the "normal" id
                data.set(i, component.deepCopy().append(new StringTextComponent(
                        " (ID: " + id + ")"
                ).setStyle(Style.EMPTY.setFormatting(TextFormatting.AQUA))));
                break;
            }
        }
        // Set the modified TextComponent list as the result of the method
        cir.setReturnValue(data);
    }

}
