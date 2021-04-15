package net.gommehd.buildteam.addon;

import net.labymod.addon.AddonTransformer;
import net.labymod.api.TransformerType;

public class BuildTeamAddonTransformer extends AddonTransformer {

    @Override
    public void registerTransformers() {
        this.registerTransformer(TransformerType.VANILLA, "buildteam.mixin.json");
    }

}