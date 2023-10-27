package com.starcallingassist.old;

import com.starcallingassist.StarCallingAssistPlugin;
import com.starcallingassist.modules.sidepanel.elements.Link;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

@Slf4j
public class InfoPanel extends JPanel
{
	private final StarCallingAssistPlugin plugin;
	private String errorMessage = "";

	public InfoPanel(StarCallingAssistPlugin plugin)
	{
		this.plugin = plugin;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.SCROLL_TRACK_COLOR);

		build();
	}

	public void rebuild()
	{
		removeAll();

		build();

		revalidate();
		repaint();
	}

	public void setErrorMessage(String errorMessage)
	{
		if (errorMessage.equals(this.errorMessage))
		{
			return;
		}

		this.errorMessage = errorMessage;
		rebuild();
	}

	private void build()
	{
		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new EmptyBorder(5, 5, 7, 5));
		JPanel bottom = new JPanel(new BorderLayout());

		if (plugin.getConfig().getAuthorization().isEmpty())
		{
			JLabel missingKeyDescription = new JLabel("<html>To see the list of active stars you need to enter your unique key into the " +
				"<b>Authorization</b> field in the plugin settings. <br><br> You can get your unique key from " +
				"the starminers discord.</html>"
			);

			JPanel missingKeySection = new JPanel();
			missingKeySection.setLayout(new BoxLayout(missingKeySection, BoxLayout.Y_AXIS));
			missingKeySection.setBorder(new EmptyBorder(5, 0, 5, 0));
			missingKeySection.setOpaque(false);
			missingKeySection.add(missingKeyDescription);
			top.add(missingKeySection, BorderLayout.NORTH);
		}

		Link discordAdvert = new Link("https://discord.gg/starminers", "Join the Star Miners discord!").center();
		top.add(discordAdvert, BorderLayout.SOUTH);

		top.setOpaque(false);
		add(top, BorderLayout.NORTH);

		if (!errorMessage.isEmpty())
		{
			JPanel errorPanel = new JPanel(new BorderLayout());
			JLabel errorInfo = new JLabel("<html>Error when fetching list of stars: <br><br>" + errorMessage + "</html>");
			errorInfo.setForeground(Color.RED);
			errorInfo.setBorder(new EmptyBorder(10, 3, 5, 3));
			errorPanel.add(errorInfo);

			errorPanel.setOpaque(false);

			bottom.add(errorPanel, BorderLayout.NORTH);

			bottom.setOpaque(false);
			add(bottom, BorderLayout.SOUTH);
		}
	}
}
