package org.magnum.dataup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.magnum.dataup.model.Video;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//This is the controller class that will serve the incoming requests based on the incoming URL.
@Controller
public class VideoSvc {

	private Map<Long,Video> videos = new HashMap<Long,Video>();
	List<Video> vList = new ArrayList<Video>();
	private static final AtomicLong currentId = new AtomicLong(0L);
//get videos
	
	@RequestMapping(value="/video",method =RequestMethod.GET)
	public @ResponseBody List<Video> getVideos(){
	System.out.println(videos.size());
//	if(videos.size()==0)
//		return null;
//	else
//	return (List<Video>) videos.values();
	return vList;
}
	
//post video
	@RequestMapping(value="/video",method = RequestMethod.POST)
	public @ResponseBody Video postVideo(@RequestBody Video v){
		
		//set the id of the video
		checkAndSetId(v);
		//set the url of the video
		v.setDataUrl(getDataUrl(v.getId()));
		//return the video
		vList.add(v);
		videos.put(v.getId(),v);
		return v;
	}
//get video based on id
	
//post video based on id
	
	@RequestMapping(value="/video/{id}/data",method=RequestMethod.POST)
	
	
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
