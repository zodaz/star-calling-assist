package com.starcallingassist.modules.sidepanel.panels;

import com.starcallingassist.constants.PluginColors;
import com.starcallingassist.modules.sidepanel.decorators.StarListGroupDecorator;
import java.awt.BorderLayout;
import java.awt.Color;
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
	private final JPanel innerPanel;

	@Getter
	private final String title;
	private boolean hasCurrentWorldEntry = false;
	private boolean hasCurrentLocationEntry = false;
	private boolean isUnverified = false;
	private boolean isDangerousArea = false;

	public StarListGroupPanel(String title)
	{
		this.title = title;

		setOpaque(false);
		setLayout(new BorderLayout());

		titleLabel.setOpaque(true);
		titleLabel.setFont(FontManager.getRunescapeSmallFont());
		titleLabel.setForeground(PluginColors.STAR_LIST_GROUP_LABEL);
		titleLabel.setText(title);
		add(titleLabel, BorderLayout.NORTH);

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
		innerPanel.add(entry);

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
	}
}
