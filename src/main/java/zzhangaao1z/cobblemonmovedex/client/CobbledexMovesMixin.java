package zzhangaao1z.cobblemonmovedex.client;

import com.cobblemon.mod.common.pokemon.FormData;
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class CobbledexMovesMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void cobblemonmovedex$addMovesButton(CallbackInfo ci) {
        if (!((Object) this instanceof CobbledexGUI cobbledex)) {
            return;
        }

        Screen screen = (Screen) (Object) this;
        int left = (screen.width - CobbledexGUI.BASE_WIDTH) / 2;
        int top = (screen.height - CobbledexGUI.BASE_HEIGHT) / 2;

        Button movesButton = Button.builder(
                Component.translatable("cobblemonmovedex.ui.moves.open"),
                button -> {
                    FormData form = cobbledex.getSelectedPokemon();
                    if (form == null) {
                        form = CobbledexGUI.Companion.getPreviewForm();
                    }
                    if (form != null) {
                        Minecraft.getInstance().setScreen(new MovesScreen(cobbledex, form));
                    }
                })
                .bounds(left + 225, top + 178, 36, 11)
                .build();

        screen.addRenderableWidget(movesButton);
    }
}
