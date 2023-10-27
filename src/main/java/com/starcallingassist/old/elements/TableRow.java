/*
 * Copyright (c) 2021, Cyborger1, Psikoi <https://github.com/Psikoi>, Andmcadams https://github.com/andmcadams (Basis)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.starcallingassist.old.elements;

import com.starcallingassist.StarCallingAssistPlugin;
import com.starcallingassist.modules.sidepanel.SidePanelModule;
import com.starcallingassist.old.SidePanel;
import com.starcallingassist.old.objects.StarData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;

@Slf4j
public class TableRow extends JPanel
{
	private SidePanelModule module;

	@Getter
	private StarData data;

	@Getter
	@Setter
	private boolean rowVisible;

	private Color lastBackground;

	private StarCallingAssistPlugin plugin;

	// bubble up events
	private final MouseAdapter labelMouseListener = new MouseAdapter()
	{
		@Override
		public void mouseClicked(MouseEvent mouseEvent)
		{
			dispatchEvent(mouseEvent);
		}

		@Override
		public void mousePressed(MouseEvent mouseEvent)
		{
			dispatchEvent(mouseEvent);
		}

		@Override
		public void mouseReleased(MouseEvent mouseEvent)
		{
			dispatchEvent(mouseEvent);
		}

		@Override
		public void mouseEntered(MouseEvent mouseEvent)
		{
			dispatchEvent(mouseEvent);
		}

		@Override
		public void mouseExited(MouseEvent mouseEvent)
		{
			dispatchEvent(mouseEvent);
		}
	};

	public TableRow(StarData data, StarCallingAssistPlugin plugin, SidePanelModule module)
	{
		this.data = data;
		this.plugin = plugin;
		this.module = module;

		setRowVisible(true);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(2, 0, 2, 0));

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton() == MouseEvent.BUTTON1 && mouseEvent.getClickCount() == 2)
				{
					module.queueWorldHop(data.getWorldId());
				}
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					setBackground(getBackground().brighter());
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					setBackground(getBackground().darker());
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				TableRow.this.lastBackground = getBackground();

				setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				setBackground(lastBackground);
			}
		});

		JPanel row = new JPanel(new BorderLayout());
		JPanel leftSide = new JPanel(new BorderLayout());
		JPanel center = new JPanel(new BorderLayout());
		JPanel rightSide = new JPanel(new BorderLayout());

		row.setOpaque(false);
		leftSide.setOpaque(false);
		center.setOpaque(false);
		rightSide.setOpaque(false);

		leftSide.add(buildWorldField(), BorderLayout.WEST);
		leftSide.add(buildTierField(), BorderLayout.CENTER);
		center.add(buildLocationField(), BorderLayout.CENTER);
		rightSide.add(buildDeadTimeField(), BorderLayout.CENTER);
		rightSide.add(buildFoundByField(), BorderLayout.EAST);

		row.add(leftSide, BorderLayout.WEST);
		row.add(center, BorderLayout.CENTER);
		row.add(rightSide, BorderLayout.EAST);

		add(row);
	}

	private JPanel buildWorldField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setOpaque(false);

		Color foreground = null;

		if (data.getWorldId() == module.getClient().getWorld())
		{
			foreground = Color.GREEN;
		}
		else if (data.getWorldTypeSpecifier().equals("PVP"))
		{
			foreground = Color.RED;
		}
		else if (data.isP2p())
		{
			foreground = Color.ORANGE;
		}


		JPanel worldField = buildMultiLineTextField(
			(data.getWorldId() + " " + data.getWorldTypeSpecifier()).trim(),
			3,
			foreground
		);

		worldField.setBorder(new EmptyBorder(0, 2, 0, 0));
		worldField.setPreferredSize(new Dimension(SidePanel.WORLD_COLUMN_WIDTH, 30));

		column.add(worldField);
		column.addMouseListener(labelMouseListener);

		return column;
	}

	private JPanel buildTierField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setOpaque(false);

		JLabel tierField = new JLabel(String.valueOf(data.getTier(plugin.getConfig().estimateTier())), SwingConstants.CENTER);
		tierField.setFont(FontManager.getRunescapeSmallFont());
		tierField.setPreferredSize(new Dimension(SidePanel.TIER_COLUMN_WIDTH, 30));

		column.add(tierField);
		column.addMouseListener(labelMouseListener);

		return column;
	}

	private JPanel buildLocationField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setOpaque(false);

		JPanel locationField = buildMultiLineTextField(data.getLocation(), 16, data.isWilderness() ? Color.RED : null);
		locationField.setBorder(new EmptyBorder(0, 2, 0, 2));

		column.add(locationField);
		column.addMouseListener(labelMouseListener);

		return column;
	}

	private JPanel buildDeadTimeField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setOpaque(false);

		JLabel deadTimeField = new JLabel(data.getDeadTime() + "m");
		deadTimeField.setForeground(data.getDeadTime() <= 0 ? Color.RED : data.getDeadTime() <= 20 ? Color.YELLOW : Color.GREEN);
		deadTimeField.setHorizontalAlignment(SwingConstants.CENTER);
		deadTimeField.setFont(FontManager.getRunescapeSmallFont());

		deadTimeField.setPreferredSize(new Dimension(SidePanel.DEAD_TIME_COLUMN_WIDTH, 30));

		column.add(deadTimeField);
		column.addMouseListener(labelMouseListener);

		return column;
	}

	private JPanel buildFoundByField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setOpaque(false);

		JPanel foundByField = buildMultiLineTextField(fitUsername(data.getFoundBy(), 8), 8, null);

		foundByField.setPreferredSize(new Dimension(SidePanel.FOUND_BY_COLUMN_WIDTH, 30));

		column.add(foundByField);

		column.addMouseListener(labelMouseListener);

		return column;
	}

	/**
	 * Creates a JPanel containing one or more JLabels based on input.
	 *
	 * @param text       Text to divide into lines.
	 * @param charLimit  Maximum length of each line.
	 * @param foreground Color of the text.
	 * @return A JPanel containing one or more JLabels.
	 */
	private JPanel buildMultiLineTextField(String text, int charLimit, Color foreground)
	{
		JPanel column = new JPanel(new DynamicGridLayout(3, 1));

		List<String> lines = getLines(text, charLimit);

		for (int i = 0; i < lines.size(); i++)
		{
			if (i >= 3)
			{
				break;
			}

			JLabel label = new JLabel(lines.get(i));
			label.setFont(FontManager.getRunescapeSmallFont());
			if (foreground != null)
			{
				label.setForeground(foreground);
			}

			column.add(label);
		}

		column.setOpaque(false);

		return column;
	}

	/**
	 * Divides a string into a list of lines based on the lineLength parameter.
	 *
	 * @param text       Text to divide into lines.
	 * @param lineLength Maximum length of each line.
	 * @return A list of String:s where all strings are less than lineLength in length.
	 */
	private List<String> getLines(String text, int lineLength)
	{
		List<String> lines = new ArrayList<>();

		String currentLine = "";
		for (String s : text.split(" "))
		{
			if (s.length() + currentLine.length() + 1 > lineLength)
			{
				if (currentLine.isEmpty())
				{
					lines.add(s);
				}
				else
				{
					lines.add(currentLine);
					currentLine = s;
				}
			}
			else
			{
				currentLine += currentLine.isEmpty() ? s : (" " + s);
			}
		}

		if (!currentLine.isEmpty())
		{
			lines.add(currentLine);
		}

		return lines;
	}

	/**
	 * Used as a workaround to make usernames word-wrap in the foundBy JLabel if they lack blankspaces
	 *
	 * @param username Username to process.
	 * @return A username altered with blankspaces to fit the foundBy JLabel.
	 */
	private String fitUsername(String username, int charLimit)
	{
		if (username.length() > charLimit)
		{
			String[] arr = username.split(" ");

			for (int i = 0; i < arr.length; i++)
			{
				if (arr[i].length() > charLimit - 1)
				{
					arr[i] = arr[i].substring(0, charLimit - 1) + " " + arr[i].substring(charLimit - 1);
				}
			}

			username = String.join(" ", arr);
		}

		return username;
	}

	@Value
	@AllArgsConstructor
	private static class StringBool
	{
		String string;
		boolean boolValue;
	}
}