package com.starcallingassist.modules.sidepanel.panels;

import com.starcallingassist.constants.PluginColors;
import com.starcallingassist.modules.sidepanel.decorators.StarListGroupDecorator;
import com.starcallingassist.modules.sidepanel.enums.OrderBy;
import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import lombok.Getter;
import net.runelite.client.ui.FontManager;

public class StarListGroupPanel extends JPanel
{
	private final JLabel titleLabel = new JLabel();
	private final JLabel distanceLabel = new JLabel();
	private final JPanel innerPanel;

	@Getter
	private final String title;
	private final OrderBy orderByColumn;
	private final boolean isSortAscending;
	private boolean hasCurrentWorldEntry = false;
	private boolean hasCurrentLocationEntry = false;
	private boolean isUnverified = false;
	private boolean isDangerousArea = false;
	private final List<StarListGroupEntryPanel> entries = new ArrayList<>();

	public StarListGroupPanel(String title, OrderBy orderByColumn, boolean isSortAscending)
	{
		this.title = title;
		this.orderByColumn = orderByColumn;
		this.isSortAscending = isSortAscending;

		setOpaque(false);
		setLayout(new BorderLayout());

		titleLabel.setOpaque(true);
		titleLabel.setFont(FontManager.getRunescapeSmallFont());
		titleLabel.setForeground(PluginColors.STAR_LIST_GROUP_LABEL);
		titleLabel.setText(title);

		distanceLabel.setOpaque(true);
		distanceLabel.setFont(FontManager.getRunescapeSmallFont());
		distanceLabel.setForeground(PluginColors.STAR_LIST_GROUP_LABEL);
		distanceLabel.setToolTipText("Estimated travel distance in tiles, according to the Shortest Path plugin");

		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		titlePanel.add(distanceLabel, BorderLayout.EAST);
		add(titlePanel, BorderLayout.NORTH);

		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		innerPanel.setBackground(PluginColors.STAR_LIST_GROUP_ENTRY_BACKGROUND);
		innerPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(innerPanel, BorderLayout.SOUTH);

		setPanelDefaultColor();
	}

	private Color getCurrentPanelColor()
	{
		return titleLabel.getBackground();
	}

	private void setPanelColor(Color color)
	{
		setBorder(new MatteBorder(2, 2, 2, 2, color));
		titleLabel.setBackground(color);
		titleLabel.setBorder(new MatteBorder(0, 1, 0, 0, color));
		distanceLabel.setBackground(color);
		distanceLabel.setBorder(new MatteBorder(0, 4, 0, 1, color));
	}

	private void setPanelHoverColor()
	{
		if (hasCurrentWorldEntry || hasCurrentLocationEntry)
		{
			setPanelColor(getCurrentPanelColor().brighter());
			return;
		}

		setPanelColor(PluginColors.STAR_LIST_GROUP_BORDER_HOVER);
	}

	private void setPanelDefaultColor()
	{
		if (hasCurrentWorldEntry)
		{
			setPanelColor(PluginColors.STAR_LIST_GROUP_BORDER_CURRENT_WORLD);
			return;
		}

		if (hasCurrentLocationEntry)
		{
			setPanelColor(PluginColors.STAR_LIST_GROUP_BORDER_CURRENT_LOCATION);
			return;
		}

		setPanelColor(PluginColors.STAR_LIST_GROUP_BORDER);
	}

	private void setTitleColor(Color color)
	{
		if (isDangerousArea)
		{
			color = PluginColors.DANGEROUS_AREA;
		}

		if (isUnverified)
		{
			color = PluginColors.UNVERIFIED_AREA;
		}

		titleLabel.setForeground(color);
	}

	public void addEntry(StarListGroupEntryPanel entry)
	{
		entry.setGroup(new StarListGroupDecorator()
		{
			@Override
			public void onMousePressed()
			{
				setPanelColor(titleLabel.getBackground().brighter());
			}

			@Override
			public void onMouseReleased()
			{
				setPanelColor(getCurrentPanelColor().darker());
			}

			@Override
			public void onMouseEntered()
			{
				setPanelHoverColor();
			}

			@Override
			public void onMouseExited()
			{
				setPanelDefaultColor();
			}
		});

		updateGroupState(entry);
		entries.add(entry);

		setPanelDefaultColor();
		setTitleColor(PluginColors.STAR_LIST_GROUP_LABEL);
	}

	private void updateGroupState(StarListGroupEntryPanel entry)
	{
		if (!hasCurrentWorldEntry)
		{
			hasCurrentWorldEntry = entry.getAttributes().isCurrentWorld();
		}

		if (!hasCurrentLocationEntry)
		{
			hasCurrentLocationEntry = entry.getAttributes().isCurrentLocation();
		}

		if (!isUnverified)
		{
			isUnverified = entry.getAttributes().isUnverified();
		}

		if (!isDangerousArea)
		{
			isDangerousArea = entry.getAttributes().isDangerousArea();
		}

		Integer travelDistance = entry.getAttributes().getTravelDistance();
		if (travelDistance != null)
		{
			distanceLabel.setText("~" + travelDistance);

			// The title gives way to the distance, rather than making the group wider than the side-panel.
			titleLabel.setPreferredSize(null);
			titleLabel.setPreferredSize(new Dimension(0, titleLabel.getPreferredSize().height));
			titleLabel.setToolTipText(title);
		}
	}

	public void commit()
	{

		entries.sort(this::sorter);

		for (StarListGroupEntryPanel entry : entries)
		{
			innerPanel.add(entry);
		}
	}

	private int sorter(StarListGroupEntryPanel panelA, StarListGroupEntryPanel panelB)
	{
		StarListEntryAttributes a1 = panelA.getAttributes();
		StarListEntryAttributes a2 = panelB.getAttributes();

		// We only want to re-sort the location order within the location group, because
		// the entries are already added in a pre-sorted order by the StarListPanel.
		if (orderByColumn == OrderBy.LOCATION || orderByColumn == OrderBy.DISTANCE)
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
