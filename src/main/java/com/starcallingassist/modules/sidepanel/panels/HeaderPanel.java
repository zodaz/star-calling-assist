package com.starcallingassist.modules.sidepanel.panels;

import com.starcallingassist.constants.PluginColors;
import com.starcallingassist.modules.sidepanel.decorators.HeaderPanelDecorator;
import com.starcallingassist.modules.sidepanel.elements.Link;
import com.starcallingassist.modules.sidepanel.enums.OrderBy;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.ui.components.TitleCaseListCellRenderer;

public class HeaderPanel extends JPanel
{
	private final HeaderPanelDecorator decorator;

	private String errorMessage = "";

	private final JPanel sortingPanel = new JPanel(new BorderLayout());
	private final JComboBox<String> dropdown;
	private final JTextField locationFilterField;

	public HeaderPanel(HeaderPanelDecorator decorator)
	{
		this.decorator = decorator;

		setLayout(new BorderLayout());
		setBackground(PluginColors.PRIMARY_BACKGROUND);
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 2, 0, PluginColors.HEADER_BOTTOM_BORDER),
			BorderFactory.createEmptyBorder(3, 5, 1, 5)
		));

		dropdown = createSortingDropdown();
		locationFilterField = createLocationFilterField();

		sortingPanel.setOpaque(false);
		sortingPanel.add(dropdown, BorderLayout.NORTH);
		sortingPanel.add(Box.createVerticalStrut(4), BorderLayout.CENTER);
		sortingPanel.add(locationFilterField, BorderLayout.SOUTH);
		sortingPanel.setBorder(new EmptyBorder(0, 0, 3, 0));
	}

	private JComboBox<String> createSortingDropdown()
	{
		JComboBox<String> dropdown = new JComboBox();
		dropdown.setBackground(PluginColors.DROPDOWN_BACKGROUND);
		dropdown.setForeground(PluginColors.DROPDOWN_ARROW);
		dropdown.setFocusable(false);
		dropdown.setRenderer(new TitleCaseListCellRenderer());
		dropdown.setToolTipText("Change the sorting order of the star list");
		dropdown.addItemListener(event ->
		{
			if (event.getStateChange() == ItemEvent.SELECTED)
			{
				decorator.onSortingChanged(OrderBy.fromString((String) event.getItem()));
			}
		});

		for (OrderBy orderBy : OrderBy.values())
		{
			dropdown.addItem(orderBy.getName());
		}

		return dropdown;
	}

	private JTextField createLocationFilterField()
	{
		JTextField textField = new JTextField();
		textField.setToolTipText("Filter stars by location, world, or tier");
		textField.setPreferredSize(new Dimension(0, 24));
		textField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent event)
			{
				decorator.onLocationFilterChanged(textField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent event)
			{
				decorator.onLocationFilterChanged(textField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent event)
			{
				decorator.onLocationFilterChanged(textField.getText());
			}
		});

		return textField;
	}

	public void startUp()
	{
		dropdown.setSelectedItem(decorator.getOrderBy().getName());
		locationFilterField.setText(decorator.getLocationFilter());

		rebuild();
	}

	public void rebuild()
	{
		removeAll();
		add(createMainContentPanel(), BorderLayout.NORTH);

		if (decorator.hasAuthorization())
		{
			add(sortingPanel, BorderLayout.CENTER);
		}
	}

	private JPanel createMainContentPanel()
	{
		JPanel mainContent = new JPanel(new BorderLayout());
		mainContent.setOpaque(false);

		mainContent.add(createDiscordAdPanel(), BorderLayout.NORTH);
		mainContent.add(createInfoPanel(), BorderLayout.CENTER);

		return mainContent;
	}

	private JPanel createDiscordAdPanel()
	{
		JPanel adPanel = new JPanel(new BorderLayout());
		adPanel.setOpaque(false);
		adPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

		adPanel.add(new Link("https://discord.gg/starminers", "Join the Star Miners Discord!").center());

		return adPanel;
	}

	private JPanel createInfoPanel()
	{
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.setOpaque(false);

		if (decorator.hasAuthorization() && !errorMessage.isEmpty())
		{
			JLabel errorInfo = new JLabel("<html>Error when fetching list of stars: <br><br>" + errorMessage + "</html>");
			errorInfo.setForeground(PluginColors.DANGEROUS_AREA);
			errorInfo.setBorder(new EmptyBorder(0, 0, 5, 0));

			infoPanel.add(errorInfo, BorderLayout.CENTER);
		}

		return infoPanel;
	}


	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
		rebuild();
	}
}
