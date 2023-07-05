/*
 * Copyright (c) 2018, Matthew Steglinski <https://github.com/sainttx>
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
package net.runelite.client.plugins.devtools;

import com.simplicity.client.InteractableObject;
import com.simplicity.client.Tile;
import com.simplicity.client.TileObject;
import com.simplicity.client.cache.definitions.ObjectDefinition;
import net.runelite.api.Client;
import net.runelite.api.GraphicsBufferType;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;

/**
 * Tile Debug - Displays information about currently hovered tile.
 *
 * @author Blake
 */
public class TileDebugOverlay extends OverlayPanel
{
	private final Client client;
	private final DevToolsPlugin plugin;

	@Inject
    TileDebugOverlay(Client client, DevToolsPlugin plugin)
	{
		this.client = client;
		this.plugin = plugin;
		setPosition(OverlayPosition.TOP_LEFT);
		setGraphicsBuffer(GraphicsBufferType.MAIN_GAME);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.getTileDebug().isActive())
		{
			return null;
		}

		Tile tile = client.getSelectedSceneTile();

		if (tile != null) {
			panelComponent.getChildren().add(TitleComponent.builder()
					.color(Color.CYAN)
					.text("Tile")
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("X")
					.right("" + tile.getWorldLocation().getX())
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("Y")
					.right("" + tile.getWorldLocation().getY())
					.build());

			panelComponent.getChildren().add(LineComponent.builder()
					.left("Z")
					.right("" + tile.getWorldLocation().getPlane())
					.build());

			if (tile.interactableObjects != null) {
				boolean has = false;

				for (InteractableObject object : tile.interactableObjects) {
					if (object == null) {
						continue;
					}

					if (!has) {
						panelComponent.getChildren().add(LineComponent.builder()
								.leftColor(Color.YELLOW)
								.left("GameObjects")
								.build());
						has = true;
					}

					panelComponent.getChildren().add(LineComponent.builder()
							.left("    ID")
							.right("" + object.getId())
							.build());
					addInfo(object);
				}
			}

			if (tile.getDecorativeObject() != null) {
				panelComponent.getChildren().add(LineComponent.builder()
						.leftColor(Color.YELLOW)
						.left("Decor:")
						.right("" + tile.getDecorativeObject().getId())
						.build());

				addInfo(tile.getDecorativeObject());
			}

			if (tile.getWallObject() != null) {
				panelComponent.getChildren().add(LineComponent.builder()
						.leftColor(Color.YELLOW)
						.left("Wall:")
						.right("" + tile.getWallObject().getId())
						.build());
				addInfo(tile.getWallObject());
			}

			if (tile.getGroundObject() != null) {
				panelComponent.getChildren().add(LineComponent.builder()
						.leftColor(Color.YELLOW)
						.left("Ground:")
						.right("" + tile.getGroundObject().getId())
						.build());
				addInfo(tile.getGroundObject());
			}
		}

		return super.render(graphics);
	}

	private void addInfo(TileObject object) {
		final ObjectDefinition def = ObjectDefinition.forID(object.getId());

		if (def == null) {
			return;
		}

		if (def.name != null && !def.name.isEmpty()) {
			panelComponent.getChildren().add(LineComponent.builder()
					.left("    Name")
					.right(def.name)
					.build());
		}

		panelComponent.getChildren().add(LineComponent.builder()
				.left("    Unwalkable")
				.right("" + def.isUnwalkable)
				.build());

		panelComponent.getChildren().add(LineComponent.builder()
				.left("    Models")
				.right(Arrays.toString(def.objectModelIDs))
				.build());

		if (def.objectModelTypes != null) {
			panelComponent.getChildren().add(LineComponent.builder()
					.left("    Types")
					.right(Arrays.toString(def.objectModelTypes))
					.build());
		}
	}
}
