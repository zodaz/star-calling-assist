package com.starcallingassist.modules.sidepanel.panels;

import com.starcallingassist.constants.PluginColors;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.modules.sidepanel.decorators.StarListGroupDecorator;
import com.starcallingassist.modules.sidepanel.decorators.StarListGroupEntryDecorator;
import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.FontManager;

public class StarListGroupEntryPanel extends JPanel
{
	@Getter
	private final StarListEntryAttributes attributes;
	private final StarListGroupEntryDecorator entry;

	@Setter
	private StarListGroupDecorator group;
	private final JPanel mainContentPanel;

	public StarListGroupEntryPanel(StarListEntryAttributes attributes, StarListGroupEntryDecorator entry)
	{
		this.attributes = attributes;
		this.entry = entry;

		setOpaque(false);
		setLayout(new BorderLayout());

		mainContentPanel = createMainContentPanel();
		add(mainContentPanel);
	}

	private JPanel createMainContentPanel()
	{
		JPanel mainContentPanel = new JPanel(new BorderLayout());
		mainContentPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		mainContentPanel.setBackground(attributes.getDeadTime() < 0
			? PluginColors.STAR_LIST_GROUP_ENTRY_BACKGROUND_EXPIRED
			: PluginColors.STAR_LIST_GROUP_ENTRY_BACKGROUND
		);

		mainContentPanel.add(createWestPanel(), BorderLayout.WEST);
		mainContentPanel.add(createCenterPanel(), BorderLayout.CENTER);

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton() == MouseEvent.BUTTON1 && mouseEvent.getClickCount() == 2)
				{
					entry.onWorldHopRequest(new WorldHopRequest(attributes.getWorld()));
				}
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					group.onMousePressed();
					mainContentPanel.setBackground(mainContentPanel.getBackground().brighter());
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					group.onMouseReleased();
					mainContentPanel.setBackground(mainContentPanel.getBackground().darker());
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				group.onMouseEntered();
				mainContentPanel.setBackground(mainContentPanel.getBackground().brighter());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				group.onMouseExited();
				mainContentPanel.setBackground(mainContentPanel.getBackground().darker());
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
		worldPanel.setBackground(PluginColors.STAR_LIST_GROUP_ENTRY_WORLBOX_BACKGROUND);
		worldPanel.setHorizontalAlignment(SwingConstants.CENTER);
		worldPanel.setVerticalAlignment(SwingConstants.CENTER);
		worldPanel.setFont(FontManager.getRunescapeSmallFont());
		worldPanel.setToolTipText(attributes.getWorldType());
		worldPanel.setForeground(attributes.getWorldColor());

		if ((entry.showFoundByColumn() && entry.showDeadTimeColumn()) ||
			(entry.showFoundByColumn() && entry.showWorldTypeColumn())
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

		if (entry.showTierColumn())
		{
			if (entry.showFoundByColumn() || entry.showDeadTimeColumn() || entry.showWorldTypeColumn())
			{
				topLine.add(createTierColumn(SwingConstants.LEFT), BorderLayout.WEST);
			}
			else
			{
				topLine.add(createTierColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
		}

		if (entry.showFoundByColumn())
		{
			topLine.add(createFoundByColumn(SwingConstants.RIGHT), BorderLayout.EAST);
		}

		if (entry.showDeadTimeColumn())
		{
			if (entry.showTierColumn() && topLine.getComponentCount() == 1)
			{
				topLine.add(createDeadTimeColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
			else
			{
				bottomLine.add(createDeadTimeColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
		}

		if (entry.showWorldTypeColumn())
		{
			if (entry.showTierColumn() && topLine.getComponentCount() == 1)
			{
				topLine.add(createWorldTypeColumn(SwingConstants.RIGHT), BorderLayout.EAST);
			}
			else if (entry.showDeadTimeColumn() && bottomLine.getComponentCount() == 1)
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

	private JLabel createFoundByColumn(int textPosition)
	{
		return createColumnLabel(
			"Found by " + Optional.ofNullable(attributes.getFoundBy()).orElse("you"),
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
		columnLabel.setForeground(PluginColors.STAR_LIST_GROUP_LABEL);
		columnLabel.setText(text);

		return columnLabel;
	}

	public String getGroupingTitle()
	{
		return attributes.getStar().getLocation().getName();
	}
}
