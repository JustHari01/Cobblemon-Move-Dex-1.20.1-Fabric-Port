package zzhangaao1z.cobblemonmovedex.client;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokemon.moves.Learnset;
import com.cobblemon.mod.common.pokemon.FormData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class MovesScreen extends Screen {
    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 220;
    private static final int FIRST_ROW_Y = 84;
    private static final int ROW_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 9;

    private final Screen parent;
    private final FormData form;
    private final List<MoveEntry> entries = new ArrayList<>();
    private MoveCategory selectedCategory = MoveCategory.LEVEL_UP;
    private int scrollIndex;

    public MovesScreen(Screen parent, FormData form) {
        super(Component.translatable("cobblemonmovedex.ui.moves.title"));
        this.parent = parent;
        this.form = form;
        rebuildEntries();
    }

    @Override
    protected void init() {
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;
        int tabX = left + 16;
        int tabY = top + 34;

        MoveCategory[] categories = MoveCategory.values();
        for (int i = 0; i < categories.length; i++) {
            final MoveCategory category = categories[i];
            this.addRenderableWidget(Button.builder(
                    Component.translatable(category.label),
                    button -> {
                        this.selectedCategory = category;
                        this.scrollIndex = 0;
                        this.rebuildEntries();
                    })
                    .bounds(tabX + i * 57, tabY, 53, 16)
                    .build());
        }

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> this.onClose())
                .bounds(left + PANEL_WIDTH - 66, top + PANEL_HEIGHT - 21, 58, 16)
                .build());
    }

    private void rebuildEntries() {
        this.entries.clear();
        Learnset learnset = this.form.getMoves();

        switch (this.selectedCategory) {
            case LEVEL_UP -> {
                Map<Integer, List<MoveTemplate>> levels = new TreeMap<>(learnset.getLevelUpMoves());
                for (Map.Entry<Integer, List<MoveTemplate>> level : levels.entrySet()) {
                    for (MoveTemplate move : level.getValue()) {
                        this.entries.add(new MoveEntry(Integer.toString(level.getKey()), move));
                    }
                }
            }
            case TM -> addSorted(learnset.getTmMoves());
            case TUTOR -> addSorted(learnset.getTutorMoves());
            case EGG -> addSorted(learnset.getEggMoves());
            case EVOLUTION -> addSorted(learnset.getEvolutionMoves());
        }

        this.scrollIndex = Math.min(this.scrollIndex, Math.max(0, this.entries.size() - VISIBLE_ROWS));
    }

    private void addSorted(Iterable<MoveTemplate> moves) {
        List<MoveTemplate> sorted = new ArrayList<>();
        moves.forEach(sorted::add);
        sorted.sort(Comparator.comparing(MoveTemplate::getName));
        for (MoveTemplate move : sorted) {
            this.entries.add(new MoveEntry("—", move));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;
        int right = left + PANEL_WIDTH;
        int bottom = top + PANEL_HEIGHT;

        graphics.fill(left, top, right, bottom, 0xF0101720);
        graphics.fill(left, top, right, top + 1, 0xFF72A8C4);
        graphics.fill(left, bottom - 1, right, bottom, 0xFF72A8C4);
        graphics.fill(left, top, left + 1, bottom, 0xFF72A8C4);
        graphics.fill(right - 1, top, right, bottom, 0xFF72A8C4);

        String speciesName = this.form.getSpecies() == null ? "" : this.form.getSpecies().getName();
        graphics.drawCenteredString(this.font,
                Component.translatable("cobblemonmovedex.ui.moves.title", speciesName),
                this.width / 2, top + 9, 0xFFFFFFFF);

        graphics.drawString(this.font, Component.translatable("cobblemonmovedex.ui.moves.column.level"), left + 13, top + 65, 0xFF9BD4E8, false);
        graphics.drawString(this.font, Component.translatable("cobblemonmovedex.ui.moves.column.type"), left + 45, top + 65, 0xFF9BD4E8, false);
        graphics.drawString(this.font, Component.translatable("cobblemonmovedex.ui.moves.column.move"), left + 79, top + 65, 0xFF9BD4E8, false);
        graphics.drawString(this.font, Component.translatable("cobblemonmovedex.ui.moves.column.category"), left + 183, top + 65, 0xFF9BD4E8, false);
        graphics.drawString(this.font, Component.translatable("cobblemonmovedex.ui.moves.column.power"), left + 231, top + 65, 0xFF9BD4E8, false);
        graphics.drawString(this.font, Component.translatable("cobblemonmovedex.ui.moves.column.accuracy"), left + 260, top + 65, 0xFF9BD4E8, false);
        graphics.drawString(this.font, Component.translatable("cobblemonmovedex.ui.moves.column.pp"), left + 295, top + 65, 0xFF9BD4E8, false);
        graphics.fill(left + 10, top + 77, right - 10, top + 78, 0xFF526875);

        if (this.entries.isEmpty()) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("cobblemonmovedex.ui.moves.empty"),
                    this.width / 2, top + 111, 0xFFE5E5E5);
        } else {
            int end = Math.min(this.entries.size(), this.scrollIndex + VISIBLE_ROWS);
            for (int row = this.scrollIndex; row < end; row++) {
                MoveEntry entry = this.entries.get(row);
                int y = top + FIRST_ROW_Y + (row - this.scrollIndex) * ROW_HEIGHT;
                if ((row - this.scrollIndex) % 2 == 1) {
                    graphics.fill(left + 10, y - 2, right - 10, y + ROW_HEIGHT - 1, 0x442F5667);
                }

                MoveTemplate move = entry.move;
                graphics.drawString(this.font, entry.level, left + 13, y, 0xFFBECBD1, false);
                graphics.drawString(this.font, move.getElementalType().getDisplayName(), left + 45, y,
                        move.getElementalType().getHue(), false);
                graphics.drawString(this.font, move.getDisplayName(), left + 79, y, 0xFFFFFFFF, false);
                graphics.drawString(this.font, move.getDamageCategory().getName(), left + 183, y, 0xFFBECBD1, false);
                graphics.drawString(this.font, formatNumber(move.getPower()), left + 231, y, 0xFFBECBD1, false);
                graphics.drawString(this.font, formatAccuracy(move.getAccuracy()), left + 260, y, 0xFFBECBD1, false);
                graphics.drawString(this.font, Integer.toString(move.getMaxPp()), left + 295, y, 0xFFBECBD1, false);
            }
        }

        String range = this.entries.isEmpty() ? "0" : (this.scrollIndex + 1) + "–" + Math.min(this.entries.size(), this.scrollIndex + VISIBLE_ROWS);
        String footer = range + "/" + this.entries.size() + "  ·  " + this.getTitleForCategory();
        graphics.drawString(this.font, footer, left + 12, bottom - 15, 0xFF9DAAB0, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private Component getTitleForCategory() {
        return Component.translatable(this.selectedCategory.label);
    }

    private static String formatNumber(double value) {
        return value <= 0 ? "—" : Long.toString(Math.round(value));
    }

    private static String formatAccuracy(double value) {
        return value < 0 ? "—" : Math.round(value) + "%";
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        int top = (this.height - PANEL_HEIGHT) / 2;
        if (mouseY >= top + 77 && mouseY <= top + 194 && !this.entries.isEmpty()) {
            int maxScroll = Math.max(0, this.entries.size() - VISIBLE_ROWS);
            this.scrollIndex = Math.max(0, Math.min(maxScroll, this.scrollIndex - (int) Math.signum(scrollAmount)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private enum MoveCategory {
        LEVEL_UP("cobblemonmovedex.ui.moves.level"),
        TM("cobblemonmovedex.ui.moves.tm"),
        TUTOR("cobblemonmovedex.ui.moves.tutor"),
        EGG("cobblemonmovedex.ui.moves.egg"),
        EVOLUTION("cobblemonmovedex.ui.moves.evolution");

        private final String label;

        MoveCategory(String label) {
            this.label = label;
        }
    }

    private static final class MoveEntry {
        private final String level;
        private final MoveTemplate move;

        private MoveEntry(String level, MoveTemplate move) {
            this.level = level;
            this.move = move;
        }
    }
}
