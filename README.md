# WeCode
[Discord Link](https://discord.gg/8dg5hbPKC9)

## Commands
- /batchtag \<tag> \<value> \<tag2> \<value2> [...]
- /pjoin \<player>
  - Joins the plot that player is on
- /smallcaps \<text
  - Gives you a qxeii'ied version of the input text
- /dev \<id> OR /dev \<handle>
- /build \<id> OR /build \<handle>
- /cs (explained later)
- /test (don't run, does stuff for testing)

## Colorspaces (/cs)
- /cs list
  - Lists your colorspaces
- /cs create \<spacename>
  - Creates a new colorspace under the given name
- /cs delete \<spacename>
  - Deletes the given colorspace
- /cs add \<spacename> \<colorname> \<#color>
  - Adds a named color to a colorspace
- /cs remove \<spacename> \<colorname>
  - Removes a named color
- /cs setactive \<spacename>
  - Sets your active colorspace to the given space
- /cs export \<spacename>
  - Copies the given colorspace to your clipboard
- /cs importclipboard \<spacename>
  - Imports a colorspace from your clipboard (and saves it under the given name)
```
When writing in chat (or running commands) every tag in the form of <<name>> gets
replaced with the corresponding color from your active colorspace (or from the "global" colorspace)

Example:
/cs create example
/cs add example test #FFFF88
/cs setactive example
/txt <<test>>Woahie!
^^ That command gives you a /txt <#FFFF88>Woahie!
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