package com.starcallingassist.modules.sidepanel.panels;

import com.starcallingassist.constants.PluginColors;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.modules.sidepanel.decorators.StarListPanelDecorator;
import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import lombok.Getter;
import net.runelite.client.ui.FontManager;

public class StarListEntryPanel extends JPanel
{
	@Getter
	private final StarListEntryAttributes attributes;
	private final StarListPanelDecorator decorator;
	private final JPanel titlePanel;
	private final JPanel mainContentPanel;

	public StarListEntryPanel(StarListEntryAttributes attributes, StarListPanelDecorator decorator)
	{
		this.attributes = attributes;
		this.decorator = decorator;

		setOpaque(false);
		setLayout(new BorderLayout());

		titlePanel = createTitlePanel();
		mainContentPanel = createMainContentPanel();

		setPanelColors(PluginColors.ENTRY_PANEL_BACKGROUND, PluginColors.ENTRY_PANEL_BORDER);

		add(titlePanel, BorderLayout.NORTH);
		add(mainContentPanel, BorderLayout.SOUTH);
	}

	private void setPanelColors(Color backgroundColor, Color borderColor)
	{
		if (attributes.isCurrentWorld())
		{
			borderColor = PluginColors.ENTRY_PANEL_BORDER_CURRENT_WORLD;
		}

		setBorder(new MatteBorder(2, 2, 2, 2, borderColor));
		titlePanel.setBackground(borderColor);
		titlePanel.setBorder(new MatteBorder(0, 1, 0, 0, borderColor));

		mainContentPanel.setBackground(backgroundColor);
	}

	private JPanel createTitlePanel()
	{
		JPanel titlePanel = new JPanel(new BorderLayout());

		JLabel locationLabel = createLocationColumn();
		titlePanel.add(locationLabel, BorderLayout.WEST);

		return titlePanel;
	}

	private JPanel createMainContentPanel()
	{
		JPanel mainContentPanel = new JPanel(new BorderLayout());
		mainContentPanel.setBackground(PluginColors.ENTRY_PANEL_BACKGROUND);
		mainContentPanel.setBorder(new EmptyBorder(2, 2, 2, 2));

		mainContentPanel.add(createWestPanel(), BorderLayout.WEST);
		mainContentPanel.add(createCenterPanel(), BorderLayout.CENTER);

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton() == MouseEvent.BUTTON1 && mouseEvent.getClickCount() == 2)
				{
					decorator.onWorldHopRequest(new WorldHopRequest(attributes.getWorld()));
				}
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					setPanelColors(mainContentPanel.getBackground().brighter(), titlePanel.getBackground().brighter());
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					setPanelColors(mainContentPanel.getBackground().darker(), titlePanel.getBackground().darker());
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				setPanelColors(PluginColors.ENTRY_PANEL_BACKGROUND_HOVER, PluginColors.ENTRY_PANEL_BORDER_HOVER);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				setPanelColors(PluginColors.ENTRY_PANEL_BACKGROUND, PluginColors.ENTRY_PANEL_BORDER);
			}
		});


		return mainContentPanel;
	}

	private JPanel createWestPanel()
	{
		JPanel westPanel = new JPanel(new BorderLayout());
		westPanel.setOpaque(false);

		JLabel worldPanel = new JLabel();
		worldPanel.setLayout(new BorderLayout());
		worldPanel.setOpaque(true);
		worldPanel.setBackground(PluginColors.ENTRY_PANEL_WORLDBOX_BACKGROUND);
		worldPanel.setHorizontalAlignment(SwingConstants.CENTER);
		worldPanel.setVerticalAlignment(SwingConstants.CENTER);
		worldPanel.setFont(FontManager.getRunescapeSmallFont());
		worldPanel.setToolTipText(attributes.getWorldType());
		worldPanel.setForeground(attributes.getWorldColor());

		if ((decorator.showFoundByColumn() && decorator.showDeadTimeColumn()) ||
			(decorator.showFoundByColumn() && decorator.showWorldTypeColumn())
		)
		{
			worldPanel.setText("<html><center>World<br>" + attributes.getWorld().getId() + "</center></html>");
			worldPanel.setPreferredSize(new Dimension(45, 30));
		}
		else
		{
			worldPanel.setText("<html><center>World " + attributes.getWorld().getId() + "</center></html>");
			worldPanel.setPreferredSize(new Dimension(60, 20));
		}

		westPanel.add(worldPanel, BorderLayout.WEST);
		westPanel.setBorder(new EmptyBorder(0, 0, 0, 2));

		return westPanel;
	}

	private JPanel createCenterPanel()
	{
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		centerPanel.setBorder(new EmptyBorder(2, 2, 2, 2));

		JPanel topLine = new JPanel(new BorderLayout());
		topLine.setOpaque(false);

		JPanel bottomLine = new JPanel(new BorderLayout());
		bottomLine.setOpaque(false);

		if (decorator.showTierColumn())
		{
			if (decorator.showFoundByColumn() || decorator.showDeadTimeColumn() || decorator.showWorldTypeColumn())
			{
				topLine.add(createTierColumn(SwingConstants.LEFT), BorderLayout.WEST);
			}
			else
			{
				topLine.add(createTierColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
		}

		if (decorator.showFoundByColumn())
		{
			topLine.add(createFoundByColumn(SwingConstants.RIGHT), BorderLayout.EAST);
		}

		if (decorator.showDeadTimeColumn())
		{
			if (decorator.showTierColumn() && topLine.getComponentCount() == 1)
			{
				topLine.add(createDeadTimeColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
			else
			{
				bottomLine.add(createDeadTimeColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
		}

		if (decorator.showWorldTypeColumn())
		{
			if (decorator.showTierColumn() && topLine.getComponentCount() == 1)
			{
				topLine.add(createWorldTypeColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
			else if (decorator.showDeadTimeColumn() && bottomLine.getComponentCount() == 1)
			{
				bottomLine.add(createWorldTypeColumn(SwingConstants.LEFT), BorderLayout.WEST);
			}
			else
			{
				bottomLine.add(createWorldTypeColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
		}

		if (topLine.getComponentCount() == 0)
		{
			centerPanel.add(bottomLine, BorderLayout.CENTER);
		}
		else if (bottomLine.getComponentCount() == 0)
		{
			centerPanel.add(topLine, BorderLayout.CENTER);
		}
		else if (topLine.getComponentCount() != 0 && bottomLine.getComponentCount() != 0)
		{
			centerPanel.add(topLine, BorderLayout.NORTH);
			centerPanel.add(bottomLine, BorderLayout.SOUTH);
		}

		return centerPanel;
	}

	private JLabel createLocationColumn()
	{
		JLabel locationLabel = new JLabel(attributes.getStar().getLocation().getName(), SwingConstants.CENTER);
		locationLabel.setFont(FontManager.getRunescapeSmallFont());
		locationLabel.setForeground(attributes.getAreaColor());

		return locationLabel;
	}

	private JLabel createFoundByColumn(int textPosition)
	{
		return createColumnLabel(
			"Found by " + attributes.getPlayerName(),
			textPosition
		);
	}

	private JLabel createWorldTypeColumn(int textPosition)
	{
		JLabel column = createColumnLabel(
			Optional.ofNullable(attributes.getWorldType()).orElse("-"),
			textPosition
		);

		column.setForeground(
			Optional.ofNullable(attributes.getWorldLimitationColor()).orElse(PluginColors.ENTRY_IRRELEVANT_AREA)
		);

		return column;
	}

	private JLabel createDeadTimeColumn(int textPosition)
	{
		if (attributes.getDeadTime() < 0)
		{
			return createColumnLabel(
				String.format("<html>Overdue <font color=\"" + PluginColors.STAR_DEAD + "\">%s min</font>.</html>", Math.abs(attributes.getDeadTime())),
				textPosition
			);
		}

		String text = String.format(
			"<html>Dead in ~<font color=\"%s\">" + attributes.getDeadTime() + " min.</font>",
			attributes.getDeadTime() <= 20 ? PluginColors.STAR_EXPIRING : PluginColors.STAR_HEALTHY
		);

		return createColumnLabel(text, textPosition);
	}

	private JLabel createTierColumn(int textPosition)
	{
		return createColumnLabel(
			"Tier " + attributes.getTier(),
			textPosition
		);
	}

	private JLabel createColumnLabel(String text, int textPosition)
	{
		JLabel columnLabel = new JLabel();
		columnLabel.setOpaque(false);
		columnLabel.setLayout(new BorderLayout());
		columnLabel.setVerticalAlignment(SwingConstants.CENTER);
		columnLabel.setHorizontalTextPosition(textPosition);
		columnLabel.setFont(FontManager.getRunescapeSmallFont());
		columnLabel.setForeground(PluginColors.ENTRY_PANEL_LABEL);
		columnLabel.setText(text);

		return columnLabel;
	}
}
