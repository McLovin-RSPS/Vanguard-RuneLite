package com.client.definitions;

import com.client.Buffer;
import com.client.Client;
import com.client.FileArchive;
import com.client.Sprite;
import com.client.sign.Signlink;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public final class AreaDefinition {

    public static int totalAreas;
    public static AreaDefinition[] cache;
    private static int cacheIndex;
    private static Buffer area_data;
    private static int[] streamIndices;


    public int id;
    public int spriteId = -1;
    public int field3294 = -1;
    public String name = "";
    public int field3296 = -1;
    public int field3297 = -1;
    public String actions[];
    public int field3310 = -1;


    private AreaDefinition() {
        id = -1;
    }

    public static void clear() {
        streamIndices = null;
        cache = null;
        area_data = null;
    }

    public static void init(FileArchive archive) {

        area_data = new Buffer(archive.readFile("areas.dat"));
        Buffer stream = new Buffer(archive.readFile("areas.idx"));

        totalAreas = stream.readUShort();
        streamIndices = new int[totalAreas];
        int offset = 2;

        for (int _ctr = 0; _ctr < totalAreas; _ctr++) {
            streamIndices[_ctr] = offset;
            offset += stream.readUShort();
        }

        cache = new AreaDefinition[10];

        for (int _ctr = 0; _ctr < 10; _ctr++) {
            cache[_ctr] = new AreaDefinition();
        }
        // dumpObjectList();
        System.out.println("Loaded: " + totalAreas + " Areas");

    }
    public static void dumpObjectList() {
        for(int i = 0; i < totalAreas; i++) {
            AreaDefinition class5 = lookup(i);
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(Signlink.getCacheDirectory() + "/dumps/area.txt", true));
                if(class5.name!= null) {
                    bw.write("case "+i+";");
                    bw.newLine();
                    bw.write("type.spriteId "+class5.spriteId+";");
                    bw.newLine();
                    bw.write("type.field3294 "+class5.field3294+";");
                    bw.newLine();
                    bw.write("type.name "+class5.name+";");
                    bw.newLine();
                    bw.flush();
                    bw.close();
                }
            } catch (IOException ioe2) {
            }
        }
    }
    public static AreaDefinition lookup(int itemId) {

        for (int count = 0; count < 10; count++)
            if (cache[count].id == itemId)
                return cache[count];

        cacheIndex = (cacheIndex + 1) % 10;
        AreaDefinition itemDef = cache[cacheIndex];
        if (itemId > 0)
            area_data.currentPosition = streamIndices[itemId];
        itemDef.id = itemId;
        itemDef.readValues(area_data);
        switch(itemId){
            case 0:
                itemDef.spriteId = 0;
                itemDef.field3294 = -1;
                break;
            case 13:
                itemDef.spriteId = 13;
                itemDef.field3294 = -1;
                break;
        }
        return itemDef;
    }

    public void readValues(Buffer buffer) {
        do {
            int opCode = buffer.readUnsignedByte();
            if (opCode == 0)
                return;
            if (opCode == 1)
                spriteId = buffer.readInt();
            else if (opCode == 2)
                field3294 = buffer.readInt();
            else if (opCode == 3)
                name = buffer.readNewString();
            else if (opCode == 4)
                field3296 = buffer.readInt();
            else if (opCode == 5)
                field3297 = buffer.readInt();
            else if (opCode == 6)
                field3296 = buffer.readInt();
            else if (opCode >= 6 && opCode < 11) {
                if (actions  == null)
                    actions = new String[5];
                actions[opCode - 6] = buffer.readNewString();
            } else if (opCode == 12)
                field3310 = buffer.readInt();

        } while (true);
    }

}