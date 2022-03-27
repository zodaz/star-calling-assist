# Star Calling Assist
This plugin allows for crashed stars found around the game to be posted to a remote endpont for crowdsourcing purposes.
###Using
Posts can either be triggered manually by clicking the call-star button by the minimap when a star is found or automatically when a star is found by the player. This is configured in the plugin settings.
###Posting
Posts are made in the following format to the endpoint specified in the plugin settings:
```json
{
  "world": "world the star is in",
  "tier": "size of star (1-9)",
  "location": "location of star",
  "sender": "in-game name of caller"
}
```