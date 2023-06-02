# Star Calling Assist
This plugin allows for crashed stars found around the game to be posted to a remote endpont.

Posts can either be triggered manually by clicking the call-star button by the minimap when a star is found or automatically when a star is found by the player. This is configured in the plugin settings.

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