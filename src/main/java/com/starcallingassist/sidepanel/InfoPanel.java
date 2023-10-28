package com.starcallingassist.sidepanel;

import com.starcallingassist.StarCallingAssistPlugin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
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
		JPanel bottom = new JPanel(new BorderLayout());

		JPanel discordAdvert = new JPanel(new BorderLayout());

		JLabel discordLink = createClickableLink("https://discord.gg/starminers", "Join the Star Miners discord!");
		discordLink.setHorizontalAlignment(SwingConstants.CENTER);

		discordAdvert.add(discordLink, BorderLayout.CENTER);
		discordAdvert.setOpaque(false);

		top.add(discordAdvert, BorderLayout.NORTH);

		if (plugin.getConfig().getAuthorization().isEmpty())
		{
			JPanel missingKeyInfo = new JPanel(new BorderLayout());
			JLabel keyInfo = new JLabel(
				"<html>To see the list of active stars you need to enter your unique key into the " +
					"<b>Authorization</b> field in the plugin settings. <br><br> You can get your unique key from " +
					"the starminers discord: <br><br> discord.gg/starminers</html>"
			);
			keyInfo.setBorder(new EmptyBorder(10, 3, 5, 3));
			missingKeyInfo.add(keyInfo);

			missingKeyInfo.setOpaque(false);

			top.add(missingKeyInfo, BorderLayout.SOUTH);
		}

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

	private JLabel createClickableLink(final String url, String text)
	{
		JLabel linkLabel = new JLabel("<html><a href=''>" + text + "</a></html>");
		linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		linkLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI(url));
				}
				catch (Exception ex)
				{
					log.error(ex.getMessage());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				linkLabel.setText("<html><u><font color='orange'>" + text + "</font></u></html>");
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				linkLabel.setText("<html><a href=''>" + text + "</a></html>");
			}
		});

		return linkLabel;
	}
}
