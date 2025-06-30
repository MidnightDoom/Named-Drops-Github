# Named Drops

A simple mod that automatically renames items dropped on death for players and renamed mobs in the style of "\<name>'s \<item>".

The item name will be translatable, so players in different languages will still see the item name in their language.

Already named items will not be renamed.

## Config
- split into default and any number of additional sections named with player UUID's
  - If a player has a config section matching their UUID, the "rename_rule" and "item_list" from that will be used over default
- item_list: a list of items, such as "minecraft:stone"
- rename_rule: one of:
  - "ALL": renames every item
  - "LIST_ONLY": only renames items if they are in "item_list"
  - "UNSTACKABLES_ONLY": only renames items if they are unstackable
  - "UNSTACKABLES_PLUS_LIST": renames items if they are unstackable or in "item_list"
- mob_rename_rule: same as rename_rule, but only used for mobs. Only needed in the default path
- rename_mob_loot: true or false. If true, all mob drops will be renamed following "mob_rename_rule". If false, only armor items, main hand and ofhand items are renamed with "mob_rename_rule"