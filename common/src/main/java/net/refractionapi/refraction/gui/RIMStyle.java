package net.refractionapi.refraction.gui;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDir;
import net.refractionapi.refraction.Refraction;

import java.awt.*;

public class RIMStyle {
    public static void init() {
        ImGuiStyle style = ImGui.getStyle();
        Color main = Refraction.refractionPrimary;
        Color secondary = Refraction.refractionSecondary;
        float mainR = main.getRed() / 255.0F;
        float mainG = main.getGreen() / 255.0F;
        float mainB = main.getBlue() / 255.0F;
        float secondaryR = secondary.getRed() / 255.0F;
        float secondaryG = secondary.getGreen() / 255.0F;
        float secondaryB = secondary.getBlue() / 255.0F;

        style.setColor(ImGuiCol.Text, 0.9F, 0.9F, 0.9F, 1.0F);
        style.setColor(ImGuiCol.WindowBg, mainR, mainG, mainB, 0.9F);
        style.setColor(ImGuiCol.ChildBg, mainR, mainG, mainB, 0.9F);
        style.setColor(ImGuiCol.PopupBg, 0.1F, 0.1F, 0.1F, 1.0F);
        style.setColor(ImGuiCol.Border, secondaryR, secondaryG, secondaryB, 0.5F);
        style.setColor(ImGuiCol.BorderShadow, 0.15F, 0.15F, 0.15F, 0.3F);
        style.setColor(ImGuiCol.FrameBg, mainR, mainG, mainB, 0.9F);
        style.setColor(ImGuiCol.FrameBgHovered, secondaryR, secondaryG, secondaryB, 0.9F);
        style.setColor(ImGuiCol.FrameBgActive, mainR, mainG, mainB, 0.7F);
        style.setColor(ImGuiCol.TitleBg, mainR, mainG, mainB, 1.0F);
        style.setColor(ImGuiCol.TitleBgActive, secondaryR, secondaryG, secondaryB, 1.0F);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.0F, 0.0F, 0.0F, 0.5F);
        style.setColor(ImGuiCol.Button, secondaryR, secondaryG, secondaryB, 1.0F);
        style.setColor(ImGuiCol.ButtonHovered, secondaryR - 0.1F, secondaryG - 0.1F, secondaryB - 0.1F, 1.0F);
        style.setColor(ImGuiCol.ButtonActive, secondaryR - 0.1F, secondaryG - 0.1F, secondaryB - 0.1F, 1.0F);
        style.setColor(ImGuiCol.Tab, secondaryR, secondaryG, secondaryB, 0.5F);
        style.setColor(ImGuiCol.TabHovered, secondaryR, secondaryG, secondaryB, 0.8F);
        style.setColor(ImGuiCol.TabActive, secondaryR, secondaryG, secondaryB, 1.0F);
        style.setColor(ImGuiCol.TabUnfocused, 0.1F, 0.1F, 0.1F, 0.9F);
        style.setColor(ImGuiCol.CheckMark, secondaryR, secondaryG, secondaryB, 0.9F);
        style.setColor(ImGuiCol.Header, mainR, mainG, mainB, 0.5F);
        style.setColor(ImGuiCol.HeaderHovered, mainR - 0.1F, mainG - 0.1F, mainB - 0.1F, 0.5F);
        style.setColor(ImGuiCol.HeaderActive, mainR - 0.1F, mainG - 0.1F, mainB - 0.1F, 0.5F);
        style.setColor(ImGuiCol.Separator, secondaryR, secondaryG, secondaryB, 1.0F);
        style.setColor(ImGuiCol.SeparatorHovered, secondaryR - 0.1F, secondaryG - 0.1F, secondaryB - 0.1F, 0.9F);
        style.setColor(ImGuiCol.SeparatorActive, secondaryR, secondaryG, secondaryB, 1.0F);
        style.setColor(ImGuiCol.ResizeGrip, secondaryR, secondaryG, secondaryB, 0.8F);
        style.setColor(ImGuiCol.ResizeGripHovered, secondaryR - 0.1F, secondaryG - 0.1F, secondaryB - 0.1F, 1.0F);
        style.setColor(ImGuiCol.ResizeGripActive, secondaryR - 0.1F, secondaryG - 0.1F, secondaryB - 0.1F, 1.0F);

        style.setWindowMenuButtonPosition(ImGuiDir.Right);
        style.setWindowPadding(5F, 4F);
        style.setWindowBorderSize(1);
        style.setWindowRounding(1);

        style.setCellPadding(4, 2);
        style.setItemSpacing(7, 4);
        style.setItemInnerSpacing(5, 5);
        style.setTouchExtraPadding(0, 0);
        style.setFramePadding(10, 5);
        style.setIndentSpacing(15);
        style.setScrollbarSize(10);
        style.setGrabMinSize(10);

        style.setPopupBorderSize(1);
        style.setFrameBorderSize(1);
        style.setTabBorderSize(1);
        style.setChildBorderSize(1);

        style.setChildRounding(1);
        style.setFrameRounding(1);
        style.setPopupRounding(1);
        style.setScrollbarRounding(1);
        style.setGrabRounding(1);
        style.setLogSliderDeadzone(4);
        style.setTabRounding(1);
    }
}
