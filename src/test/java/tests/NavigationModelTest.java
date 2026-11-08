package tests;

import org.junit.jupiter.api.Test;
import ui.navigation.NavigationModel;
import ui.navigation.NavigationModel.NavigationEvent;
import ui.navigation.ScreenId;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NavigationModelTest {

    private static List<NavigationEvent> record(NavigationModel model) {
        List<NavigationEvent> events = new ArrayList<>();
        model.addObserver(events::add);
        return events;
    }

    @Test
    public void goToPushesHistoryAndBackReturns() {
        NavigationModel nav = new NavigationModel();
        List<NavigationEvent> events = record(nav);

        nav.goTo(ScreenId.MAIN_MENU);
        nav.goTo(ScreenId.SETTINGS);
        assertEquals(ScreenId.SETTINGS, nav.current());
        assertTrue(nav.canGoBack());

        assertTrue(nav.back());
        assertEquals(ScreenId.MAIN_MENU, nav.current());
        assertFalse(nav.canGoBack());
        assertFalse(nav.back());

        assertNull(events.get(0).from());
        assertEquals(ScreenId.MAIN_MENU, events.get(2).to());
        assertEquals(ScreenId.SETTINGS, events.get(2).from());
    }

    @Test
    public void goingToTheCurrentScreenDoesNothing() {
        NavigationModel nav = new NavigationModel();
        nav.goTo(ScreenId.MAIN_MENU);
        List<NavigationEvent> events = record(nav);

        nav.goTo(ScreenId.MAIN_MENU);

        assertTrue(events.isEmpty());
        assertFalse(nav.canGoBack());
    }

    @Test
    public void replaceWithDoesNotKeepTheScreenInHistory() {
        NavigationModel nav = new NavigationModel();
        nav.goTo(ScreenId.MAIN_MENU);
        nav.goTo(ScreenId.PLAY);
        nav.replaceWith(ScreenId.GAME);

        assertEquals(ScreenId.GAME, nav.current());
        nav.back();
        assertEquals(ScreenId.MAIN_MENU, nav.current()); // PLAY ficou de fora
    }

    @Test
    public void resetToClearsHistory() {
        NavigationModel nav = new NavigationModel();
        nav.goTo(ScreenId.MAIN_MENU);
        nav.goTo(ScreenId.ACHIEVEMENTS);
        nav.resetTo(ScreenId.MAIN_MENU);

        assertEquals(ScreenId.MAIN_MENU, nav.current());
        assertFalse(nav.canGoBack());
    }
}
