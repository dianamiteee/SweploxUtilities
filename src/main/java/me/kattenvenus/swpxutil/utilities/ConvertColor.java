package me.kattenvenus.swpxutil.utilities;

import java.awt.*;

public class ConvertColor {

    public static String getStringFromColor(Color clr) {

        if (clr == null) clr = new Color(0, 0, 1);

        return String.format("#%02X%02X%02X", clr.getRed(), clr.getGreen(), clr.getBlue());
    }

    public static Color getColorFromString(String hex) {

        if (hex.toCharArray()[0] == '#') {

            if (hex.equalsIgnoreCase("#000000")) hex = "#000001";

            return Color.decode(hex);
        } else {
            if (hex.equalsIgnoreCase("000000")) hex = "000001";
            return Color.decode("#" + hex);
        }

    }

}
