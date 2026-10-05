package com.starcallingassist.modules.worldmapoverlay.worldmapstardetails;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.constants.SpriteConstants;
import com.starcallingassist.enums.SignalEventType;
import com.starcallingassist.events.AnnouncementAttributesUpdated;
import com.starcallingassist.events.SignalEvent;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;

import com.starcallingassist.modules.spriteutil.SpriteUtilModule;
import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui.Padding;
import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui.StarDetailsActiveStarButton;
import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui.StarDetailsButton;
import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui.StarDetailsIconTextField;
import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui.StarDetailsTextField;
import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui.StarDetailsUIElement;
import com.starcallingassist.enums.StarLocationDetails;
import com.starcallingassist.objects.StarLocationTransport;
import com.starcallingassist.util.ConcurrentUtil;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.api.Quest;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.overlay.Overlay;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.http.api.worlds.World;

/**
 * Draggable and closeable overlay rendered on the world map. Displays detailed information about star locations.
 */
@Slf4j
@Singleton
public class WorldMapStarDetailsOverlay extends Overlay
{
	// <editor-fold desc="Static Fields">
	/**
	 * Masks the bit used to toggle world map tooltips in the {@link #WORLD_MAP_TOGGLES_VARBIT}.
	 */
	private static final int TOOLTIP_TOGGLE_BITMASK = 0b1000;

	/**
	 * Varbit containing a bit field used to toggle functions on the world map.
	 * A set bit indicates a specific function being toggled off.
	 * <p>
	 * Bit position 0 = 'You are here' widget.<br>
	 * Bit position 1 = Intra-map links.<br>
	 * Bit position 2 = Map labels.<br>
	 * Bit position 3 = Icon tooltips.<br>
	 * Bit position 4 = Effects.
	 * </p>
	 */
	private static final int WORLD_MAP_TOGGLES_VARBIT = 5640;

	private static final String POSITION_CONFIG_KEY = "world_map_star_details_view_position";

	private static final int BORDER_THICKNESS = 3;
	private static final int BORDER_SPRITE_SIZE = 32;
	private static final int BORDER_LEFT_INTERSECT_WIDTH = 7;
	private static final int BORDER_LEFT_INTERSECT_HEIGHT = 12;
	private static final int BORDER_RIGHT_INTERSECT_WIDTH = 8;
	private static final int BORDER_RIGHT_INTERSECT_HEIGHT = 11;
	private static final int BORDER_INTERSECT_VERTICAL_OFFSET = -4;

	private static final int ACTIVE_STAR_BUTTON_WIDTH = 63;
	private static final int ACTIVE_STAR_BUTTON_HEIGHT = 40;

	private static final int CLOSE_BUTTON_SPRITE_SIZE = 16;

	private static final Padding PADDING = new Padding(2);
	// </editor-fold>

	// <editor-fold desc="Private Fields">
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private EventBus eventBus;

	@Inject
	private ConfigManager configManager;

	/**
	 * Whether the overlay should be rendered.
	 */
	@Getter
	private boolean visible;

	/**
	 * Whether the overlay is currently being hovered.
	 */
	private boolean hovering;

	/**
	 * Location of the currently displayed star.
	 */
	private String currentStarLocation;

	/**
	 * X position relative to {@link #mapViewWidget}.
	 */
	private int x = 0;

	/**
	 * Y position relative to {@link #mapViewWidget}.
	 */
	private int y = 0;

	/**
	 * Height of the entire overlay.
	 */
	private int height = 250;

	/**
	 * Width of the entire overlay.
	 */
	private int width = 204;

	/**
	 * Y position at the bottom of the bottom most {@link StarDetailsUIElement} relative to {@link #mapViewWidget}.
	 */
	private int lastElementBottomY;

	// Y positions of the three horizontal section dividers.
	private int firstBorderIntersectY;
	private int secondBorderIntersectY;
	private int thirdBorderIntersectY;

	// Images that are always used.
	private BufferedImage background;
	private BufferedImage verticalBorder;
	private BufferedImage horizontalBorder;
	private BufferedImage leftIntersectBorder;
	private BufferedImage rightIntersectBorder;
	private BufferedImage topLeftCornerBorder;
	private BufferedImage topRightCornerBorder;
	private BufferedImage bottomLeftCornerBorder;
	private BufferedImage bottomRightCornerBorder;
	private BufferedImage closeButton;
	private BufferedImage closeButtonHover;
	private BufferedImage activeStarButton;
	private BufferedImage activeStarButtonHover;
	private BufferedImage questIcon;

	/**
	 * The widget containing the rendered and interactive world map in the world map interface.
	 */
	private Widget mapViewWidget;

	/**
	 * A previous {@link #WORLD_MAP_TOGGLES_VARBIT} value saved by {@link #hideWorldMapTooltips()}.
	 * The value is used by {@link #restoreWorldMapTooltips()} to restore the varbit. Value
	 * <code>Integer.MIN_VALUE</code> indicates that no previous varbit value is saved.
	 */
	private int savedTooltipVarbitState = Integer.MIN_VALUE;

	/**
	 * Mouse position at the previously received {@link #mouseDragged(MouseEvent)} event for this overlay. Value
	 * <code>(Integer.MIN_VALUE, Integer.MIN_VALUE)</code> indicates that dragging of the overlay has not yet started.
	 */
	private Point mouseDragPrevious = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

	/**
	 * World ID -> {@link StarListEntryAttributes} mapping for all currently known stars.
	 */
	private ConcurrentHashMap<Integer, StarListEntryAttributes> announcementAttributes = new ConcurrentHashMap<>();

	/**
	 * Contains all {@link StarDetailsUIElement} to be rendered.
	 */
	private List<StarDetailsUIElement> uiElements = new ArrayList<>();

	/**
	 * Lock used to prevent {@link ConcurrentModificationException} in {@link #uiElements} while modifying the UI.
	 */
	private final ReadWriteLock uiElementsLock = new ReentrantReadWriteLock();
	// </editor-fold>

	// <editor-fold desc="Constructor">
	public WorldMapStarDetailsOverlay()
	{
		SpriteUtilModule.registerLoadSpritesCallback(this::loadSprites);

		setMovable(false);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPosition(OverlayPosition.DYNAMIC);
	}
	// </editor-fold>

	// <editor-fold desc="Public Methods">
	@Override
	public Dimension render(Graphics2D graphics2D)
	{
		if (!visible || isWorldMapClosed() || isWorldMapResizing())
		{
			return null;
		}

		// Makes sure the overlay renders within the world map.
		enforceBounds();

		// Make the overlay semi-transparent while dragged.
		if (isDragging())
		{
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		}

		renderBackground(graphics2D);
		renderUiElements(graphics2D);

		return new Dimension(width, height);
	}

	/**
	 * Open the overlay for a specific star location.
	 *
	 * @param starLocation The star location to display.
	 */
	public void showStarLocation(String starLocation)
	{
		if (isWorldMapClosed())
		{
			return;
		}

		mapViewWidget = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);

		if (rebuildUiElements(starLocation))
		{
			visible = true;
			currentStarLocation = starLocation;
			updateBounds(x, y, width, lastElementBottomY + BORDER_THICKNESS);
		}
		else
		{
			closeOverlay();
		}
	}

	/**
	 * Load overlay settings saved in the plugin config.
	 */
	public void loadSavedSettings()
	{
		Point savedPosition = configManager.getConfiguration(StarCallingAssistConfig.CONFIG_GROUP,
			POSITION_CONFIG_KEY, Point.class);

		if (savedPosition != null)
		{
			x = savedPosition.x;
			y = savedPosition.y;
		}
	}

	/**
	 * Fills a list with {@link MenuEntry} depending on what UI element is being hovered.
	 * As click events on this overlay are consumed, any menu entries added are only for cosmetic purposes.
	 *
	 * @param menu        The menu instance to create menu entries from.
	 * @param menuEntries The list of menu entries to populate.
	 */
	public void fillMenuEntries(Menu menu, List<MenuEntry> menuEntries)
	{
		ConcurrentUtil.acquireLockAndRun(uiElementsLock.readLock(), true, () -> {
			for (final StarDetailsUIElement uiElement : uiElements)
			{
				if (uiElement.fillMenuEntries(menu, menuEntries))
				{
					break;
				}
			}
		});
	}

	/**
	 * Callback for drag events that started within the bounds of this overlay. Drags the overlay.
	 *
	 * @param mouseEvent The mouse event.
	 */
	public void mouseDragged(MouseEvent mouseEvent)
	{
		mouseEvent.consume();

		// Case for starting to drag overlay
		if (!isDragging())
		{
			if (canStartDragging())
			{
				mouseDragPrevious = mouseEvent.getPoint();
			}

			return;
		}

		int deltaX = mouseEvent.getX() - mouseDragPrevious.x;
		int deltaY = mouseEvent.getY() - mouseDragPrevious.y;

		x = x + deltaX;
		y = y + deltaY;

		mouseDragPrevious = mouseEvent.getPoint();
	}

	/**
	 * Callback for mouse release events. Stops any dragging and saves the position.
	 */
	public void mouseReleased()
	{
		if (isDragging())
		{
			// Update saved position in config
			configManager.setConfiguration(StarCallingAssistConfig.CONFIG_GROUP,
				POSITION_CONFIG_KEY, new Point(x, y));
		}

		mouseDragPrevious = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	/**
	 * Callback for mouse press events. Forwards the event to all UI elements.
	 *
	 * @param mouseEvent The mouse event
	 */
	public void mousePressed(MouseEvent mouseEvent)
	{
		ConcurrentUtil.acquireLockAndRun(uiElementsLock.writeLock(), false, () -> {
			for (final StarDetailsUIElement uiElement : uiElements)
			{
				uiElement.mousePressed(mouseEvent);

				if (mouseEvent.isConsumed())
				{
					break;
				}
			}
		});
	}

	/**
	 * Communicate the most recently known mouse position to this overlay. Used to determine
	 * hovering status for the overlay as a whole and its individual elements.
	 *
	 * @param newMousePosition The most recently known mouse position.
	 */
	public void updateLastMousePosition(Point newMousePosition)
	{
		if (mapViewWidget == null)
		{
			return;
		}

		Point localMousePosition = new Point(newMousePosition.x - (x + mapViewWidget.getCanvasLocation().getX()),
			newMousePosition.y - (y + mapViewWidget.getCanvasLocation().getY()));

		boolean newPosHovering = this.getBounds().contains(newMousePosition);

		if (newPosHovering && !hovering)
		{
			hideWorldMapTooltips();
		}
		else if (!newPosHovering && hovering)
		{
			restoreWorldMapTooltips();
		}

		ConcurrentUtil.acquireLockAndRun(uiElementsLock.readLock(), true, () -> {
			for (final StarDetailsUIElement uiElement : uiElements)
			{
				uiElement.updateMousePosition(localMousePosition);
			}
		});

		hovering = newPosHovering;
	}
	// </editor-fold>

	// <editor-fold desc="Private Methods">
	@Subscribe
	private void onAnnouncementAttributesUpdated(AnnouncementAttributesUpdated announcementAttributesUpdated)
	{
		announcementAttributes = announcementAttributesUpdated.getAnnouncementAttributes();

		// Update the overlay if open
		if (visible && currentStarLocation != null)
		{
			showStarLocation(currentStarLocation);
		}
	}

	/**
	 * Renders the overlay background and borders.
	 *
	 * @param graphics2D The {@link Graphics2D} instance to draw to.
	 */
	private void renderBackground(Graphics2D graphics2D)
	{
		// Create 1px wide black border around the overlay
		graphics2D.setColor(Color.BLACK);
		graphics2D.drawRect(-1, -1, width + 1, height + 1);

		graphics2D.drawImage(background, 0, 0, width, height, null);

		// Draw all border corner sprites
		graphics2D.drawImage(topLeftCornerBorder, 0, 0, BORDER_SPRITE_SIZE, BORDER_SPRITE_SIZE, null);
		graphics2D.drawImage(topRightCornerBorder, width - BORDER_SPRITE_SIZE, 0, BORDER_SPRITE_SIZE, BORDER_SPRITE_SIZE, null);
		graphics2D.drawImage(bottomLeftCornerBorder, 0, height - BORDER_SPRITE_SIZE, BORDER_SPRITE_SIZE, BORDER_SPRITE_SIZE, null);
		graphics2D.drawImage(bottomRightCornerBorder, width - BORDER_SPRITE_SIZE, height - BORDER_SPRITE_SIZE, BORDER_SPRITE_SIZE, BORDER_SPRITE_SIZE, null);

		int horizontalBorderLength = width - (BORDER_SPRITE_SIZE * 2);
		int verticalBorderLength = height - (BORDER_SPRITE_SIZE * 2);

		// Draw horizontal border sprites
		graphics2D.drawImage(horizontalBorder, BORDER_SPRITE_SIZE, 0, horizontalBorderLength, BORDER_THICKNESS, null);
		graphics2D.drawImage(horizontalBorder, BORDER_SPRITE_SIZE, height - BORDER_THICKNESS, horizontalBorderLength, BORDER_THICKNESS, null);

		// Draw vertical border sprites
		graphics2D.drawImage(verticalBorder, 0, BORDER_SPRITE_SIZE, BORDER_THICKNESS, verticalBorderLength, null);
		graphics2D.drawImage(verticalBorder, width - BORDER_THICKNESS, BORDER_SPRITE_SIZE, BORDER_THICKNESS, verticalBorderLength, null);

		// Draw border intersections. These y-values depends on the star and are set by rebuildUiElements.
		renderBorderIntersection(firstBorderIntersectY, graphics2D);
		renderBorderIntersection(secondBorderIntersectY, graphics2D);
		renderBorderIntersection(thirdBorderIntersectY, graphics2D);
	}

	/**
	 * Renders a horizontal border intersection.
	 *
	 * @param y The y-coordinate at which to draw the horizontal border intersection.
	 * @param graphics2D The {@link Graphics2D} instance to draw to.
	 */
	private void renderBorderIntersection(int y, Graphics2D graphics2D)
	{
		int horizontalBorderLength = width - BORDER_LEFT_INTERSECT_WIDTH - BORDER_RIGHT_INTERSECT_WIDTH;

		graphics2D.drawImage(leftIntersectBorder, 0, y + BORDER_INTERSECT_VERTICAL_OFFSET, BORDER_LEFT_INTERSECT_WIDTH, BORDER_LEFT_INTERSECT_HEIGHT, null);
		graphics2D.drawImage(rightIntersectBorder, width - BORDER_RIGHT_INTERSECT_WIDTH, y + BORDER_INTERSECT_VERTICAL_OFFSET, BORDER_RIGHT_INTERSECT_WIDTH, BORDER_RIGHT_INTERSECT_HEIGHT, null);

		graphics2D.drawImage(horizontalBorder, BORDER_LEFT_INTERSECT_WIDTH, y, horizontalBorderLength, BORDER_THICKNESS, null);
	}

	/**
	 * Renders all UI elements for the overlay.
	 *
	 * @param graphics2D The {@link Graphics2D} instance to draw to.
	 */
	private void renderUiElements(Graphics2D graphics2D)
	{
		// Just skip rendering this frame in the rare event that the write lock is held.

		ConcurrentUtil.acquireLockAndRun(uiElementsLock.readLock(), true, () -> {
			for (final StarDetailsUIElement element : uiElements)
			{
				element.render(graphics2D);
			}
		});
	}

	/**
	 * Close this overlay and signal for it to be unregistered.
	 */
	public void closeOverlay()
	{
		visible = false;
		currentStarLocation = null;

		ConcurrentUtil.acquireLockAndRun(uiElementsLock.writeLock(), false, () -> uiElements.clear());

		eventBus.post(new SignalEvent(SignalEventType.WORLDMAP_STAR_DETAILS_CLOSED));
	}

	/**
	 * Builds all {@link StarDetailsUIElement} to display for a specific star
	 * and populates the {@link #uiElements} list.
	 *
	 * @param location The location name of the star.
	 * @return         Whether the overlay UI could be built.
	 */
	private boolean rebuildUiElements(String location)
	{
		final StarLocationDetails starLocationDetails = StarLocationDetails.getByName(location);

		if (starLocationDetails == null)
		{
			return false;
		}

		final List<StarDetailsUIElement> newUiElements = new ArrayList<>();
		StarDetailsUIElement uiElement;

		// Close button
		uiElement = new StarDetailsButton(
			width - BORDER_THICKNESS - CLOSE_BUTTON_SPRITE_SIZE,
			BORDER_THICKNESS,
			CLOSE_BUTTON_SPRITE_SIZE,
			CLOSE_BUTTON_SPRITE_SIZE,
			closeButton,
			closeButtonHover,
			"Close",
			this::closeOverlay
		);

		newUiElements.add(uiElement);

		// Star location text field
		uiElement = new StarDetailsTextField(
			location,
			BORDER_THICKNESS,
			BORDER_THICKNESS,
			width - CLOSE_BUTTON_SPRITE_SIZE - (BORDER_THICKNESS * 2),
			100,
			PADDING,
			FontManager.getRunescapeBoldFont(),
			JagexColors.DARK_ORANGE_INTERFACE_TEXT
		);

		firstBorderIntersectY = uiElement.getHeight() + BORDER_THICKNESS;
		newUiElements.add(uiElement);

		rebuildActiveStarSection(location, newUiElements);
		rebuildQuestSection(starLocationDetails, newUiElements);
		rebuildTransportSection(starLocationDetails, newUiElements);

		ConcurrentUtil.acquireLockAndRun(uiElementsLock.writeLock(), false, () -> uiElements = newUiElements);

		return true;
	}

	/**
	 * Builds all {@link StarDetailsActiveStarButton} elements to display in the
	 * active stars section and populates the ui element list with these.
	 *
	 * @param location      The location name of the star.
	 * @param newUiElements The list to add new UI elements to.
	 */
	private void rebuildActiveStarSection(String location, final List<StarDetailsUIElement> newUiElements)
	{
		int currentY = firstBorderIntersectY + BORDER_THICKNESS + PADDING.top;

		final StarListEntryAttributes[] starsToDisplay = announcementAttributes.values().stream()
			.filter(starAttributes ->
				starAttributes.getStar().getLocation().getName().equalsIgnoreCase(location) &&
				starAttributes.shouldBeVisible())
			.sorted(Comparator.comparing(StarListEntryAttributes::getTier, Comparator.reverseOrder()))
			.toArray(StarListEntryAttributes[]::new);

		for (int i = 0; i < starsToDisplay.length; i++)
		{
			final int column = i % 3;
			final int currentX = (BORDER_THICKNESS + PADDING.left) + (ACTIVE_STAR_BUTTON_WIDTH * column) + (2 * column);
			final World world = starsToDisplay[i].getWorld();

			if (column == 0 && i != 0)
			{
				currentY += ACTIVE_STAR_BUTTON_HEIGHT + PADDING.top;
			}

			final StarDetailsActiveStarButton button = new StarDetailsActiveStarButton(
				currentX,
				currentY,
				ACTIVE_STAR_BUTTON_WIDTH,
				ACTIVE_STAR_BUTTON_HEIGHT,
				activeStarButton,
				activeStarButtonHover,
				starsToDisplay[i],
				() -> hopToWorld(world)
			);

			newUiElements.add(button);
		}

		// No stars present at this location
		if (starsToDisplay.length == 0)
		{
			// Star location text field
			final StarDetailsTextField uiElement = new StarDetailsTextField(
				"There are currently no known stars at this location",
				BORDER_THICKNESS,
				currentY,
				width - CLOSE_BUTTON_SPRITE_SIZE - (BORDER_THICKNESS * 2),
				100,
				new Padding(0, 2, 2, 2),
				FontManager.getRunescapeSmallFont(),
				Color.RED
			);

			newUiElements.add(uiElement);

			secondBorderIntersectY = currentY + uiElement.getHeight();
		}
		else
		{
			secondBorderIntersectY = currentY + ACTIVE_STAR_BUTTON_HEIGHT + PADDING.top;
		}
	}

	/**
	 * Builds all {@link StarDetailsActiveStarButton} elements to display in the
	 * quest section and populates the ui element list with these.
	 *
	 * @param starLocationDetails The location name of the star.
	 * @param newUiElements       The list to add new UI elements to.
	 */
	private void rebuildQuestSection(StarLocationDetails starLocationDetails,
									 final List<StarDetailsUIElement> newUiElements)
	{
		int yPos = secondBorderIntersectY + BORDER_THICKNESS;

		final Quest quest = starLocationDetails.getQuestRequirement();

		String text = "No quest requirements";
		Color textColor = Color.GREEN;

		if (quest != null)
		{
			text = quest.getName();
			textColor = JagexColors.DARK_ORANGE_INTERFACE_TEXT;
		}

		final StarDetailsIconTextField iconTextField = new StarDetailsIconTextField(
			questIcon,
			text,
			BORDER_THICKNESS,
			yPos,
			width - (BORDER_THICKNESS * 2),
			100,
			PADDING,
			new Padding(2, 0, 2, 2),
			textColor
		);

		if (quest != null)
		{
			updateQuestStatusText(iconTextField, quest);
		}

		newUiElements.add(iconTextField);

		thirdBorderIntersectY = yPos + iconTextField.getHeight();
	}

	/**
	 * Builds all {@link StarDetailsActiveStarButton} elements to display in the
	 * transport section and populates the ui element list with these.
	 *
	 * @param starLocationDetails The location name of the star.
	 * @param newUiElements       The list to add new UI elements to.
	 */
	private void rebuildTransportSection(StarLocationDetails starLocationDetails,
										 final List<StarDetailsUIElement> newUiElements)
	{
		int yPos = thirdBorderIntersectY + BORDER_THICKNESS;

		for (StarLocationTransport transport : starLocationDetails.getTransportList())
		{
			final StarDetailsIconTextField iconTextField = new StarDetailsIconTextField(
				transport.getTransportType().getIcon(),
				transport.getTransportType().getName() + " - " + transport.getTransportName(),
				BORDER_THICKNESS,
				yPos,
				width - (BORDER_THICKNESS * 2),
				100,
				PADDING,
				new Padding(2, 0, 2, 2),
				JagexColors.DARK_ORANGE_INTERFACE_TEXT
			);

			newUiElements.add(iconTextField);

			yPos += iconTextField.getHeight();
		}

		lastElementBottomY = yPos;
	}

	/**
	 * Sets the text color of a {@link StarDetailsIconTextField} depending on the progression status of a quest.
	 *
	 * @param iconTextField The {@link StarDetailsIconTextField} set the text color of.
	 * @param quest			The quest status to check.
	 */
	private void updateQuestStatusText(final StarDetailsIconTextField iconTextField, final Quest quest)
	{
		if (!client.isClientThread())
		{
			clientThread.invoke(() -> updateQuestStatusText(iconTextField, quest));
			return;
		}

		Color textColor = Color.GREEN;

		switch (quest.getState(client))
		{
			case NOT_STARTED:
				textColor = Color.RED;
				break;
			case IN_PROGRESS:
				textColor = Color.YELLOW;
		}

		iconTextField.getTextField().setColor(textColor);
	}

	/**
	 * Checks if the world map is open.
	 * @return Whether the world map is open.
	 */
	private boolean isWorldMapClosed()
	{
		return client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER) == null;
	}

	/**
	 * Request a world hop.
	 *
	 * @param world The world to hop to.
	 */
	private void hopToWorld(World world)
	{
		if (world == null)
		{
			log.error("Unable to hop worlds. World is null.");
			return;
		}

		eventBus.post(new WorldHopRequest(world, null));
	}

	/**
	 * Check the dragging status of the overlay.
	 *
	 * @return Whether the overlay is being dragged.
	 */
	private boolean isDragging()
	{
		return mouseDragPrevious.x != Integer.MIN_VALUE && mouseDragPrevious.y != Integer.MIN_VALUE;
	}

	/**
	 * Check if dragging of the overlay can start at the current mouse position.
	 *
	 * @return Whether any {@link StarDetailsUIElement} is blocking dragging from starting.
	 */
	private boolean canStartDragging()
	{
		Optional<Boolean> result = ConcurrentUtil.acquireLockAndGet(uiElementsLock.readLock(), false, () -> {
			for (StarDetailsUIElement element : uiElements)
			{
				if (element.isHovering() && element.isBlockDragging())
				{
					return false;
				}
			}

			return true;
		});

		return result.isPresent() && result.get() == Boolean.TRUE;
	}

	/**
	 * Check if the world map is being resized.
	 *
	 * @return Whether the world map is currently being resized.
	 */
	private boolean isWorldMapResizing()
	{
		final Widget mapContainer = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
		final Widget resizePreview = client.getWidget(InterfaceID.Worldmap.RESIZE_PREVIEW);

		if (mapContainer == null || resizePreview == null)
		{
			return false;
		}

		return mapContainer.isHidden() && !resizePreview.isHidden();
	}

	/**
	 * Hides the world map tooltips. Saves the current
	 * {@link #WORLD_MAP_TOGGLES_VARBIT} value in {@link #savedTooltipVarbitState}.
	 */
	private void hideWorldMapTooltips()
	{
		if (!client.isClientThread())
		{
			clientThread.invoke(this::hideWorldMapTooltips);
			return;
		}

		savedTooltipVarbitState = client.getVarbitValue(WORLD_MAP_TOGGLES_VARBIT);

		client.setVarbit(WORLD_MAP_TOGGLES_VARBIT, savedTooltipVarbitState | TOOLTIP_TOGGLE_BITMASK);
	}

	/**
	 * Restores world map tooltips to the state held in {@link #savedTooltipVarbitState}.
	 */
	private void restoreWorldMapTooltips()
	{
		if (!client.isClientThread())
		{
			clientThread.invoke(this::restoreWorldMapTooltips);
			return;
		}

		if (savedTooltipVarbitState != Integer.MIN_VALUE)
		{
			client.setVarbit(WORLD_MAP_TOGGLES_VARBIT, savedTooltipVarbitState);
			savedTooltipVarbitState = Integer.MIN_VALUE;
		}
	}

	/**
	 * Loads all sprites used by this overlay from cache into their respective {@link BufferedImage}.
	 *
	 * @param spriteManager The {@link SpriteManager} instance to use when loading sprites.
	 */
	public void loadSprites(SpriteManager spriteManager)
	{
		// Background
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.BACKGROUND, 0, (sprite) -> background = sprite);

		// Close button
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.CLOSE_BUTTON, 0, (sprite) -> closeButton = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.CLOSE_BUTTON_HOVER, 0, (sprite) -> closeButtonHover = sprite);

		// Active star button
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.ACTIVE_STAR_BUTTON, 0, (sprite) -> activeStarButton = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.ACTIVE_STAR_BUTTON_HOVER, 0, (sprite) -> activeStarButtonHover = sprite);

		// Border sprites
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.VERTICAL_BORDER, 0, (sprite) -> verticalBorder = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.HORIZONTAL_BORDER, 0, (sprite) -> horizontalBorder = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.LEFT_INTERSECT_BORDER, 0, (sprite) -> leftIntersectBorder = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.RIGHT_INTERSECT_BORDER, 0, (sprite) -> rightIntersectBorder = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.TOP_LEFT_CORNER_BORDER, 0, (sprite) -> topLeftCornerBorder = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.TOP_RIGHT_CORNER_BORDER, 0, (sprite) -> topRightCornerBorder = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.BOTTOM_LEFT_CORNER_BORDER, 0, (sprite) -> bottomLeftCornerBorder = sprite);
		spriteManager.getSpriteAsync(SpriteConstants.WorldMapStarDetails.BOTTOM_RIGHT_CORNER_BORDER, 0, (sprite) -> bottomRightCornerBorder = sprite);

		// Other sprites
		spriteManager.getSpriteAsync(SpriteConstants.QUEST_ICON, 0, (sprite) -> questIcon = sprite);
	}

	/**
	 * Checks that this overlay is within the bounds of the {@link #mapViewWidget}
	 * widget. Moves the overlay to remain within the widget if needed.
	 */
	private void enforceBounds()
	{
		mapViewWidget = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);

		if (mapViewWidget == null)
		{
			return;
		}

		int mapWidth = mapViewWidget.getWidth();
		int mapHeight = mapViewWidget.getHeight();


		if (x < 0)
		{
			x = 0;
		}
		if (y < 0)
		{
			y = 0;
		}

		if (x + width > mapWidth)
		{
			x = mapWidth - width;
		}
		if (y + height > mapHeight)
		{
			y = mapHeight - height;
		}

		updateBounds();
	}

	/**
	 * Updates the overlay bounds to be relative to the {@link #mapViewWidget}.
	 */
	private void updateBounds()
	{
		if (mapViewWidget != null)
		{
			setBounds(new Rectangle(x + mapViewWidget.getCanvasLocation().getX(),
				y + mapViewWidget.getCanvasLocation().getY(), width, height));
		}
	}

	/**
	 * Update the position and dimensions of this overlay.
	 *
	 * @param x      X position relative to {@link #mapViewWidget}.
	 * @param y      Y position relative to {@link #mapViewWidget}.
	 * @param width  Width of the entire overlay.
	 * @param height Height of the entire overlay.
	 */
	private void updateBounds(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		updateBounds();
	}
	// </editor-fold>
}
