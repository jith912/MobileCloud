package org.magnum.dataup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import retrofit.http.Multipart;

//This is the controller class that will serve the incoming requests based on the incoming URL.
@Controller
public class VideoSvc {

	List<Video> vList = new ArrayList<Video>();
	private static final AtomicLong currentId = new AtomicLong(0L);
//get videos
	
	@RequestMapping(value="/video",method =RequestMethod.GET)
	public @ResponseBody List<Video> getVideos(){
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
		return v;
	}
//get video based on id
	
	@RequestMapping(value="/video/{id}/data",method=RequestMethod.GET)
	public @ResponseBody void getVideoData(@PathVariable("id") long id,HttpServletResponse response) throws Exception{
		Iterator itr = vList.iterator();
		boolean videoFound=false;
		Video compVideo=null;
		while(itr.hasNext()){
			compVideo =(Video)itr.next();
			if(compVideo.getId()==id){
				videoFound =true;
				break;
			}
		}
		//match video here
		if(videoFound && VideoFileManager.get().hasVideoData(compVideo)){
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			VideoFileManager.get().copyVideoData(compVideo, response.getOutputStream());
			response.flushBuffer();
		}else{
			response.setStatus(HttpStatus.NOT_FOUND.value());
			response.flushBuffer();
		}
	}
	
//post video based on id
// URL:/video/{id}/data
	@RequestMapping(value="/video/{id}/data",method=RequestMethod.POST)
	@Multipart
	public @ResponseBody VideoStatus setVideoData(@PathVariable("id") long id, @RequestPart("data") MultipartFile videoData,HttpServletResponse response) throws Exception{
		Iterator itr = vList.iterator();
		boolean videoFound=false;
		Video compVideo=null;
		while(itr.hasNext()){
			compVideo =(Video)itr.next();
			if(compVideo.getId()==id){
				videoFound =true;
				VideoFileManager.get().saveVideoData(compVideo, videoData.getInputStream());
				break;
			}
		}
		if(!videoFound){
			response.setStatus(HttpStatus.NOT_FOUND.value());
			response.flushBuffer();
			return null;
		}
		return new VideoStatus(VideoState.READY);
	}
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
     
     @ExceptionHandler(Exception.class)
     @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
     public void handleAllException(Exception ex,HttpServletResponse response){
    	 ex.printStackTrace();
     }
}
