package cs.ualberta.ca.tunein;

import java.util.ArrayList;
import java.util.Date;

public class Comment {
	
	private Commenter commenter;
	private String title;
	private String comment;
	private CommentList replies;
	private Image img;
	private Date date;
	private GeoLocation geolocation;
	private boolean hasImage;
	
	//constructor for creating a comment with no picture
	public Comment(Commenter user, String aTitle, String aComment, GeoLocation loc) 
	{
		
		this.commenter = user;
		this.title = aTitle;
		this.comment = aComment;
		this.geolocation = loc;
		this.date = new Date();
		this.replies = new CommentList();
		this.hasImage = false;
	}

	//constructor for creating comment with a picture
	public Comment(Commenter user, String aTitle, String aComment, GeoLocation loc, Image aImage) 
	{
		
		this.commenter = user;
		this.title = aTitle;
		this.comment = aComment;
		this.geolocation = loc;
		this.img = aImage;
		this.date = new Date();
		this.replies = new ArrayList<Comment>();
		this.hasImage = true;
	}


	public Commenter getCommenter() 
	{
		return commenter;
	}


	public void setCommenter(Commenter commenter) 
	{
		this.commenter = commenter;
	}


	public String getTitle() 
	{
		return title;
	}


	public void setTitle(String title) 
	{
		this.title = title;
	}


	public String getComment() 
	{
		return comment;
	}


	public void setComment(String comment) 
	{
		this.comment = comment;
	}


	public ArrayList<Comment> getReplies() 
	{
		return replies;
	}


	public void setReplies(ArrayList<Comment> replies) 
	{
		this.replies = replies;
	}


	public Image getImg() 
	{
		return img;
	}


	public void setImg(Image img) 
	{
		this.img = img;
	}


	public Date getDate() 
	{
		return date;
	}


	public void setDate(Date date) 
	{
		this.date = date;
	}


	public GeoLocation getGeolocation() 
	{
		return geolocation;
	}


	public void setGeolocation(GeoLocation geolocation) 
	{
		this.geolocation = geolocation;
	}

	
}