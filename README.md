# WoodFood

## Projekt bemutatása:

A WoodFood egy recept kezelő alkalmazás, ahol lehetőség van a már adatbázisban tárolt receptek

- lekorlátozott listázására
- részleteinek megtekintésére
- mentésére (update művelet)
- törlésére

A receptek megjelenítésekor a mentett receptek kerülnek az oldal legtetejére. A mentett recepteket a receptkönyvre kattintva (fölső menüsor) tudjuk megtekinteni, valamint törölni tudjuk őket a mentett receptek közül.

Új recept létrehozására is van lehetőség, ami mentés után egy default képpel az adatbázisba kerül.

## Pontozási szempontokban szereplő, a kódban nehezebben megtalálható részek:

- Felhasznált animációk:

  - SavedAdapter: 113. sor: blink animation
  - SavedAdapter: 42. sor: sample animation

- Lifecycle Hook:

  - MainActivity.java: 123. sor: onPause
  - RecipeListActivity.java: 223. sor: onDestroy
  - SavedRecipesActivity.java: 171. sor: onDestroy

- Legalább egy notification vagy alam manager vagy job scheduler használata:

  - Notification megvalósítása: NotificationHandler.java
    - recept mentésekor, valamint a mentet receptek eltávolításakor küld üzenetet

- Widget:
  - Bár a feladat nem kérte, widgetet is készítettem, amelyet a telefonotok képernyőjének szerkesztésénél értek el, widget fül alatt (WoodFood néven)

## Megjegyzés a készítőtől:

- Az alkalmazást, amennyiben lehetséges, világos módban teszteljétek, a színek arra vannak optimalizálva
- A receptek és az ételekről készült fotók a saját konyhámból kerültek ki (kivétel esetén az eredeti recept elérhetőségét csatoltam), használjátok őket bátran! :)
- Borító és default kép forrása: Bitmoji
