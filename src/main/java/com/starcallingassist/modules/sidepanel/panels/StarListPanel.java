package com.starcallingassist.modules.sidepanel.panels;

import com.starcallingassist.constants.PluginColors;
import com.starcallingassist.modules.sidepanel.decorators.StarListGroupEntryDecorator;
import com.starcallingassist.modules.sidepanel.enums.OrderBy;
import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;
import com.starcallingassist.objects.Star;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.http.api.worlds.World;

public class StarListPanel extends JPanel
{
	@Setter
	private OrderBy orderByColumn = OrderBy.TIER;

	@Getter
	@Setter
	private boolean isSortAscending = false;

	private static final String EMPTY_PANEL = "EMPTY_PANEL";
	private static final String STAR_PANEL = "STAR_PANEL";
	public final CardLayout cardLayout = new CardLayout();
	private final PluginErrorPanel noStarsPanel = new PluginErrorPanel();
	private final JPanel starPanel = new JPanel();
	public final JPanel starPanelContainer = new JPanel(cardLayout);
	private final StarListGroupEntryDecorator decorator;

	public final ConcurrentHashMap<Integer, StarListEntryAttributes> announcementAttributes = new ConcurrentHashMap<>();

	public StarListPanel(StarListGroupEntryDecorator decorator)
	{
		super(false);
		this.decorator = decorator;

		setLayout(new BorderLayout());
		setBackground(PluginColors.SCROLLBOX_BACKGROUND);

		starPanel.setLayout(new BoxLayout(starPanel, BoxLayout.Y_AXIS));
		starPanel.setBorder((new EmptyBorder(0, 3, 0, 4)));
		starPanel.setBackground(PluginColors.SCROLLBOX_BACKGROUND);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(PluginColors.SCROLLBOX_BACKGROUND);
		wrapper.add(starPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(wrapper);
		scrollPane.setBackground(PluginColors.SCROLLBOX_BACKGROUND);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(2, 0));
		starPanelContainer.add(scrollPane, STAR_PANEL);

		JPanel welcomeWrapper = new JPanel(new BorderLayout());
		welcomeWrapper.setBackground(PluginColors.SCROLLBOX_BACKGROUND);
		welcomeWrapper.add(noStarsPanel, BorderLayout.NORTH);
		starPanelContainer.add(welcomeWrapper, EMPTY_PANEL);

		add(starPanelContainer, BorderLayout.CENTER);
	}

	public void onStarUpdate(@Nonnull Star star, @Nonnull World world, long updatedAt)
	{
		announcementAttributes.put(world.getId(), new StarListEntryAttributes(star, world, updatedAt, decorator));

		SwingUtilities.invokeLater(this::rebuild);
	}

	public void startUp()
	{
		rebuild();
	}

	public void rebuild()
	{
		SwingUtilities.invokeLater(() ->
		{
			rebuildNoStarsPanel();

			List<StarListGroupEntryPanel> tableEntries = new ArrayList<>();
			for (StarListEntryAttributes announcementAttribute : announcementAttributes.values())
			{
				if (!announcementAttribute.shouldBeVisible())
				{
					continue;
				}

				tableEntries.add(new StarListGroupEntryPanel(announcementAttribute, decorator));
			}

			if (tableEntries.isEmpty() || !decorator.hasAuthorization())
			{
				cardLayout.show(starPanelContainer, EMPTY_PANEL);
				return;
			}

			cardLayout.show(starPanelContainer, STAR_PANEL);
			tableEntries.sort(this::sorter);

			starPanel.removeAll();
			starPanel.add(Box.createVerticalStrut(4));

			StarListGroupPanel group = null;
			for (StarListGroupEntryPanel entry : tableEntries)
			{
				if (group != null && entry.getGroupingTitle().equals(group.getTitle()))
				{
					group.addEntry(entry);
					continue;
				}

				if (group != null)
				{
					group.commit();
					starPanel.add(group);
					starPanel.add(Box.createVerticalStrut(4));
				}

				group = new StarListGroupPanel(entry.getGroupingTitle(), orderByColumn, isSortAscending);
				group.addEntry(entry);
			}

			if (group != null)
			{
				group.commit();
				starPanel.add(group);
				starPanel.add(Box.createVerticalStrut(4));
			}

			revalidate();
			repaint();
		});
	}

	private void rebuildNoStarsPanel()
	{
		if (decorator.hasAuthorization())
		{
			noStarsPanel.setContent("No stars available", "There currently aren't any stars to display.");
			return;
		}

		noStarsPanel.setContent(
			"Authorization Required",
			"To see the current list of active stars,<br>" +
				"you'll need to authorize the plugin first.<br>" +
				"<br>" +
				"You can do this by entering your unique key<br>" +
				"into the <b>Authorization</b> field in the settings.<br>" +
				"(\"Configuration\" tab -> \"Star Miners\")<br>" +
				"<br>" +
				"<br>" +
				"<u>Don't have an unique key yet?</u><br>" +
				"<br>" +
				"Don't worry. You can obtain one from the<br>" +
				"<b>Star Miners Discord</b> using the above link.<br>"
		);
	}

	private int sorter(StarListGroupEntryPanel panelA, StarListGroupEntryPanel panelB)
	{
		StarListEntryAttributes a1 = panelA.getAttributes();
		StarListEntryAttributes a2 = panelB.getAttributes();

		if (orderByColumn == OrderBy.WORLD)
		{
			int world1 = a1.getWorld().getId();
			int world2 = a2.getWorld().getId();

			return isSortAscending
				? Integer.compare(world2, world1)
				: Integer.compare(world1, world2);
		}

		if (orderByColumn == OrderBy.TIER)
		{
			int tier1 = a1.getTier();
			int tier2 = a2.getTier();

			return !isSortAscending
				? Integer.compare(tier2, tier1)
				: Integer.compare(tier1, tier2);
		}

		if (orderByColumn == OrderBy.LOCATION)
		{
			String location1 = a1.getStar().getLocation().getName();
			String location2 = a2.getStar().getLocation().getName();

			return isSortAscending
				? location2.compareTo(location1)
				: location1.compareTo(location2);
		}

		if (orderByColumn == OrderBy.DEAD_TIME)
		{
			int deadTime1 = a1.getDeadTime();
			int deadTime2 = a2.getDeadTime();

			return !isSortAscending
				? Integer.compare(deadTime2, deadTime1)
				: Integer.compare(deadTime1, deadTime2);
		}

		return 0;
	}
}
