package com.mauriciogiordano.fooplayer.network;

/**
 * Created by mauricio on 10/31/14.
 */
public class Endpoints
{
    public static class LastFM
    {
        public static String HOST = "ws.audioscrobbler.com";
        public static String PATH = "2.0";
    }

    public static class Youtube
    {
        public static String HOST = "www.googleapis.com";
        public static String PATH = "youtube/v3/search";
    }

    public static class YoutubeRTSP
    {
        public static String HOST = "gdata.youtube.com";
        public static String PATH = "feeds/api/videos/";
    }
}
