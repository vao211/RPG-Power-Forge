package vao211.rpgpowerforge.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import vao211.rpgpowerforge.screen.UpgradePowerTableScreenHandler;

public class UpgradePowerTableScreen extends HandledScreen<UpgradePowerTableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/anvil.png");

    public UpgradePowerTableScreen(UpgradePowerTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.fill(x + 6, y + 4, x + 170, y + 40, 0xFFC6C6C6);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}