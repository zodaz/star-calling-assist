# Star Calling Assist

This plugin allows for crashed stars found around the game to be posted to a remote endpont.

Posts can either be triggered manually by clicking the call-star button by the minimap when a star is found or
automatically when a star is found by the player. This is configured in the plugin settings.

Posts are made in the following format to the endpoint specified in the plugin settings:

```json
{
  "world": "world the star is in",
  "tier": "size of star (1-9)",
  "location": "location of star",
  "sender": "in-game name of caller or empty string",
  "miners": "Players around the star. -1 if too far away to render players"
}
```

Authorization header is set to what is specified in the plugin settings.

## Shortest Path integration

When the [Shortest Path](https://github.com/Skretzo/shortest-path) plugin is installed and enabled, this plugin can:

- Show the route to a star, using the "Route via shortest path" option when right-clicking a star in the side-panel.
- Automatically show the route to a star when you hop to its world from the side-panel.
- Clear the route when you arrive at the star, or when the star is depleted or missing.
- Display the estimated travel distance to each star location in the side-panel, and sort the stars by it.
  This requires a version of Shortest Path that answers `query` plugin messages.

All of the above can be configured in the "Shortest Path" section of the plugin settings.
