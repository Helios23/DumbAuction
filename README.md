![DumbAuction Logo](http://home.turt2live.com/DumbAuction-BukkitDev-Logo.png)


Features
-------

Auctions. Integrates with vault. No other crap.


Commands
-------

`/auc start [amount] [startPrice] [increment] [time] ` - Starts an auction

`/auc info` - Shows current auction infor

`/auc showqueue` - Shows the active queue

`/auc cancel` - Cancels your auction

`/auc toggle` - Toggles auction spam being sent to your chat

`/auc reload` - Reloads the configuration


Permissions
------

`dumbauction.auction` - Permits /auction. Also allows /auc start

`dumbauction.admin` - Allows cancelling of auctions as an administrator. (/auc cancel on active auction, regardless of owner)

`dumbauction.admin` will also bypass limits (besides the minimum limits) and permit `/auc reload`.


Background
------

Nothing else works. Might as well write a small simple lightweight auction thingy.


Jenkins and Stuff
------

[Jenkins Job](http://ci.turt2live.com/job/DumbAuction/?)

[JavaDocs](http://ci.turt2live.com/job/DumbAuction/javadoc/?)

[BukkitDev](http://dev.bukkit.org/bukkit-plugins/dumbauction/)