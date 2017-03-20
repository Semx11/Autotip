## General Information
This Forge mod automatically tips all active Network Boosters and other Autotip users on the Hypixel Network ([hypixel.net](https://hypixel.net)), allowing the user to collect coins, karma, and experience (earnable currencies) over time. At its most effective, a user may leave the mod on at all possible times in order to maximize profit. The mod also includes detailed tracking features in order to visualize earnings and modify the messages that tipping causes for an simpler user experience. 

**Downloads and more information can be found at [autotip.pro](https://autotip.pro).**


## Processes
Autotip runs in waves of 15 minutes (viewable by the ingame command '''/autotip wave'''), starting at the first login to the Hypixel Network of the Minecraft session. At every pulse, a request will be made to a endpoint on our server side API with the client's username, which first fetches an '''all''' in order to tip online boosters, and then a list of random other online autotip users, plus random gamemodes for each user in the format '''Username GameMode'''. 

The command '''/tip ''' is hardcoded into the client, and the server only returns the list of what should be suffixed after that command. This is in order to appease any fears that people with access to the Autotip backend may commit malicious acts on the basis that they have remote access to a player's commands. Any person with such fears can have the peace of mind that this is impossible with how the client acts on the response.


## Server Side Components
Many of the components that make Autotip work take place on the autotip.pro servers, the primary being the mechanic of tipping other online players. Upon the first login to Hypixel, a player will be authenticated with our server side API with basic information, including their Minecraft UUID, Minecraft version, Autotip version, operating system. These are both used in both analytics of the mod and authentication with our online player system. A number of checks will be run on the player in order to prevent spoofing. After authenticated, a request will be made to our API every 15 minutes in order to fetch the tip queue, as discribed in Processes above. The API code is not currently avalible in order to not reveal our authentication methods, and more importantly to not reveal the embarrasingly written code that lies there.

# Contributors
- [Semx11](https://hypixel.net/members/semx11.20123) - Developer of mod, developer of API, PR. 
- [2Pi](https://hypixel.net/members/2pi.22108) - Developer of API, developer of mod from April 2016 to August 2016, PR.
- [Sk1er](https://hypixel.net/members/sk1er.199731) - Host, PR.
