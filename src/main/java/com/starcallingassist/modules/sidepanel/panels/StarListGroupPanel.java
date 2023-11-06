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

		updatePanelColor(PluginColors.STAR_LIST_GROUP_BORDER);
	}

	public void updatePanelColor(Color borderColor)
	{
		if (hasCurrentWorldEntry)
		{
			borderColor = PluginColors.STAR_LIST_GROUP_BORDER_CURRENT_WORLD;
		}

		setBorder(new MatteBorder(2, 2, 2, 2, borderColor));
		titleLabel.setBackground(borderColor);
		titleLabel.setBorder(new MatteBorder(0, 1, 0, 0, borderColor));
	}


	private void updateTitleColor(Color color)
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
				updatePanelColor(titleLabel.getBackground().brighter());
			}

			@Override
			public void onMouseReleased()
			{
				updatePanelColor(titleLabel.getBackground().darker());
			}

			@Override
			public void onMouseEntered()
			{
				updatePanelColor(PluginColors.STAR_LIST_GROUP_BORDER_HOVER);
			}

			@Override
			public void onMouseExited()
			{
				updatePanelColor(PluginColors.STAR_LIST_GROUP_BORDER);
			}
		});

		updateGroupState(entry);
		innerPanel.add(entry);

		updatePanelColor(PluginColors.STAR_LIST_GROUP_BORDER);
		updateTitleColor(PluginColors.STAR_LIST_GROUP_LABEL);
	}


	private void updateGroupState(StarListGroupEntryPanel entry)
	{
		if (!hasCurrentWorldEntry)
		{
			hasCurrentWorldEntry = entry.getAttributes().isCurrentWorld();
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
