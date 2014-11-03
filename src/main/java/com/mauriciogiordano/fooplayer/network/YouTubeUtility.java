package com.mauriciogiordano.fooplayer.network;

import android.util.Log;

import com.keyes.youtube.VideoStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mauricio on 11/2/14.
 */
public class YouTubeUtility {

    /**
     * Calculate the YouTube URL to load the video.  Includes retrieving a token that YouTube
     * requires to play the video.
     *
     * @param pYouTubeFmtQuality quality of the video.  17=low, 18=high
     * @param pFallback whether to fallback to lower quality in case the supplied quality is not available
     * @param pYouTubeVideoId the id of the video
     * @return the url string that will retrieve the video
     * @throws IOException
     * @throws ClientProtocolException
     * @throws UnsupportedEncodingException
     */
    public static String calculateYouTubeUrl(String pYouTubeFmtQuality, boolean pFallback,
                                             String pYouTubeVideoId) throws IOException,
            ClientProtocolException, UnsupportedEncodingException {

        String lUriStr = null;
        HttpClient lClient = new DefaultHttpClient();

        HttpGet lGetMethod = new HttpGet("http://www.youtube.com/get_video_info?&video_id=" +
                pYouTubeVideoId);

        HttpResponse lResp = null;

        lResp = lClient.execute(lGetMethod);

        ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
        String lInfoStr = null;

        lResp.getEntity().writeTo(lBOS);
        lInfoStr = new String(lBOS.toString("UTF-8"));

        Log.d("INFOSTREAM", lInfoStr);

        String[] lArgs=lInfoStr.split("&");
        Map<String,String> lArgMap = new HashMap<String, String>();
        for(int i=0; i<lArgs.length; i++){
            String[] lArgValStrArr = lArgs[i].split("=");
            if(lArgValStrArr != null){
                if(lArgValStrArr.length >= 2){
                    lArgMap.put(lArgValStrArr[0], URLDecoder.decode(lArgValStrArr[1]));
                }
            }
        }

        //Find out the URI string from the parameters

        //Populate the list of formats for the video
        String lFmtList = URLDecoder.decode(lArgMap.get("fmt_list"));
        ArrayList<Format> lFormats = new ArrayList<Format>();
        if(null != lFmtList){
            String lFormatStrs[] = lFmtList.split(",");

            for(String lFormatStr : lFormatStrs){
                Format lFormat = new Format(lFormatStr);
                lFormats.add(lFormat);
            }
        }

        //Populate the list of streams for the video
        String lStreamList = lArgMap.get("url_encoded_fmt_stream_map");
        if(null != lStreamList){
            String lStreamStrs[] = lStreamList.split(",");
            ArrayList<VideoStream> lStreams = new ArrayList<VideoStream>();
            for(String lStreamStr : lStreamStrs){
                VideoStream lStream = new VideoStream(lStreamStr);
                lStreams.add(lStream);
            }

            //Search for the given format in the list of video formats
            // if it is there, select the corresponding stream
            // otherwise if fallback is requested, check for next lower format
            int lFormatId = Integer.parseInt(pYouTubeFmtQuality);

            Format lSearchFormat = new Format(lFormatId);
            while(!lFormats.contains(lSearchFormat) && pFallback ){
                int lOldId = lSearchFormat.getId();
                int lNewId = getSupportedFallbackId(lOldId);

                if(lOldId == lNewId){
                    break;
                }
                lSearchFormat = new Format(lNewId);
            }

            int lIndex = lFormats.indexOf(lSearchFormat);
            if(lIndex >= 0){
                VideoStream lSearchStream = lStreams.get(lIndex);
                lUriStr = lSearchStream.getUrl();
            }

        }
        //Return the URI string. It may be null if the format (or a fallback format if enabled)
        // is not found in the list of formats for the video
        return lUriStr;
    }

    public static int getSupportedFallbackId(int pOldId){
        final int lSupportedFormatIds[] = {13,  //3GPP (MPEG-4 encoded) Low quality
                17,  //3GPP (MPEG-4 encoded) Medium quality
                18,  //MP4  (H.264 encoded) Normal quality
                22,  //MP4  (H.264 encoded) High quality
                37   //MP4  (H.264 encoded) High quality
        };
        int lFallbackId = pOldId;
        for(int i = lSupportedFormatIds.length - 1; i >= 0; i--){
            if(pOldId == lSupportedFormatIds[i] && i > 0){
                lFallbackId = lSupportedFormatIds[i-1];
            }
        }
        return lFallbackId;
    }


    /**
     * Represents a format in the "fmt_list" parameter
     * Currently, only id is used
     *
     */
    public static class Format {

        protected int mId;

        /**
         * Construct this object from one of the strings in the "fmt_list" parameter
         * @param pFormatString one of the comma separated strings in the "fmt_list" parameter
         */
        public Format(String pFormatString){
            String lFormatVars[] = pFormatString.split("/");
            mId = Integer.parseInt(lFormatVars[0]);
        }
        /**
         * Construct this object using a format id
         * @param pId id of this format
         */
        public Format(int pId){
            this.mId = pId;
        }

        /**
         * Retrieve the id of this format
         * @return the id
         */
        public int getId(){
            return mId;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object pObject) {
            if(!(pObject instanceof Format)){
                return false;
            }
            return ((Format)pObject).mId == mId;
        }
    }

}
