package org.magnum.dataup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.magnum.dataup.model.Video;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//This is the controller class that will serve the incoming requests based on the incoming URL.
@Controller
public class VideoSvc {

	private Map<Long,Video> videos = new HashMap<Long,Video>();
	private static final AtomicLong currentId = new AtomicLong(0L);
//get videos
	
	@RequestMapping(value="/video",method =RequestMethod.GET)
	public @ResponseBody List<Video> getVideos(){
	return (List<Video>) videos.values();
}
	
//post video
	public @ResponseBody Video postVideo(Video v){
		
		//set the id of the video
		checkAndSetId(v);
		//set the url of the video
		v.setDataUrl(getDataUrl(v.getId()));
		//return the video
		return v;
	}
//get video based on id
	
//post video based on id
	
//method to generate the video id
	
	private void checkAndSetId(Video entity){
		
		if(entity.getId()==0L){
			entity.setId(currentId.incrementAndGet());
		}
	}
	
	 private String getDataUrl(long videoId){
         String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
         return url;
     }

     private String getUrlBaseForLocalServer() {
        HttpServletRequest request = 
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base = 
           "http://"+request.getServerName() 
           + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
        return base;
     }
}
