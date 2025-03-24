# WeCode
[Discord Link](https://discord.gg/8dg5hbPKC9)

## Plot Searching
- CTRL + F by default to search the entire plot for functions/processes/events

## Commands
- /exp \<expression>
  - good way to quickly write %math expressions
- /color \<hex>
  - runs either /i color hex \<hex> or /par color \<hex> depending on what you are holding
- /batchtag \<tag> \<value> \<tag2> \<value2> [...]
- /pjoin \<player>
  - Joins the plot that player is on
- /sjoin \<name>
- /speedjoin set \<name> \<id/handle
- /speedjoin list
- /smallcaps \<text
  - Gives you a qxeii'ied version of the input text
- /dev \<id> OR /dev \<handle>
- /build \<id> OR /build \<handle>
- /cb (explained later)
- /inv save AND /inv load
- /dtp \<args>
  - Runs /dev and then /ctp \<args>
- /test (don't run, does stuff for testing)

## Clipboards (/cb) (formerly known as Colorspaces /cs)
- /cb list
  - Lists your clipboards
- /cb create \<boardname>
  - Creates a new clipboard under the given name
- /cb delete \<boardname>
  - Deletes the given clipboard
- /cb add \<boardname> \<colorname> \<#color>
  - Adds a named value/color to a clipboard
- /cb remove \<boardname> \<colorname>
  - Removes a named value/color
- /cb setactive \<boardname>
  - Sets your active clipboard to the given board
- /cb export \<boardname>
  - Copies the given clipboard to your clipboard (haha!)
- /cb importclipboard \<boardname>
  - Imports a clipboard from your clipboard (and saves it under the given name)
```
When writing in chat (or running commands) every tag in the form of <<name>> gets
replaced with the corresponding color from your active colorspace (or from the "global" colorspace)

Example:
/cb create example
/cb add example test #FFFF88
/cb add example pi 3.141
/cb setactive example
/txt <<test>>Pi: <<pi>>!
^^ That command gives you a /txt <#FFFF88>Pi: 3.141!
```

## Keybinds
- Show Tags
- Pin Item Tooltip
- Pin Template Preview
  - There is a template peeker and this allows you to freeze it in place
- Flight Speed Toggle Normal \<==> Fast (Normal and Fast are defined in the config)

## Other
- Lagslayer Display in the top left
- Custom Notifications
  - Moves all DF errors and successes to a fancy display in the top right (can be toggled in config) 
