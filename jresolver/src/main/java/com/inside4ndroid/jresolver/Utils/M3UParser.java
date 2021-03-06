package com.inside4ndroid.jresolver.Utils;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3UParser {

    private static final String EXT_M3U = "#EXTM3U";
    private static final String EXT_INF = "#EXT";
    private static final String EXT_PLAYLIST_NAME = "#PLAYLIST";
    private static final String EXT_LOGO = "tvg-logo";
    private static final String EXT_URL = "http";

    private String convertStreamToString(InputStream is) {
        try {
            return new Scanner(is).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public M3UPlaylist parseFile(String stream) throws FileNotFoundException {
        stream = stream.replace("#EXTM3U", "");
        stream = stream.replace("\n", "").replace("\r", "");
        stream = stream.replace(" ", "");
        M3UPlaylist m3UPlaylist = new M3UPlaylist();
        ArrayList<M3UItem> playlistItems = new ArrayList<>();
        String[] linesArray = stream.split("#EXT-X");
        for (String currLine : linesArray) {

            M3UItem playlistItem = new M3UItem();

            String[] dataArray = currLine.split(",");
            try {
                String url = dataArray[dataArray.length-1].substring(dataArray[dataArray.length-1].indexOf(EXT_URL));
                if(url.contains("iframes")){
                    continue;
                } else {
                    Matcher b = Pattern.compile("RESOLUTION.*x(.*?),").matcher(Arrays.toString(dataArray));
                    if(b.find()){
                        playlistItem.setItemName(b.group(1)+"p");
                    }
                    playlistItem.setItemUrl(url);

                }

            } catch (Exception A){
                continue;
            }

                playlistItems.add(playlistItem);

        }
        m3UPlaylist.setPlaylistItems(playlistItems);
        return m3UPlaylist;
    }
}
