package fr.ubx.poo.ubgarden.game.launcher;

public class MapRepoString implements MapRepo {
    final char EOL = 'x';

    @Override
    public MapLevel load(String string) {

        int width = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == EOL) {
                break;
            } else {
                width++;
            }
        }
        int height = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == EOL) {
                height++;
            }
        }
        MapLevel mapLevel = new MapLevel(width,height);
        int indexString = 0;
        char c;
        for(int j=0; j<height ; j++){
            for(int i=0; i<width+1 ; i++) {
                c = string.charAt(indexString);
                if (c != EOL) {
                    MapEntity mapEntity = MapEntity.fromCode(c);
                    mapLevel.set(i,j,mapEntity);
                }
                indexString++;
            }
        }
        return mapLevel;
    }}