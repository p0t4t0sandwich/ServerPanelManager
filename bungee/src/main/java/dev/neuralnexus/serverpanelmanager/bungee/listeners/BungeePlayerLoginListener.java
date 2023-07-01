package dev.neuralnexus.serverpanelmanager.bungee.listeners;

import dev.neuralnexus.template.common.listneners.TemplatePlayerLoginListener;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeePlayerLoginListener implements Listener, TemplatePlayerLoginListener {
    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        // Do stuff
    }
}
