package ca.sperrer.p0t4t0sandwich.template.velocity.listeners.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import dev.neuralnexus.template.common.listneners.TemplatePlayerLoginListener;

/**
 * Listens for player logins and adds the TaterPlayer to the cache.
 */
public class VelocityPlayerLoginListener implements TemplatePlayerLoginListener {
    /**
     * Called when a player logs in.
     * @param event The player login event
     */
    @Subscribe
    public void onPlayerLogin(ServerConnectedEvent event) {
        // If player is switching servers, don't run this function
        if (event.getPreviousServer().isPresent()) return;

        // Do stuff
    }
}
