package ca.sperrer.p0t4t0sandwich.ampservermanager;

public class VersionUtils {
    // Check if the server is running Folia
    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    // Check if the server is running Magma
    public static boolean isMagma() {
        try {
            Class.forName("org.magmafoundation.magma.Magma");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    // Check if the server is running Mohist
    public static boolean isMohist() {
        try {
            Class.forName("org.mohistmc.MohistMC");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    // Check if the server is running Arclight
    public static boolean isArclight() {
        try {
            Class.forName("io.izzel.arclight.common.ArclightVersion");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
