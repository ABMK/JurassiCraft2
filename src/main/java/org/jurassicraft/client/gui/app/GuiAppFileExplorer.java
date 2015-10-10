package org.jurassicraft.client.gui.app;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.gui.GuiPaleoTab;
import org.jurassicraft.common.entity.data.JCPlayerData;
import org.jurassicraft.common.entity.data.JCPlayerDataClient;
import org.jurassicraft.common.message.JCNetworkManager;
import org.jurassicraft.common.message.MessageRequestFile;
import org.jurassicraft.common.paleopad.App;
import org.jurassicraft.common.paleopad.AppFileExplorer;
import org.jurassicraft.common.paleopad.JCFile;

import java.util.List;
import java.util.regex.Pattern;

public class GuiAppFileExplorer extends GuiApp
{
    private static final ResourceLocation texture = new ResourceLocation(JurassiCraft.MODID, "textures/gui/paleo_pad/apps/file_explorer.png");

    private boolean intro;

    public GuiAppFileExplorer(App app)
    {
        super(app);
    }

    private boolean loading;

    @Override
    public void render(int mouseX, int mouseY, GuiPaleoTab gui)
    {
        super.renderButtons(mouseX, mouseY, gui);

        AppFileExplorer app = (AppFileExplorer) getApp();

        if (intro)
        {
            gui.drawScaledText("Hello " + mc.thePlayer.getDisplayName().getFormattedText() + "! Welcome to " + app.getName() + "!", 4, 10, 1.0F, 0xFFFFFF);
            mc.getTextureManager().bindTexture(texture);
            gui.drawScaledTexturedModalRect(1, 20, 0, 0, 32, 32, 32, 32, 1.0F);
            gui.drawScaledText("Using " + app.getName() + " you can browse all your files!", 34, 29, 0.7F, 0xFFFFFF);
        }
        else
        {
            String path = app.getPath();

            List<JCFile> filesAtPath = JCPlayerDataClient.getPlayerData().getFilesAtPath(path);

            if (loading)
            {
                if (filesAtPath != null)
                {
                    loading = false;
                }
                else
                {
                    gui.drawScaledText("Downloading files...", 4, 10, 1.0F, 0xFFFFFF);
                }
            }
            else
            {
                int y = 5;

                for (JCFile file : filesAtPath)
                {
                    if (file != null && file.getName().length() > 0 && y < 125)
                    {
                        gui.drawBoxOutline(5, y, 207, 12, 1, 1.0F, 0x606060);
                        String name = file.getName();

                        if (name.length() > 23)
                        {
                            name = name.substring(0, 23) + "...";
                        }

                        gui.drawScaledText(name, 7, y + 3, 1.0F, 0xFFFFFF);
                        gui.drawScaledText(file.isFile() ? "       File" : "Directory", 160, y + 3, 1.0F, 0x7F7F7F);

                        y += 15;
                    }
                }

                gui.drawScaledRect(217, 5, 7, 140, 1.0F, 0x7F7F7F);
                gui.drawBoxOutline(217, 5, 7, 140, 1, 1.0F, 0x606060);

                gui.drawBoxOutline(5, 132, 65, 12, 1, 1.0F, 0x606060);
                gui.drawScaledText("<-- Move up", 8, 135, 1.0F, 0xFFFFFF);
            }
//            gui.drawBoxOutline(10, 10, 100, 15, 1, 1.0F, 0x606060);
        }
    }

    @Override
    public void actionPerformed(GuiButton button)
    {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, GuiPaleoTab gui)
    {
        ScaledResolution dimensions = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        mouseX -= dimensions.getScaledWidth() / 2 - 115;
        mouseY -= 65;

        if (intro)
        {

        }
        else
        {
            AppFileExplorer app = (AppFileExplorer) getApp();

            String path = app.getPath();

            List<JCFile> filesAtPath = JCPlayerDataClient.getPlayerData().getFilesAtPath(path);

            if (filesAtPath != null)
            {
                int y = 5;

                for (JCFile file : filesAtPath)
                {
                    if (file != null && file.getName().length() > 0 && file.isDirectory() && y < 125)
                    {
                        if (mouseX > 5 && mouseX < 212 && mouseY > y && mouseY < y + 12)
                        {
                            app.setPath(file.getPath());

                            if (!(path.equals(app.getPath())))
                            {
                                request(app.getPath());
                            }

                            break;
                        }

                        y += 15;
                    }
                }
            }

            if (mouseX > 5 && mouseX < 70 && mouseY > 132 && mouseY < 144)
            {
                String[] split = path.split(Pattern.quote("/"));

                if (split.length > 1)
                {
                    app.setPath(path.substring(0, path.lastIndexOf("/")));
                }
                else
                {
                    app.setPath("");
                }

                if (!(path.equals(app.getPath())))
                {
                    request(app.getPath());
                }
            }
        }
    }

    private void request(String path)
    {
        if (path == null)
        {
            path = "";
        }

        JCNetworkManager.networkWrapper.sendToServer(new MessageRequestFile(path));
        JCPlayerData playerData = JCPlayerDataClient.getPlayerData();

        if (path.length() == 0)
        {
            playerData.clearRootFiles();
        }
        else
        {
            playerData.remove(playerData.getFileFromPath(path));
        }

        loading = true;
    }

    @Override
    public void init()
    {
        intro = !app.hasBeenPreviouslyOpened();

        request(((AppFileExplorer) app).getPath());
    }

    @Override
    public ResourceLocation getTexture(GuiPaleoTab gui)
    {
        return texture;
    }
}
