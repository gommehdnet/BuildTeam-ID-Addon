package net.gommehd.buildteam.addon.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.gommehd.buildteam.addon.BuildTeamAddon;
import net.gommehd.buildteam.addon.updater.UpdateResponse;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.settings.elements.AddonElement;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Really dirty method to inject a custom "reload ids" button to the addon overview
 */
@SuppressWarnings("ConstantConditions")
@Mixin(AddonElement.class)
public abstract class LabyModAddonMixin {

    /**
     * Draw the "reload ids" button at the location, where the settings button would be drawn.
     *
     * @see AddonElement#draw(MatrixStack, int, int, int, int, int, int)
     */
    @Inject(method = "draw", at = @At("RETURN"), cancellable = true, remap = false)
    public void injectDraw(MatrixStack matrixStack, int x, int y, int maxX, int maxY, int mouseX, int mouseY, CallbackInfo ci) {
        AddonElement addon = ((AddonElement) (Object) this);
        if (addon == null || !addon.isAddonInstalled())
            return;
        if (!addon.getInstalledAddon().equals(BuildTeamAddon.getAddon()))
            return;
        if (addon.getLastActionState() != AddonInfo.AddonActionState.UNINSTALL_REVOKE)
            return;
        int marginX = 30;
        int marginY = (maxY - y - 14) / 2;
        if (drawButton(matrixStack, new ResourceLocation("labymod/addons/buildteam/reload.png"), y, 14, 6, marginX, marginY, maxX, maxY, mouseX, mouseY)) {
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "Refresh Block IDs");
            hoverButtonId = 99;
        }
    }

    /**
     * Inject a mouse click event for the custom button drawn in {@code injectDraw}.
     * In this click listener, the update will be performed with some pretty notifications for the user.
     *
     * @see AddonElement#mouseClicked(int, int, int)
     */
    @Inject(method = "mouseClicked", at = @At("RETURN"), cancellable = true, remap = false)
    public void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (hoverButtonId != 99 || BuildTeamAddon.getAddon().getUpdater().getInProgress().get())
            return;
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        Minecraft.getInstance().getToastGui().add(new SystemToast(
                SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER,
                new StringTextComponent("GommeHD.net BuildTeam"),
                new StringTextComponent("Retrieving block IDs")
        ));
        BuildTeamAddon.getAddon().getUpdater().update(response -> {
            IFormattableTextComponent sub = new StringTextComponent("Update failed")
                    .setStyle(Style.EMPTY.setFormatting(TextFormatting.RED));
            if (response == UpdateResponse.SUCCESS) {
                sub = new StringTextComponent("Update succeeded")
                        .setStyle(Style.EMPTY.setFormatting(TextFormatting.GREEN));
            }
            if (response == UpdateResponse.SKIPPED) {
                sub = new StringTextComponent("Already up to date")
                        .setStyle(Style.EMPTY.setFormatting(TextFormatting.GOLD));
            }
            Minecraft.getInstance().getToastGui().add(new SystemToast(
                    SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER,
                    new StringTextComponent("GommeHD.net BuildTeam"), sub));
        });
    }

    @Shadow(remap = false)
    abstract boolean drawButton(MatrixStack matrixStack, ResourceLocation resourceLocation, int y, int buttonSize, int buttonPadding, int marginX, int marginY, int maxX, int maxY, int mouseX, int mouseY);

    @Shadow(remap = false)
    private int hoverButtonId;

}
